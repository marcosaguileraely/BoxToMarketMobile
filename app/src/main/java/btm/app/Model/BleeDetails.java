package btm.app.Model;

import java.util.ArrayList;

/**
 * Created by maguilera on 11/27/17.
 */

public class BleeDetails {
    private static String id;
    private static String img_uri;
    private static String prices;

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        BleeDetails.id = id;
    }

    public static String getImg_uri() {
        return img_uri;
    }

    public static void setImg_uri(String img_uri) {
        BleeDetails.img_uri = img_uri;
    }

    public static String getPrices() {
        return prices;
    }

    public static void setPrices(String prices) {
        BleeDetails.prices = prices;
    }
}
