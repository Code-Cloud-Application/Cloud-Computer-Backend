package cloud.computer.backend.DataAccess;

import cloud.computer.backend.Entity.OpenStackConnector;
import cloud.computer.backend.Entity.Server;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.RebootType;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.identity.v3.Token;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
//@Scope("prototype")
public class OpenStackKeystoneDataAccess {

    private final OpenStackConnector connector;
    @Value("${openstack.network_id}")
    private String network_id;
    @Value("${openstack.LAN_network_id}")
    private String LAN_network_id;


    private final ServerDataAccess serverDataAccess;

    public OpenStackKeystoneDataAccess(ServerDataAccess serverDataAccess,
                                       OpenStackConnector connector) {
        this.serverDataAccess = serverDataAccess;
        this.connector = connector;
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
    public Server createServer(Server server,
                               String password) {
//        connector.getClient() = OSFactory.clientFromToken(token);
        ServerCreate sc = Builders.server()
                .name(server.getName())
                .flavor(server.getFlavorId())
                .image(server.getImageId())
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
        return server;
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



}
