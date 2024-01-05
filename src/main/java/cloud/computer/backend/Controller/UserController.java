package cloud.computer.backend.Controller;

import cloud.computer.backend.Entity.AuthenticationResult;
import cloud.computer.backend.Service.AuthenticationService;
import cloud.computer.backend.Service.TokenService;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final AuthenticationService authenticationService;
    private final TokenService tokenService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public UserController(AuthenticationService authenticationService, TokenService tokenService) {
        this.authenticationService = authenticationService;
        this.tokenService = tokenService;
    }

    @PostMapping(value = "/login")
    public JSONObject login(@RequestBody JSONObject param){
        String username = param.getString("username");
        String password = param.getString("password");
        logger.info("收到用户 " + username + " 的登录请求" );
        JSONObject result = new JSONObject();
        AuthenticationResult authenticate = this.authenticationService.authenticate(username, password);
        result.put("code", authenticate.getResult() ? 1 : 0);
        if(result.getInteger("code") == 1){
            result.put("token", tokenService.assignToken(username));
        }
        if (authenticate.getReason() != null) {
            switch (authenticate.getReason()){
                case USER_NOT_FOUND -> {
                    result.put("message", "用户" + username + "不存在！");
                    logger.info("用户 " + username + " 找不到，拒绝登录");
                }
                case WRONG_PASSWORD -> {
                    result.put("message", "密码错误！");
                    logger.info("用户 " + username + " 密码错误，拒绝登录");
                }
            }
        }else {
            result.put("message", "登录成功！");
            logger.info("用户 " + username + " 的登录请求合规，准许登录");
        }
        return result;
    }


    @PostMapping("/register")
    public JSONObject register(@RequestBody JSONObject param){
        String username = param.getString("username");
        String password = param.getString("password");
        logger.info("收到注册请求，用户名：" + username);
        JSONObject result = new JSONObject();
        AuthenticationResult authenticate = this.authenticationService.addUser(username, password);
        result.put("code", authenticate.getResult() ? 1 : 0);
        if (authenticate.getReason() != null){
            result.put("message", "用户" + username + "已存在！");
        }
        logger.info("注册请求处理结果：" + (authenticate.getResult() ? "成功注册": "注册失败，原因：" + authenticate.getReason().toString()));
        return result;


    }
}
