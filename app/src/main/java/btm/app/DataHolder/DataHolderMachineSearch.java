package btm.app.DataHolder;

import android.content.Intent;
import android.util.Log;

/**
 * Created by maguilera on 9/10/18.
 */

public class DataHolderMachineSearch {
    public static String search_data;
    public static Integer total_pay;
    public static String machine_code;
    public static String lines_to_pay;

    public static String getLines_to_pay() {
        return lines_to_pay;
    }

    public static void setLines_to_pay(String lines_to_pay) {
        DataHolderMachineSearch.lines_to_pay = lines_to_pay;
    }

    public static String getMachine_code() {
        return machine_code;
    }

    public static void setMachine_code(String machine_code) {
        DataHolderMachineSearch.machine_code = machine_code;
    }

    public static Integer getTotal_pay() {
        return total_pay;
    }

    public static void setTotal_pay(Integer total_pay) {
        DataHolderMachineSearch.total_pay = total_pay;
        Log.d("-->", "data holder total pay: " + total_pay);
    }

    public static String getSearch_data() {
        return search_data;
    }

    public static void setSearch_data(String search_data) {
        Log.d("-->", "data holder : " + search_data);
        DataHolderMachineSearch.search_data = search_data;
    }

}
