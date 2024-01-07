package cloud.computer.backend.DataAccessTest;

import cloud.computer.backend.DataAccess.ServerDataAccess;
import cloud.computer.backend.Entity.Server;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerDataAccessTest {
    private ServerDataAccess serverDataAccess;
    private static final Server server = new Server();
    private static int size;

    @Autowired
    private void setServerDataAccess(ServerDataAccess serverDataAccess) {
        this.serverDataAccess = serverDataAccess;
    }

    @Test
    @Order(1)
    @DisplayName("添加云桌面")
    public void addServer(){
        size = serverDataAccess.getServers().size();
        Assertions.assertDoesNotThrow(() -> {
            server.setId("test-server-id");
            server.setImageId("1");
            server.setName("test-server-name");
            server.setAddress("0.0.0.0");
            server.setOwnerId(1);
            server.setFlavorId("7");
            server.setStatus("SHUTOFF");
            this.serverDataAccess.addServer(server);
        });
    }


    @Test
    @Order(2)
    @DisplayName("检查云桌面是否添加成功")
    public void check1(){
        Assertions.assertEquals(size + 1, serverDataAccess.getServers().size());
    }

    @Test
    @Order(3)
    @DisplayName("更新数据库信息")
    public void updateServer(){
        Assertions.assertDoesNotThrow(() -> {
            server.setStatus("POWER_ON");
            this.serverDataAccess.updateServer(server);
        });
    }

    @Test
    @Order(4)
    @DisplayName("检查更新是否成功")
    public void checkUpdatedSuccessful(){
        Server s = this.serverDataAccess.getServerById(server.getId());
        Assertions.assertEquals(s.getStatus(), server.getStatus());
    }

    @Test
    @Order(5)
    @DisplayName("删除信息")
    public void delete(){
        Assertions.assertDoesNotThrow(() -> {
            serverDataAccess.removeServer(server.getId());
        });
    }

    @Test
    @Order(6)
    @DisplayName("检查删除是否成功")
    public void check2(){
        Assertions.assertEquals(size, serverDataAccess.getServers().size());
    }
}
