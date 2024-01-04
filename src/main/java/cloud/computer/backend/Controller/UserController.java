package cloud.computer.backend.Controller;

import cloud.computer.backend.Entity.AuthenticationResult;
import cloud.computer.backend.Entity.User;
import cloud.computer.backend.Service.AuthenticationService;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserController {

    private final AuthenticationService authenticationService;

    public UserController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/login")
    public JSONObject login(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            HttpServletRequest request,
                            HttpServletResponse response){
        System.out.println(username);
        System.out.println(password);
        JSONObject result = new JSONObject();
        AuthenticationResult authenticate = this.authenticationService.authenticate(username, password);
        result.put("code", authenticate.getResult() ? 1 : 0);
        if (authenticate.getReason() != null) {
            switch (authenticate.getReason()){
                case USER_NOT_FOUND -> result.put("message", "用户" + username + "不存在！");
                case WRONG_PASSWORD -> result.put("message", "密码错误！");
            }
        }
        System.out.println(UUID.randomUUID());

        return result;
    }
}
