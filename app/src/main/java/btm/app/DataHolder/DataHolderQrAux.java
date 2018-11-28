package btm.app.DataHolder;

/**
 * Created by maguilera on 10/16/18.
 */

public class DataHolderQrAux {
    private static String response;
    private static String BigResponse;

    public static String getBigResponse() {
        return BigResponse;
    }

    public static void setBigResponse(String bigResponse) {
        BigResponse = bigResponse;
    }

    public static String getResponse() {
        return response;
    }

    public static void setResponse(String response) {
        DataHolderQrAux.response = response;
    }
}
