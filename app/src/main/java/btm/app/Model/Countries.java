package btm.app.Model;

/**
 * Created by maguilera on 3/26/18.
 */

public class Countries {
    int id;
    String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Countries(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
