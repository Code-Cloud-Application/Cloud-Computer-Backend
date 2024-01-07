package cloud.computer.backend.Controller;

import cloud.computer.backend.DataAccess.UserDataAccess;
import cloud.computer.backend.Entity.User;
import cloud.computer.backend.Service.CloudDesktopService;
import cloud.computer.backend.Service.OpenStackCloudDesktopService;
import com.alibaba.fastjson2.JSONObject;
import org.openstack4j.api.OSClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@Transactional
public class CloudDesktopController {

    private OSClient.OSClientV3 client;

    private UserDataAccess userDataAccess;
    private CloudDesktopService cloudDesktopService;

    public CloudDesktopController(UserDataAccess userDataAccess,
                                  OpenStackCloudDesktopService cloudDesktopService) {
        this.userDataAccess = userDataAccess;
        this.cloudDesktopService = cloudDesktopService;
    }

    @PostMapping("/createInstance")
    public JSONObject create(@RequestBody JSONObject param) throws InterruptedException {
        JSONObject result = new JSONObject();
        String name = param.getString("name");
        String flavor_id = param.getString("flavor-id");
        String image_id = param.getString("image-id");
        String username = param.getString("username");
        String desktop_password = param.getString("desktop-password");
        User user = userDataAccess.getUser(username);
        int userId = user.getId();
        this.cloudDesktopService.create(name, flavor_id, image_id, desktop_password, userId);
        result.put("code", 1);
        return result;
    }
}
