package btm.app.Model;

import java.util.ArrayList;

/**
 * Created by maguilera on 11/27/17.
 */

public class BleeDetails {
    private String id;
    private String img_uri;
    private String type;

    public BleeDetails(String id, String img_uri, String type) {
        this.id = id;
        this.img_uri = img_uri;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg_uri() {
        return img_uri;
    }

    public void setImg_uri(String img_uri) {
        this.img_uri = img_uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
