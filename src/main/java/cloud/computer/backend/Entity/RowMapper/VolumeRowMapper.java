package cloud.computer.backend.Entity.RowMapper;

import cloud.computer.backend.Entity.Volume;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VolumeRowMapper implements RowMapper<Volume> {
    @Override
    public Volume mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Volume(
                rs.getString("id"),
                rs.getString("desktop_id"),
                rs.getInt("size"),
                rs.getShort("bootable") == 1
                );
    }
}
