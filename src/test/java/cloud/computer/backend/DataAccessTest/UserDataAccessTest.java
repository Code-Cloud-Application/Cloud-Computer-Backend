package cloud.computer.backend.DataAccessTest;

import cloud.computer.backend.DataAccess.UserDataAccess;
import cloud.computer.backend.Entity.Exception.UserAlreadyExistException;
import cloud.computer.backend.Entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserDataAccessTest {

    private UserDataAccess userDataAccess;

    @Autowired
    public void setUserDataAccess(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    @Test
    public void getUsers(){
        System.out.println(this.userDataAccess.getUsers());
    }

    @Test
    public void operate() throws UserAlreadyExistException {
        int size = this.userDataAccess.getUsers().size();
        User user = new User(10000, "test", "123456");
        this.userDataAccess.addUser(user);
        UserAlreadyExistException exception = Assertions.assertThrowsExactly(UserAlreadyExistException.class, () -> this.userDataAccess.addUser(user));
        Assertions.assertEquals(exception.getUser(), user);
        Assertions.assertEquals(this.userDataAccess.getUsers().size(), size+1);
        user.setUsername("test02");
        user.setPassword("654321");
        Assertions.assertDoesNotThrow(() -> userDataAccess.updateUser(user));
        Assertions.assertEquals(user, this.userDataAccess.getUser(user.getId()));
        this.userDataAccess.removeUser(10000);
        Assertions.assertEquals(this.userDataAccess.getUsers().size(), size);
    }
}
