package cloud.computer.backend.Entity;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.identity.v3.Token;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class OpenStackConnector {
    private OSClient.OSClientV3 client;
    private Token token;
    @Value("${openstack.url}")
    private String endpoint;
    @Value("${openstack.admin}")
    private String username;
    @Value("${openstack.password}")
    private String password;
    @Value("${openstack.project_id}")
    private String project_id;
    @Value("${openstack.domain}")
    private String domain;

    @PostConstruct
    public void init(){
        this.client = OSFactory.builderV3()
                .endpoint(endpoint)
                .credentials(username, password, Identifier.byId(domain))
                .scopeToProject(Identifier.byId(project_id))
                .authenticate();
        this.token = this.client.getToken();
    }
    public OSClient.OSClientV3 getClient(){
        return OSFactory.clientFromToken(token);
    }
}
