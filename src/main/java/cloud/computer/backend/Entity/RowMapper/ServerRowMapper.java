package cloud.computer.backend.Entity.RowMapper;

import cloud.computer.backend.Entity.Server;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerRowMapper implements RowMapper<Server> {
    @Override
    public Server mapRow(ResultSet rs, int rowNum) throws SQLException {
        Server server = new Server();
        server.setId(rs.getString("id"));
        server.setName(rs.getString("name"));
        server.setAddress(rs.getString("address"));
        server.setImageId(rs.getString("image_id"));
        server.setFlavorId(rs.getString("flavor_id"));
        server.setStatus(rs.getString("status"));
        server.setOwnerId(rs.getInt("owner_id"));
        return server;
    }
}
