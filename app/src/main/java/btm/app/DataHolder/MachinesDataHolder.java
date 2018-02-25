package btm.app.DataHolder;

/**
 * Created by maguilera on 2/24/18.
 */

public class MachinesDataHolder {
    private static String machinedata;
    private static String data_aux;

    public MachinesDataHolder(String machinedata) {
        MachinesDataHolder.machinedata = machinedata;

    }

    public static String getMachinedata() {
        return machinedata;
    }

    public String returnMachineResponse(String value){
        String retval = "";
        String[] List;
        for(int i=0; i<2; i++){
            retval+= value;
        }
        return retval;
    }

    public static void setMachinedata(String machinedata) {
        MachinesDataHolder.machinedata = machinedata;
    }

    public static String getData_aux() {
        return data_aux;
    }

    public static void setData_aux(String data_aux) {
        MachinesDataHolder.data_aux = data_aux;
    }

}
