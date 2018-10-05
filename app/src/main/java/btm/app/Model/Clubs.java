package btm.app.Model;

/**
 * Created by maguilera on 11/5/17.
 */

public class Clubs {

    public String type;
    public String img_uri;
    public String available_qty;

    public Clubs(String type, String available_qty, String img_uri) {
        this.type = type;
        this.available_qty = available_qty;
        this.img_uri = img_uri;
    }

    public String getTitle() {
        return type;
    }

    public void setTitle(String type) {
        this.type = type;
    }

    public String getImg_uri() {
        return img_uri;
    }

    public void setImg_uri(String img_uri) {
        this.img_uri = img_uri;
    }

    public String getAvailable_qty() {
        return available_qty;
    }

    public void setAvailable_qty(String available_qty) {
        this.available_qty = available_qty;
    }


}
