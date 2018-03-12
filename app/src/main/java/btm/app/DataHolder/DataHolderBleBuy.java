package btm.app.DataHolder;

/**
 * Created by maguilera on 2/26/18.
 */

public class DataHolderBleBuy {
    private static String liSelected;
    private static String liNameSeleted;

    public static String getLiNameSeleted() {
        return liNameSeleted;
    }

    public static void setLiNameSeleted(String liNameSeleted) {
        DataHolderBleBuy.liNameSeleted = liNameSeleted;
    }


    public static String getLiSelected() {
        return liSelected;
    }

    public static void setLiSelected(String liSelected) {
        DataHolderBleBuy.liSelected = liSelected;
    }


}
