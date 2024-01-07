package cloud.computer.backend.Entity;

import java.util.Calendar;
import java.util.Date;

public class Server {
    private String id;
    private String name;
    private String address;
    private String ImageId;
    private String FlavorId;
    private String status;
    private int OwnerId;
    private final Date created = Calendar.getInstance().getTime();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageId() {
        return ImageId;
    }

    public void setImageId(String imageId) {
        ImageId = imageId;
    }

    public String getFlavorId() {
        return FlavorId;
    }

    public void setFlavorId(String flavorId) {
        FlavorId = flavorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getOwnerId() {
        return OwnerId;
    }

    public void setOwnerId(int ownerId) {
        OwnerId = ownerId;
    }

    public Date getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "Server{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", ImageId='" + ImageId + '\'' +
                ", FlavorId='" + FlavorId + '\'' +
                ", status='" + status + '\'' +
                ", OwnerId=" + OwnerId +
                ", created=" + created +
                '}';
    }
}
