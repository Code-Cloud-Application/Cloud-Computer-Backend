package cloud.computer.backend.Service;

import cloud.computer.backend.DataAccess.TokenDataAccess;
import cloud.computer.backend.DataAccess.UserDataAccess;
import cloud.computer.backend.Entity.Token;
import cloud.computer.backend.Entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {
    private final TokenDataAccess tokenDataAccess;
    private final UserDataAccess userDataAccess;

    @Value("${token.period}")
    private int period;

    public TokenService(TokenDataAccess tokenDataAccess, UserDataAccess userDataAccess) {
        this.tokenDataAccess = tokenDataAccess;
        this.userDataAccess = userDataAccess;
    }

    public String assignToken(String username){
        String token = UUID.randomUUID().toString();
        int id = this.tokenDataAccess.getMaxId() + 1;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, period);
        Date date = calendar.getTime();
        int user_id = this.userDataAccess.getUser(username).getId();
        this.tokenDataAccess.addToken(new Token(id, token, date, user_id));
        return token;
    }

    public void revokeALlTokens(String username){
        int user_id = this.userDataAccess.getUser(username).getId();
        this.tokenDataAccess.removeTokenByOwnerId(user_id);
    }
}
