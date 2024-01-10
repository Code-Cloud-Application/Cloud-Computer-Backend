package cloud.computer.backend.Service;

import cloud.computer.backend.Entity.Server;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.image.v2.Image;

import java.util.List;
import java.util.Map;

/**
 * 云桌面服务接口（核心API）
 * @since 1.0.0
 * @author 等陌上花开
 * @implNote 涉及到使用ID变更电源状态的情况，请验证是否在缓存数据库内存在，不存在的不属于
 * 管理范围，直接跳过，不要变更电源状态。每次涉及到电源状态的变更时，请同步更新数据库
 */
public interface CloudDesktopService {

    /**
     * 获取某位用户的所有云桌面实例
     * @param user_id 用户ID
     * @return 列表，包含该用户所持有的所有云桌面实例
     */
    List<Server> getCloudDesktops(int user_id);
    Server getCloudDesktop(String id);

    /**
     * 开启某个云桌面实例
     *
     * @param id 云桌面实例的ID
     */
    void powerOn(String id);

    /**
     * 关闭某个云桌面实例
     *
     * @param id 云桌面实例的ID
     */
    void powerOff(String id);

    /**
     * 重启某个云桌面实例
     * @param id 云桌面实例的ID
     * @param method 重启方法，包含软重启和硬重启
     */
    void reboot(String id, RebootMethod method);

    /**
     * 删除某个云桌面实例
     * @param id 云桌面实例ID
     */
    void remove(String id);

    /**
     * 创建云桌面实例
     * @param server 云桌面实例
     */
    void create(String server_name,
                String flavor_id,
                String image_id,
                String password,
                int owner_id) throws InterruptedException;

    Flavor getFlavor(String id);

    Map<String, ? extends Number> getInfo(String id);

    List<? extends Flavor> getFlavors();

    List<? extends Image> getImages();


    long getCpuUsage(String id);

    /**
     * 云桌面实例重启方法
     */
    enum RebootMethod {
        /**
         * 硬重启，直接断电重启
         */
        HARD,
        /**
         * 软重启，正常先关机再开机
         */
        SOFT
    }
}
