package cloud.computer.backend.Entity.RowMapper;

import cloud.computer.backend.Entity.Token;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenRowMapper implements RowMapper<Token> {
    @Override
    public Token mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Token(rs.getInt("id"),
                rs.getString("token"),
                rs.getDate("expired"),
                rs.getInt("owner_id"));
    }


}
