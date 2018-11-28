package btm.app.DataHolder;

/**
 * Created by maguilera on 10/9/18.
 */

public class DataHolderQrActivesDetails {
    private static String serial;
    private static String qr_image_ui;
    private static String value;
    private static String date;

    public static String getDate() {
        return date;
    }

    public static void setDate(String date) {
        DataHolderQrActivesDetails.date = date;
    }

    public static String getSerial() {
        return serial;
    }

    public static void setSerial(String serial) {
        DataHolderQrActivesDetails.serial = serial;
    }

    public static String getQr_image_ui() {
        return qr_image_ui;
    }

    public static void setQr_image_ui(String qr_image_ui) {
        DataHolderQrActivesDetails.qr_image_ui = qr_image_ui;
    }

    public static String getValue() {
        return value;
    }

    public static void setValue(String value) {
        DataHolderQrActivesDetails.value = value;
    }
}
