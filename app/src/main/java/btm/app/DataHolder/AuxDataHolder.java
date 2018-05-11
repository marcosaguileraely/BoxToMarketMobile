package btm.app.DataHolder;

/**
 * Created by maguilera on 4/20/18.
 */

public class AuxDataHolder {

    private static String deviceName;
    private static String macaddress;
    private static String machine_id;
    private static String machine_image;
    private static String machine_type;

    public static String getDeviceName() {
        return deviceName;
    }

    public static void setDeviceName(String deviceName) {
        AuxDataHolder.deviceName = deviceName;
    }

    public static String getMachine_id() {
        return machine_id;
    }

    public static void setMachine_id(String machine_id) {
        AuxDataHolder.machine_id = machine_id;
    }

    public static String getMachine_image() {
        return machine_image;
    }

    public static void setMachine_image(String machine_image) {
        AuxDataHolder.machine_image = machine_image;
    }

    public static String getMachine_type() {
        return machine_type;
    }

    public static void setMachine_type(String machine_type) {
        AuxDataHolder.machine_type = machine_type;
    }

    public static void setMacaddress(String macaddress) {
        AuxDataHolder.macaddress = macaddress;
    }

    public static String getMacaddress() {
        return macaddress;
    }

}
