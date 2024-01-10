package cloud.computer.backend.Service;

import cloud.computer.backend.DataAccess.UserDataAccess;
import cloud.computer.backend.Entity.AuthenticationResult;
import cloud.computer.backend.Entity.Exception.UserAlreadyExistException;
import cloud.computer.backend.Entity.User;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserDataAccess userDataAccess;

    public AuthenticationService(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    public AuthenticationResult authenticate(String username, String password){
        User user = this.userDataAccess.getUser(username);
        AuthenticationResult result = new AuthenticationResult();
        if (user == null) {
            result.setResult(false);
            result.setReason(AuthenticationResult.Reason.USER_NOT_FOUND);
        } else if (!user.getPassword().equals(password)) {
            result.setResult(false);
            result.setReason(AuthenticationResult.Reason.WRONG_PASSWORD);
        } else {
            result.setResult(true);
        }
        return result;
    }

    public AuthenticationResult addUser(String username, String password){
        AuthenticationResult result = new AuthenticationResult();
        try {
            userDataAccess.addUser(new User(userDataAccess.getMaxId() + 1, username, password));
            result.setResult(true);
        } catch (UserAlreadyExistException e) {
            result.setResult(false);
            result.setReason(AuthenticationResult.Reason.USER_ALREADY_EXIST);
        }
        return result;
    }
}
