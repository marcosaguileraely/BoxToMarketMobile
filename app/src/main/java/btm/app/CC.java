package btm.app;

import android.util.Log;

/**
 * Created by aple on 21/02/17.
 */

public class CC {

    private String id;
    private String num;

    public CC(String inf) {
        Log.d("CC", inf);
        String[] info = inf.split(",");
        this.id = info[0];
        this.num = info[1];
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return num;
    }
}
