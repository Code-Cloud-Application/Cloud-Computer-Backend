package cloud.computer.backend.Service;

import cloud.computer.backend.DataAccess.OpenStackDataAccess;
import cloud.computer.backend.DataAccess.ServerDataAccess;
import cloud.computer.backend.Entity.Server;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.image.v2.Image;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class OpenStackCloudDesktopService implements CloudDesktopService{

    private final ServerDataAccess serverDataAccess;
    private final OpenStackDataAccess openStackDataAccess;

    public OpenStackCloudDesktopService(ServerDataAccess serverDataAccess, OpenStackDataAccess openStackDataAccess) {
        this.serverDataAccess = serverDataAccess;
        this.openStackDataAccess = openStackDataAccess;
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
    }

    /**
     * 创建云桌面实例
     *
     * @param server 云桌面实例
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
//        Server server1 = null;
        this.openStackDataAccess.createServer(server, password, owner_id);
//
//        server1.setOwnerId(owner_id);
//        this.serverDataAccess.addServer(server1);
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
