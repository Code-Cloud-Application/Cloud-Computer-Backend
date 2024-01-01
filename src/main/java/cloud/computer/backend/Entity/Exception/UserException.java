package cloud.computer.backend.Entity.Exception;

import cloud.computer.backend.Entity.User;

public class UserException extends Exception{

    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser(){
        return this.user;
    }
}
