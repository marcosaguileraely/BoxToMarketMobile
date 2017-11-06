package btm.app.Model;

/**
 * Created by maguilera on 11/5/17.
 */

public class Clubs {
    public String id;
    public String title;
    public String img_uri;

    public Clubs(String id, String title, String img_uri) {
        this.id = id;
        this.title = title;
        this.img_uri = img_uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
