package cloud.computer.backend.Entity;

public class Volume extends OpenStackEntity{
    private String id;
    private String desktopId;
    private int size;
    private boolean bootable;

    public Volume(String id, String desktopId, int size, boolean bootable) {
        this.id = id;
        this.desktopId = desktopId;
        this.size = size;
        this.bootable = bootable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesktopId() {
        return desktopId;
    }

    public void setDesktopId(String desktopId) {
        this.desktopId = desktopId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isBootable() {
        return bootable;
    }

    public void setBootable(boolean bootable) {
        this.bootable = bootable;
    }
}
