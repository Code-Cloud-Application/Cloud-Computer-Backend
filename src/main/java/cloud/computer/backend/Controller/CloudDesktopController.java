package cloud.computer.backend.Controller;

import cloud.computer.backend.DataAccess.UserDataAccess;
import cloud.computer.backend.Entity.Server;
import cloud.computer.backend.Entity.User;
import cloud.computer.backend.Service.CloudDesktopService;
import cloud.computer.backend.Service.OpenStackCloudDesktopService;
import com.alibaba.fastjson2.JSONObject;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.image.v2.Image;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        if (user == null) {
            result.put("code", 0);
            result.put("message", "用户不存在");
            return result;
        }
        int userId = user.getId();
        this.cloudDesktopService.create(name, flavor_id, image_id, desktop_password, userId);
        result.put("code", 1);
        return result;
    }

    @GetMapping("/listInstance")
    public JSONObject list(@RequestBody JSONObject param){
        JSONObject result = new JSONObject();
        String username = param.getString("username");
        User user = this.userDataAccess.getUser(username);
        if (user == null) {
            result.put("code", 0);
            result.put("message", "用户不存在");
            return result;
        }
        result.put("code", 1);
        List<JSONObject> servers = new LinkedList<>();
        for (Server desktop : this.cloudDesktopService.getCloudDesktops(user.getId())) {
            JSONObject a = new JSONObject();
            Flavor flavor = this.cloudDesktopService.getFlavor(desktop.getFlavorId());
            a.put("name", desktop.getName());
            a.put("status", desktop.getStatus());
            a.put("address", desktop.getAddress());
            a.put("vCPU", flavor.getVcpus());
            a.put("RAM", flavor.getRam());
            a.put("disk", flavor.getDisk());
            double cpu_usage = 0;
            Map<String, ? extends Number> info = this.cloudDesktopService.getInfo(desktop.getId());
            for (int i = 0; i < flavor.getVcpus(); i++) {
                cpu_usage += ((Long) info.get("cpu" + i + "_time")) / 1e9;
            }
            a.put("cpu_usage", cpu_usage);

            servers.add(a);
        }
        result.put("desktops", servers);
        return result;
    }

    @GetMapping("/listImages")
    public JSONObject listImages(){
        JSONObject result = new JSONObject();
        result.put("code", 1);
        List<JSONObject> list = new LinkedList<>();
        for (Image image : this.cloudDesktopService.getImages()) {
            String id = image.getId();
            String name = image.getName();
            Long minDisk = image.getMinDisk();
            Long minRam = image.getMinRam();
            JSONObject a = new JSONObject();
            a.put("id", id);
            a.put("name", name);
            a.put("min_disk", minDisk);
            a.put("min_ram", minRam);
            list.add(a);
        }
        result.put("images", list);
        return result;
    }

    @GetMapping("/listFlavors")
    public JSONObject listFlavors(){
        JSONObject result = new JSONObject();
        result.put("code", 1);
        List<JSONObject> list = new LinkedList<>();
        for (Flavor flavor : this.cloudDesktopService.getFlavors()) {
            String id = flavor.getId();
            String name = flavor.getName();
            int vcpus = flavor.getVcpus();
            int ram = flavor.getRam();
            int disk = flavor.getDisk();
            JSONObject a = new JSONObject();
            a.put("id", id);
            a.put("name", name);
            a.put("vcpus", vcpus);
            a.put("ram", ram);
            a.put("disk", disk);
            list.add(a);
        }
        result.put("flavors", list);
        return result;
    }
}
