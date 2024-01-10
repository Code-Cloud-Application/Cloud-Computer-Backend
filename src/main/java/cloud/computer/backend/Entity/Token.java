package cloud.computer.backend.Entity;

import java.util.Calendar;
import java.util.Date;

public class Token extends OpenStackEntity{
    private int id;
    private String value;
    private Date expired_at;
    private int owner_id;
    private boolean expired;

    public Token(int id, String value, Date expired_at, int owner_id) {
        this.id = id;
        this.value = value;
        this.expired_at = expired_at;
        this.owner_id = owner_id;
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        this.expired = date.after(expired_at);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getExpiredAt() {
        return expired_at;
    }

    public void setExpiredAt(Date expired_at) {
        this.expired_at = expired_at;
    }

    public int getOwnerId() {
        return owner_id;
    }

    public void setOwnerId(int owner_id) {
        this.owner_id = owner_id;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
