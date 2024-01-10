package cloud.computer.backend.Entity;

import cloud.computer.backend.DataAccess.ServerDataAccess;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
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
            cpu_usage += Long.parseLong(String.valueOf(info.get("cpu" + i + "_time")));
        }
        return cpu_usage;
    }

    public org.openstack4j.model.compute.Server get(List<? extends org.openstack4j.model.compute.Server> servers, String id){
        for (org.openstack4j.model.compute.Server server : servers) {
            if (server.getId().equals(id)) return server;
        }
        return null;
    }

    @Async
    public void sync(){
        OSClient.OSClientV3 client = this.connector.getClient();
        while (true){
            this.logger.info("正在同步OpenStack数据库");
            long t1 = System.nanoTime();
            List<? extends org.openstack4j.model.compute.Server> list = client.compute().servers().list(true);
            for (Server server : this.serverDataAccess.getServers()) {
                try {
                    org.openstack4j.model.compute.Server openstack_server = get(list, server.getId());
                    server.setName(openstack_server.getName());
                    server.setAddress(openstack_server.getAddresses().getAddresses().get(connector.getClient().networking().network().get(network_id).getName()).getFirst().getAddr());
                    server.setStatus(openstack_server.getVmState());
                    server.setImageName(client.imagesV2().get(server.getImageId()).getName());
                    server.setvCPU(client.compute().flavors().get(openstack_server.getFlavorId()).getVcpus());
                    server.setRAM(client.compute().flavors().get(openstack_server.getFlavorId()).getRam());
                    if (!openstack_server.getVmState().equals("stopped")) {
                        server.setCpuUsage(calculateCpuUsage(server.getId()));
                    }

                }catch (Exception e){
                    logger.warn(e.getLocalizedMessage());
                }finally {
                    serverDataAccess.updateServer(server);
                }

            }
            long t2 = System.nanoTime();
            logger.info("同步完毕，此次同步耗时：" + (t2-t1)/1e6 + "毫秒");
            try {
                Thread.sleep(period * 1000L);
            } catch (InterruptedException ignored) {

            }


        }
    }
}
