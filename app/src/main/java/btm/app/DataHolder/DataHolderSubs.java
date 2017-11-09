package btm.app.DataHolder;

/**
 * Created by maguilera on 11/8/17.
 */

public class DataHolderSubs {
    private static int id;
    private static String name;
    private static String img_url;
    private static String product_type;
    private static String price;

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        DataHolderSubs.id = id;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        DataHolderSubs.name = name;
    }

    public static String getImg_url() {
        return img_url;
    }

    public static void setImg_url(String img_url) {
        DataHolderSubs.img_url = img_url;
    }

    public static String getProduct_type() {
        return product_type;
    }

    public static void setProduct_type(String product_type) {
        DataHolderSubs.product_type = product_type;
    }

    public static String getPrice() {
        return price;
    }

    public static void setPrice(String price) {
        DataHolderSubs.price = price;
    }

}
