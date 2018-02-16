package btm.app.DataHolder;

/**
 * Created by maguilera on 11/8/17.
 */

public class DataHolderBleeRsa {
    public static String getRsaKey() {
        return rsaKey;
    }

    public static void setRsaKey(String rsaKey) {
        DataHolderBleeRsa.rsaKey = rsaKey;
    }

    private static String rsaKey;
}
