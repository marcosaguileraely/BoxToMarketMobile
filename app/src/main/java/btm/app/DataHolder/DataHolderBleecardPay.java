package btm.app.DataHolder;

/**
 * Created by maguilera on 5/3/18.
 */

public class DataHolderBleecardPay {
    private static String lineSelected;
    private static String lineNameSeleted;
    private static String rsaLineSelected;

    public static String getRsaLineSelected() {
        return rsaLineSelected;
    }

    public static String getLineNameSeleted() {
        return lineNameSeleted;
    }

    public static String getLineSelected() {
        return lineSelected;
    }

    public static void setLineNameSeleted(String lineNameSeleted) {
        DataHolderBleecardPay.lineNameSeleted = lineNameSeleted;
    }

    public static void setLineSelected(String lineSelected) {
        DataHolderBleecardPay.lineSelected = lineSelected;
    }

    public static void setRsaLineSelected(String rsaLineSelected) {
        DataHolderBleecardPay.rsaLineSelected = rsaLineSelected;
    }
}
