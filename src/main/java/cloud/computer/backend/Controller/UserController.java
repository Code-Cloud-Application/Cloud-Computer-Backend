package cloud.computer.backend.Controller;

import cloud.computer.backend.Entity.User;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @PostMapping(value = "/login", consumes = "application/json")
    public JSONObject login(@RequestBody JSONObject param,
                            HttpServletRequest request,
                            HttpServletResponse response){
        System.out.println(param);
        return null;
    }
}
