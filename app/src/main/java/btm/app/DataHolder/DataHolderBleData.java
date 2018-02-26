package btm.app.DataHolder;

/**
 * Created by maguilera on 2/25/18.
 */

public class DataHolderBleData {
    private static String type;
    private static String id;
    private static String name;
    private static String img;
    private static String mac;

    public static String getType() {
        return type;
    }

    public static void setType(String type) {
        DataHolderBleData.type = type;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        DataHolderBleData.id = id;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        DataHolderBleData.name = name;
    }

    public static String getImg() {
        return img;
    }

    public static void setImg(String img) {
        DataHolderBleData.img = img;
    }

    public static String getMac() {
        return mac;
    }

    public static void setMac(String mac) {
        DataHolderBleData.mac = mac;
    }

}
