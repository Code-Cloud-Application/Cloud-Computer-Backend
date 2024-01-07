package cloud.computer.backend.DataAccess;

import cloud.computer.backend.Entity.RowMapper.ServerRowMapper;
import cloud.computer.backend.Entity.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

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

    @Nullable
    public Server getServerById(String id){
        try {
            return this.template.queryForObject("select * from server where id=?", new ServerRowMapper(), id);
        }catch (EmptyResultDataAccessException e){
            return null;
        }

    }

    @Nullable
    public Server getServerByName(String name){
        return this.template.queryForObject("select * from server where name=?", new ServerRowMapper(), name);
    }

    public List<Server> getServerByOwner(int owner_id){
        return this.template.query("select * from server where owner_id=?", new ServerRowMapper(), owner_id);
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
                server.getOwnerId(),
                server.getId());
    }

    public void removeServer(String id){
        this.template.update("delete from server where id=?;", id);
    }
}
