package cloud.computer.backend;


import org.junit.jupiter.api.Test;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.compute.ServerService;
import org.openstack4j.api.identity.v3.ProjectService;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.openstack.OSFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class OpenStackTests {
    @Test
    public void login(){
        OSClientV3 client = OSFactory.builderV3()
                .endpoint("http://10.2.10.1:5000/v3")
                .credentials("admin", "openstack2023", Identifier.byId("default"))
                .scopeToProject(Identifier.byId("b7ea1c378b0f4d68aa9673841bd2257f"))
                .authenticate();

    }

    @Test
    public void listFlavors(){
        OSClientV3 client = OSFactory.builderV3()
                .endpoint("http://10.2.10.1:5000/v3")
                .credentials("admin", "openstack2023", Identifier.byId("default"))
                .scopeToProject(Identifier.byId("b7ea1c378b0f4d68aa9673841bd2257f"))
                .authenticate();

        for (Flavor flavor : client.compute().flavors().list()) {
            System.out.println(flavor);
        }
    }

    @Test
    public void listProject(){
        OSClientV3 client = OSFactory.builderV3()
                .endpoint("http://10.2.10.1:5000/v3")
                .credentials("admin", "openstack2023", Identifier.byId("default"))
                .scopeToProject(Identifier.byId("b7ea1c378b0f4d68aa9673841bd2257f"))
                .authenticate();

        for (Project project : client.identity().projects().list()) {
            System.out.println(project);
        }
    }

    @Test
    public void startVM(){
        OSClientV3 client = OSFactory.builderV3()
                .endpoint("http://10.2.10.1:5000/v3")
                .credentials("admin", "openstack2023", Identifier.byId("default"))
                .scopeToProject(Identifier.byId("b7ea1c378b0f4d68aa9673841bd2257f"))
                .authenticate();
        List<? extends Server> list = client.compute().servers().list();
        Server server = list.get(0);
        System.out.println(server.getStatus());
        ServerService servers = client.compute().servers();
        servers.action(server.getId(), Action.START);
    }

    @Test
    public void getVMInfo(){
        OSClientV3 client = OSFactory.builderV3()
                .endpoint("http://10.2.10.1:5000/v3")
                .credentials("admin", "openstack2023", Identifier.byId("default"))
                .scopeToProject(Identifier.byId("b7ea1c378b0f4d68aa9673841bd2257f"))
                .authenticate();
        for (Server server : client.compute().servers().list(true)) {
            System.out.println("电源状态\t实例规格ID");
            System.out.print(server.getStatus() + "\t");
            System.out.print(server.getFlavorId() + "\t");
        }
    }
}
