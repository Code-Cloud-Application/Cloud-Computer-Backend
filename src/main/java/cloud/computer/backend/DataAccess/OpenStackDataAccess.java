package cloud.computer.backend.DataAccess;

import cloud.computer.backend.Entity.OpenStackConnector;
import cloud.computer.backend.Entity.Server;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.storage.BlockVolumeService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.RebootType;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.compute.builder.BlockDeviceMappingBuilder;
import org.openstack4j.model.image.v2.Image;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.VolumeAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
//@Scope("prototype")
public class OpenStackDataAccess {

    private final OpenStackConnector connector;
    @Value("${openstack.network_id}")
    private String network_id;
    @Value("${openstack.LAN_network_id}")
    private String LAN_network_id;
    @Value("${openstack.create_instance_timeout}")
    private int create_instance_timeout;
    private final Logger logger = LoggerFactory.getLogger(getClass());


    private final ServerDataAccess serverDataAccess;
    private final VolumeDataAccess volumeDataAccess;

    public OpenStackDataAccess(ServerDataAccess serverDataAccess,
                               OpenStackConnector connector,
                               VolumeDataAccess volumeDataAccess) {
        this.serverDataAccess = serverDataAccess;
        this.connector = connector;
        this.volumeDataAccess = volumeDataAccess;
    }


    /**
     * 从OpenStack获取所有的云桌面实例（不是从缓存数据库）
     * @return 云桌面实例构成的列表
     */
    public List<Server> getServers(){
        List<Server> result = new LinkedList<>();
        List<? extends org.openstack4j.model.compute.Server> list = connector.getClient().compute().servers().list(true);
        for (org.openstack4j.model.compute.Server openstack_server : list) {
            // 如果在缓存数据库中找不到此ID的云桌面实例，则说明此实例并非由本云电脑平台创建，不管理
            if (this.serverDataAccess.getServerById(openstack_server.getId()) == null) {
                continue;
            }
            Server server = new Server();
            server.setId(openstack_server.getId());
            server.setName(openstack_server.getName());
            server.setStatus(openstack_server.getStatus().toString());
            server.setFlavorId(openstack_server.getFlavorId());
            server.setImageId(openstack_server.getImageId());
            server.setOwnerId(Objects.requireNonNull(this.serverDataAccess.getServerById(openstack_server.getId())).getOwnerId());
            result.add(server);
        }
        return result;
    }

    /**
     * 请求OpenStack创建云桌面实例
     * @param server 云桌面实例（需要设置名称、实例类型、镜像ID）
     */
    @Async("desktop")
    public void createServer(Server server,
                               String password,
                               int owner_id) {

        Volume volume = createBootableVolume(server.getImageId(),
                this.connector.getClient().compute().flavors().get(server.getFlavorId()).getDisk());

        for (int i = 0; i < create_instance_timeout; i++) {
            if(connector.getClient().blockStorage().volumes().get(volume.getId()).getStatus().name().equals("AVAILABLE")){
//                logger.info("");
                break;
            }else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {

                }
            }
        }

        BlockDeviceMappingBuilder blockDeviceMappingBuilder = Builders.blockDeviceMapping()
                .uuid(volume.getId())
                .deviceName("/dev/vda")
                .bootIndex(0);

        ServerCreate sc = Builders.server()
                .name(server.getName())
                .flavor(server.getFlavorId())
                .blockDevice(blockDeviceMappingBuilder.build())
                .addMetadataItem("admin_pass", password)
                .networks(List.of(network_id))
                .build();
        org.openstack4j.model.compute.Server openstack_server =
                connector.getClient().compute().servers()
                        .bootAndWaitActive(sc, 10000);
        openstack_server = connector.getClient().compute().servers().list(Map.of("id", openstack_server.getId())).getFirst();
        String IP = openstack_server.getAddresses().getAddresses().get(connector.getClient().networking().network().get(network_id).getName()).getFirst().getAddr();
        server.setAddress(IP);
        server.setId(openstack_server.getId());
        // 将VmState设定为状态
        server.setStatus(openstack_server.getVmState());
        server.setOwnerId(owner_id);
        server.setCpuUsage(0L);
        server.setImageName(connector.getClient().imagesV2().get(server.getImageId()).getName());
        server.setRAM(connector.getClient().compute().flavors().get(server.getFlavorId()).getRam());
        server.setvCPU(connector.getClient().compute().flavors().get(server.getFlavorId()).getVcpus());
        this.serverDataAccess.addServer(server);
        volume = connector.getClient().blockStorage().volumes().get(volume.getId());
        this.volumeDataAccess.addVolume(new cloud.computer.backend.Entity.Volume(volume.getId(), server.getId(), volume.getSize(), volume.bootable()));
    }


    public void powerOffServer(String id){
        connector.getClient().compute().servers().action(id, Action.STOP);
    }

    public void powerOnServer(String id){
        connector.getClient().compute().servers().action(id, Action.START);
    }

    public void softRebootServer(String id){
        connector.getClient().compute().servers().reboot(id, RebootType.SOFT);
    }

    public void hardRebootServer(String id){
        connector.getClient().compute().servers().reboot(id, RebootType.HARD);
    }

//    @Async("desktop")
    public void deleteServer(String id){
        connector.getClient().compute().servers().delete(id);
    }

    /**
     * 向OpenStack请求云桌面实例状态
     * @param id 云桌面实例ID
     * @return 状态字符串
     */
    public String getPowerStatus(String id){
        return connector.getClient().compute().servers().get(id).getVmState();
    }

//    @Async
//    public FutureTask<Server> create(Server server, String password){
//
//        return null;
//    }

    public Flavor getFlavor(String id){
        return this.connector.getClient().compute().flavors().get(id);
    }

    public Map<String, ? extends Number> getInfo(String id){
        return this.connector.getClient().compute().servers().diagnostics(id);
    }

    public List<? extends Flavor> getFlavors(){
        return this.connector.getClient().compute().flavors().list();
    }

    public List<? extends Image> getImages(){
        return this.connector.getClient().imagesV2().list();
    }

    public Volume createBootableVolume(String imageId, int size){
        return connector.getClient().blockStorage().volumes().create(
                Builders.volume()
                        .imageRef(imageId)
                        .size(size) //大小
                        .bootable(true)
                        .build()
        );
    }

    private void removeVolume(String id){
        BlockVolumeService volumeService = this.connector.getClient().blockStorage().volumes();
        while (true){

            if (volumeService.delete(id).getCode()/100 == 2){
                break;
            }
            logger.info("删除卷" + id + "失败，即将重试！");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }
        }
        logger.info("删除卷 " + id + " 成功！");
    }


    private void detachVolume(String id){
        OSClient.OSClientV3 client = this.connector.getClient();
        Volume volume = client.blockStorage().volumes().get(id);
        for (VolumeAttachment attachment : volume.getAttachments()) {
            while (true){
                ActionResponse detach = client.blockStorage().volumes().detach(id, attachment.getAttachmentId());
                if ((detach.getCode() / 100) == 2){
                    logger.info("分离卷 " + id + " 的附加关系" + attachment.getAttachmentId() + " 成功！");
                    break;
                }else {
                    logger.info("分离卷 " + id + " 的附加关系" + attachment.getAttachmentId() + " 失败，即将重试！");
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException ignored) {

                    }
                }
            }

        }
        client.blockStorage().volumes().resetState(id, Volume.Status.AVAILABLE);
    }

    @Async("desktop")
    public void deleteVolume(String id){
        detachVolume(id);
        removeVolume(id);
    }



}
