package cloud.computer.backend.Service;

import cloud.computer.backend.DataAccess.ServerDataAccess;
import cloud.computer.backend.Entity.Server;
import cloud.computer.backend.DataAccess.OpenStackKeystoneDataAccess;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class OpenStackCloudDesktopService implements CloudDesktopService{

    private final ServerDataAccess serverDataAccess;
    private final OpenStackKeystoneDataAccess openStackKeystoneDataAccess;

    public OpenStackCloudDesktopService(ServerDataAccess serverDataAccess, OpenStackKeystoneDataAccess openStackKeystoneDataAccess) {
        this.serverDataAccess = serverDataAccess;
        this.openStackKeystoneDataAccess = openStackKeystoneDataAccess;
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
        this.openStackKeystoneDataAccess.powerOnServer(id);
        server.setStatus(this.openStackKeystoneDataAccess.getPowerStatus(id));
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
        this.openStackKeystoneDataAccess.powerOffServer(id);
        server.setStatus(this.openStackKeystoneDataAccess.getPowerStatus(id));
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
            case HARD -> this.openStackKeystoneDataAccess.hardRebootServer(id);
            case SOFT -> this.openStackKeystoneDataAccess.softRebootServer(id);
        }
        server.setStatus(this.openStackKeystoneDataAccess.getPowerStatus(id));
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
        this.openStackKeystoneDataAccess.deleteServer(id);
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
        Server server1 = this.openStackKeystoneDataAccess.createServer(server, password);
        server1.setOwnerId(owner_id);
        this.serverDataAccess.addServer(server1);
    }



}
