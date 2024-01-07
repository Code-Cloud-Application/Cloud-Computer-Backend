package cloud.computer.backend.DataAccessTest;

import cloud.computer.backend.Entity.Server;
import cloud.computer.backend.DataAccess.OpenStackKeystoneDataAccess;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OpenStackDataAccessTest {
    private OpenStackKeystoneDataAccess openStackKeystoneDataAccess;

    @Autowired
    public void setOpenStackKeystoneDataAccess(OpenStackKeystoneDataAccess openStackKeystoneDataAccess) {
        this.openStackKeystoneDataAccess = openStackKeystoneDataAccess;
    }

    @Test
    public void create() throws InterruptedException {
        Server server = new Server();
        server.setOwnerId(1);
        server.setImageId("75a32e66-6f1d-439c-8d5d-1961b1e36471");
        server.setFlavorId("3");
        server.setName("测试服务器");
        this.openStackKeystoneDataAccess.createServer(server, "123456");
    }
}
