package btm.app.Model;

/**
 * Created by maguilera on 11/6/17.
 */

public class SubscriptionsPublic {
    private String value;
    private String title;
    private String img_uri;
    private int id;

    public SubscriptionsPublic(String value, String title, String img_uri, int id) {
        this.value = value;
        this.title = title;
        this.img_uri = img_uri;
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_uri() {
        return img_uri;
    }

    public void setImg_uri(String img_uri) {
        this.img_uri = img_uri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }




}
