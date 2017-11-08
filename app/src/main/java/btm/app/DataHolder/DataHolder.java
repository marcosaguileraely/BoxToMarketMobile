package btm.app.DataHolder;

/**
 * Created by maguilera on 11/8/17.
 */

public class DataHolder {
    private static String data;
    private static String username;
    private static String pass;
    private static String id_country;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        DataHolder.username = username;
    }

    public static String getPass() {
        return pass;
    }

    public static void setPass(String pass) {
        DataHolder.pass = pass;
    }

    public static String getId_country() {
        return id_country;
    }

    public static void setId_country(String id_country) {
        DataHolder.id_country = id_country;
    }

    public static String getData() {
        return data;
    }

    public static String setData(String data) {
       return DataHolder.data = data;
    }
}
