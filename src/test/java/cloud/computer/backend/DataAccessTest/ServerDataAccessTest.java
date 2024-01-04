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
    private final Server server = new Server();

    @Autowired
    private void setServerDataAccess(ServerDataAccess serverDataAccess) {
        this.serverDataAccess = serverDataAccess;
    }

    @Test
    @Order(1)
    public void addServer(){
        Assertions.assertDoesNotThrow(() -> {
            server.setId("test-server-id");
            server.setImageId("1");
            server.setName("test-server-name");
            server.setAddress("0.0.0.0");
            server.setOwnerId(1);
            server.setFlavorId("7");
            server.setStatus("SHUTOFF");
        });
    }

    @Test
    @Order(2)
    public void updateServer(){
        Assertions.assertDoesNotThrow(() -> {
            server.setStatus("POWERON");
            this.serverDataAccess.updateServer(server);
        });
    }

    @Test
    @Order(3)
    public void checkUpdatedSuccessful(){
        Server s = this.serverDataAccess.getServerById(server.getId());
        Assertions.assertEquals(server, s);
    }
}
