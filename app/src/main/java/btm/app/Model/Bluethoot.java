package btm.app.Model;

/**
 * Created by maguilera on 11/11/17.
 */

public class Bluethoot {
    private String uuid;
    private String mac;
    private String address;

    public Bluethoot(String uuid, String mac, String address) {
        this.uuid = uuid;
        this.mac = mac;
        this.address = address;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }






}
