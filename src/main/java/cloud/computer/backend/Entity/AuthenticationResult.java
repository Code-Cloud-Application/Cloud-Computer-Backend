package cloud.computer.backend.Entity;

public class AuthenticationResult extends OpenStackEntity{
    private boolean result;
    private Reason reason;

    public AuthenticationResult() {
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public boolean getResult() {
        return result;
    }

    public Reason getReason() {
        return reason;
    }

    public enum Reason{
        USER_NOT_FOUND,
        WRONG_PASSWORD,
        USER_ALREADY_EXIST
    }
}

