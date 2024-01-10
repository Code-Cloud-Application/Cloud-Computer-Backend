package cloud.computer.backend.Service;

import cloud.computer.backend.DataAccess.OpenStackDataAccess;
import cloud.computer.backend.DataAccess.ServerDataAccess;
import cloud.computer.backend.DataAccess.VolumeDataAccess;
import cloud.computer.backend.Entity.Server;
import cloud.computer.backend.Entity.Volume;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.image.v2.Image;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class OpenStackCloudDesktopService implements CloudDesktopService{

    private final ServerDataAccess serverDataAccess;
    private final OpenStackDataAccess openStackDataAccess;
    private final VolumeDataAccess volumeDataAccess;

    public OpenStackCloudDesktopService(ServerDataAccess serverDataAccess, OpenStackDataAccess openStackDataAccess, VolumeDataAccess volumeDataAccess) {
        this.serverDataAccess = serverDataAccess;
        this.openStackDataAccess = openStackDataAccess;
        this.volumeDataAccess = volumeDataAccess;
    }

    /**
     * 获取某位用户的所有云桌面实例
     *
     * @param user_id 用户ID
     * @return 列表，包含该用户所持有的所有云桌面实例
     */
    @Override
    public List<Server> getCloudDesktops(int user_id) {
        return this.serverDataAccess.getServerByOwner(user_id);
    }

    @Override
    @Nullable
    public Server getCloudDesktop(String id) {
        return this.serverDataAccess.getServerById(id);
    }

    /**
     * 开启某个云桌面实例
     *
     * @param id 云桌面实例的ID
     */
    @Override
    public void powerOn(String id) {
        Server server = this.serverDataAccess.getServerById(id);
        if (server == null) {
            return;
        }
        this.openStackDataAccess.powerOnServer(id);
        server.setStatus(this.openStackDataAccess.getPowerStatus(id));
        this.serverDataAccess.updateServer(server);
    }

    /**
     * 关闭某个云桌面实例
     *
     * @param id 云桌面实例的ID
     */
    @Override
    public void powerOff(String id) {
        Server server = this.serverDataAccess.getServerById(id);
        if (server == null) {
            return;
        }
        this.openStackDataAccess.powerOffServer(id);
        server.setStatus(this.openStackDataAccess.getPowerStatus(id));
        this.serverDataAccess.updateServer(server);
    }

    /**
     * 重启某个云桌面实例
     *
     * @param id     云桌面实例的ID
     * @param method 重启方法，包含软重启和硬重启
     */
    @Override
    public void reboot(String id, RebootMethod method) {
        Server server = this.serverDataAccess.getServerById(id);
        if (server == null) {
            return;
        }
        switch (method){
            case HARD -> this.openStackDataAccess.hardRebootServer(id);
            case SOFT -> this.openStackDataAccess.softRebootServer(id);
        }
        server.setStatus(this.openStackDataAccess.getPowerStatus(id));
        this.serverDataAccess.updateServer(server);
    }

    /**
     * 删除某个云桌面实例
     *
     * @param id 云桌面实例ID
     */
    @Override
    public void remove(String id) {
        Server server = this.serverDataAccess.getServerById(id);
        if (server == null) {
            return;
        }

        this.openStackDataAccess.deleteServer(id);
        this.serverDataAccess.removeServer(id);
        for (Volume volume : this.volumeDataAccess.getByDesktopId(id)) {
            this.openStackDataAccess.deleteVolume(volume.getId());
            this.volumeDataAccess.removeVolume(volume.getId());
        }


    }

    /**
     *
     * @param server_name 云桌面名称
     * @param flavor_id 云桌面实例规格ID
     * @param image_id 云桌面镜像ID
     * @param password 云桌面密码
     * @param owner_id 云桌面所有者 ID
     * @throws InterruptedException 创建中断会抛出此异常
     */
    @Override
    public void create(String server_name,
                       String flavor_id,
                       String image_id,
                       String password,
                       int owner_id) throws InterruptedException {
        Server server = new Server();
        server.setName(server_name);
        server.setFlavorId(flavor_id);
        server.setImageId(image_id);
        this.openStackDataAccess.createServer(server, password, owner_id);
    }

    @Override
    public Flavor getFlavor(String id){
        return this.openStackDataAccess.getFlavor(id);
    }

    @Override
    public Map<String, ? extends Number> getInfo(String id){
        return this.openStackDataAccess.getInfo(id);
    }

    @Override
    public List<? extends Flavor> getFlavors(){
        return this.openStackDataAccess.getFlavors();
    }

    @Override
    public List<? extends Image> getImages(){
        return this.openStackDataAccess.getImages();
    }

    @Override
    public long getCpuUsage(String id){
        return Objects.requireNonNull(this.serverDataAccess.getServerById(id)).getCpuUsage();
    }

}
