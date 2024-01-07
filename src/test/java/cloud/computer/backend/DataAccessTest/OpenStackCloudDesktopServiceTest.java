package cloud.computer.backend.DataAccessTest;

import cloud.computer.backend.Entity.Server;
import cloud.computer.backend.Service.CloudDesktopService;
import cloud.computer.backend.Service.OpenStackCloudDesktopService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OpenStackCloudDesktopServiceTest {
    private CloudDesktopService cloudDesktopService;

    private static int size;
    private static Server server;

    @Autowired
    public void setCloudDesktopService(OpenStackCloudDesktopService cloudDesktopService) {
        this.cloudDesktopService = cloudDesktopService;
    }

    @Test
    @Order(1)
    @DisplayName("创建云桌面")
    public void create() {
        size = this.cloudDesktopService.getCloudDesktops(1).size();
        Assertions.assertDoesNotThrow(() -> {
            Server server = new Server();
            server.setName("测试机");
            server.setImageId("75a32e66-6f1d-439c-8d5d-1961b1e36471");
            server.setFlavorId("3");
            this.cloudDesktopService.create(server.getName(), server.getFlavorId(), server.getImageId(), "123456", 1);
        });
    }

    @Test
    @Order(2)
    @DisplayName("查看云桌面的信息")
    @Timeout(5)
    public void inspect(){
        Assertions.assertDoesNotThrow(() -> {
            server = this.cloudDesktopService.getCloudDesktops(1).getLast();
            System.out.println(server);
        });

    }

    @Test
    @Order(3)
    @DisplayName("销毁云桌面实例")
    public void delete(){
        Assertions.assertDoesNotThrow(() -> {
            this.cloudDesktopService.remove(server.getId());
        });
    }

}
