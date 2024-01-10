package cloud.computer.backend.ServiceTest;

import cloud.computer.backend.DataAccess.UserDataAccess;
import cloud.computer.backend.Entity.User;
import cloud.computer.backend.Service.TokenService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TokenServiceTest {
    private TokenService tokenService;
    private UserDataAccess userDataAccess;
    private static int userid;
    private static int size;
    private static String token;

    @Autowired
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    @Autowired
    public void setUserDataAccess(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }




    @Test
    @DisplayName("增加用于测试的用户")
    @Order(1)
    public void addUser(){
        userid = this.userDataAccess.getMaxId()+1;
        Assertions.assertDoesNotThrow(() -> {
            this.userDataAccess.addUser(new User(userid, "test_user", "123456"));
        });
    }

    @Test
    @DisplayName("分配令牌")
    @Order(2)
    public void assignToken(){
        Assertions.assertDoesNotThrow(() -> {
            this.tokenService.assignToken("test_user");
        });
    }


    @Test
    @DisplayName("吊销测试用户的所有令牌")
    @Order(10)
    public void revokeAllTokens(){
        Assertions.assertDoesNotThrow(() -> {
            this.tokenService.revokeALlTokens("test_user");
        });
    }

    @Test
    @DisplayName("删除测试用户")
    @Order(20)
    public void deleteUser(){
        Assertions.assertDoesNotThrow(() -> this.userDataAccess.removeUser(userid));
    }
}
