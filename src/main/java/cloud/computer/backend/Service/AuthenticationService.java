package cloud.computer.backend.Service;

import cloud.computer.backend.DataAccess.UserDataAccess;
import cloud.computer.backend.Entity.AuthenticationResult;
import cloud.computer.backend.Entity.Reason;
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
            result.setReason(Reason.USER_NOT_FOUND);
        } else if (!user.getPassword().equals(password)) {
            result.setResult(false);
            result.setReason(Reason.WRONG_PASSWORD);
        } else {
            result.setResult(true);
        }
        return result;
    }
}
