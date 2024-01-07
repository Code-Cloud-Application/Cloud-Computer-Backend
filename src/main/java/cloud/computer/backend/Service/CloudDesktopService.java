package cloud.computer.backend.Service;

import cloud.computer.backend.Entity.Server;

import java.util.List;

public interface CloudDesktopService {
    List<Server> getCloudDesktops(int user_id);

    void powerOn(Server server);

    void powerOff(Server server);

    void restart(Server server, RestartMethod method);

    void remove(String server_id);


    enum RestartMethod{
        HARD,
        SOFT
    }
}
