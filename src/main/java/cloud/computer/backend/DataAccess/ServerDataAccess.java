package cloud.computer.backend.DataAccess;

import cloud.computer.backend.Entity.RowMapper.ServerRowMapper;
import cloud.computer.backend.Entity.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ServerDataAccess {

    private JdbcTemplate template;

    @Autowired
    private void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    public List<Server> getServers(){
        return this.template.query("select * from server", new ServerRowMapper());
    }

    public Server getServerById(String id){
        return this.template.queryForObject("select * from server where id=?", new ServerRowMapper(), id);
    }

    @Nullable
    public Server getServerByName(String name){
        return this.template.queryForObject("select * from server where name=?", new ServerRowMapper(), name);
    }

    public void addServer(Server server){
        this.template.update("insert into server (id, name, address, image_id, flavor_id, status, owner_id, created_at) values (?, ?, ?, ?, ?, ?, ?, ?);",
                server.getId(), server.getName(), server.getAddress(), server.getImageId(), server.getFlavorId(), server.getStatus(), server.getOwnerId(), server.getCreated());
    }

    public void updateServer(Server server){
        this.template.update("update server set address = ?, image_id = ?, flavor_id = ?, " +
                "status = ?, owner_id = ? where id = ?;",
                server.getAddress(),
                server.getImageId(),
                server.getFlavorId(),
                server.getStatus(),
                server.getOwnerId());
    }

    public void removeServer(String id){
        this.template.update("delete from server where id=?;", id);
    }
}
