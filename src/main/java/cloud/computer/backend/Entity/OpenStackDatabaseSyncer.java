package cloud.computer.backend.Entity;

import cloud.computer.backend.DataAccess.ServerDataAccess;
import org.openstack4j.api.OSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class OpenStackDatabaseSyncer {

    private final OpenStackConnector connector;
    private final ServerDataAccess serverDataAccess;
    @Value("${openstack.network_id}")
    private String network_id;
    @Value("${sync.period}")
    private int period;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public OpenStackDatabaseSyncer(OpenStackConnector connector, ServerDataAccess serverDataAccess) {
        this.connector = connector;
        this.serverDataAccess = serverDataAccess;
    }

    public Long calculateCpuUsage(String id){
        OSClient.OSClientV3 client = this.connector.getClient();
        Long cpu_usage = 0L;
        Map<String, ? extends Number> info = client.compute().servers().diagnostics(id);
        for (int i = 0; i < client.compute().servers().get(id).getFlavor().getVcpus(); i++) {
            cpu_usage += ((Long) info.get("cpu" + i + "_time"));
        }
        return cpu_usage;
    }

    @Async
    public void sync(){
        while (true){
            this.logger.info("正在同步OpenStack数据库");
            OSClient.OSClientV3 client = this.connector.getClient();

            for (Server server : this.serverDataAccess.getServers()) {
                org.openstack4j.model.compute.Server openstack_server = client.compute().servers().get(server.getId());
                server.setName(openstack_server.getName());
                server.setAddress(openstack_server.getAddresses().getAddresses().get(connector.getClient().networking().network().get(network_id).getName()).getFirst().getAddr());
                server.setStatus(openstack_server.getVmState());
                server.setImageName(connector.getClient().imagesV2().get(server.getImageId()).getName());
                server.setvCPU(client.compute().flavors().get(openstack_server.getFlavorId()).getVcpus());
                server.setRAM(client.compute().flavors().get(openstack_server.getFlavorId()).getRam());
                server.setCpuUsage(calculateCpuUsage(server.getId()));
                serverDataAccess.updateServer(server);
            }

            try {
                Thread.sleep(period * 1000L);
            } catch (InterruptedException ignored) {

            }
        }
    }
}
