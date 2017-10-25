package btm.app.Model;

/**
 * Created by maguilera on 10/23/17.
 */

public class Subscriptions {

    public int id;
    public int qty;
    public String img_uri;

    public Subscriptions(int id, int qty, String img_uri) {
        this.id = id;
        this.qty = qty;
        this.img_uri = img_uri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getImg_uri() {
        return img_uri;
    }

    public void setImg_uri(String img_uri) {
        this.img_uri = img_uri;
    }

}
