package cloud.computer.backend.DataAccessTest;

import cloud.computer.backend.DataAccess.TokenDataAccess;
import cloud.computer.backend.Entity.Token;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TokenDataAccessTest {
    private TokenDataAccess tokenDataAccess;
    private static List<Token> tokens;

    private static Token token;

    @Value("${token.period}")
    private int period;

    @Autowired
    public void setTokenDataAccess(TokenDataAccess tokenDataAccess) {
        this.tokenDataAccess = tokenDataAccess;
    }

    @Test
    @DisplayName("取出所有令牌")
    @Order(1)
    public void getTokens(){
        Assertions.assertDoesNotThrow(() -> {
            tokens = this.tokenDataAccess.getTokens();
        });
    }

    @Test
    @DisplayName("添加令牌")
    @Order(2)
    public void addToken(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, period);
        token = new Token(this.tokenDataAccess.getMaxId()+1, "test", calendar.getTime(), 1);
        Assertions.assertDoesNotThrow(() -> {
            this.tokenDataAccess.addToken(token);
        });
        Assertions.assertEquals(tokens.size()+1,
                this.tokenDataAccess.getTokens().size());
    }

    @Test
    @DisplayName("检查令牌是否添加成功")
    @Order(3)
    public void checkToken(){
        Assertions.assertEquals(token.getId(), this.tokenDataAccess.getToken("test").getId());
        Assertions.assertEquals(token.getOwnerId(), this.tokenDataAccess.getToken("test").getOwnerId());
        Assertions.assertEquals(token.getValue(), this.tokenDataAccess.getToken("test").getValue());
    }

    @Test
    @DisplayName("删除令牌")
    @Order(4)
    public void removeToken(){
        Assertions.assertDoesNotThrow(() -> {
            this.tokenDataAccess.removeToken(token.getId());
        });
    }

    @Test
    @DisplayName("检查令牌是否删除成功")
    @Order(5)
    public void checkRemoved(){
        Assertions.assertEquals(tokens.size(), this.tokenDataAccess.getTokens().size());
    }
}
