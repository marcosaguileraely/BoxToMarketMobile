package btm.app.Model;

/**
 * Created by maguilera on 11/11/17.
 */

public class Bluethoot {
    public Bluethoot(String mac, String name, String id, String img, String type) {
        this.mac = mac;
        this.name = name;
        this.id = id;
        this.img = img;
        this.type = type;
    }

    private String mac;
    private String name;
    private String id;
    private String img;
    private String type;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



}
