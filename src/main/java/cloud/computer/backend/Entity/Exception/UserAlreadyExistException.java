package cloud.computer.backend.Entity.Exception;

import cloud.computer.backend.Entity.User;

public class UserAlreadyExistException extends UserException{
    private User DuplicateUser;



    public User getDuplicateUser() {
        return DuplicateUser;
    }

    public void setDuplicateUser(User duplicateUser) {
        DuplicateUser = duplicateUser;
    }
}
