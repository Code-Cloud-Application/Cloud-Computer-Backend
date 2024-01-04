package cloud.computer.backend.DataAccess;

import cloud.computer.backend.Entity.RowMapper.TokenRowMapper;
import cloud.computer.backend.Entity.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class TokenDataAccess implements GenericIdDataAccess{

    private JdbcTemplate template;

    @Value("${token.period}")
    private int period;

    public TokenDataAccess(JdbcTemplate template) {
        this.template = template;
    }

    public String generateToken(){
        return UUID.randomUUID().toString();
    }

    public List<Token> getTokens(){
        return this.template.query("select * from `token`", new TokenRowMapper());
    }

    @Nullable
    public Token getToken(String value){
        return this.template.queryForObject("select * from `token` where token=?",
                new TokenRowMapper(),
                value);
    }

    @Override
    public int getMaxId(){
        Optional<Integer> optional = Optional.ofNullable(this.template.queryForObject("select MAX(id) from `token`", Integer.class));
        return optional.orElse(0);
    }


    public void addToken(Token token){

        this.template.update("insert into token (id, token, expired, owner_id) values (?, ?, ?, ?);",
                token.getId(), token.getValue(), token.getExpiredAt(), token.getOwnerId());

    }

    public void removeToken(int id){
        this.template.update("delete from token where id=?;", id);
    }

    public void removeTokenByOwnerId(int id){
        this.template.update("delete from token where owner_id=?;", id);
    }


}
