package btm.app.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

//import com.polkapolka.bluetooth.le.DataHolder.AuxDataHolder;
import btm.app.DataHolder.AuxDataHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by maguilera on 4/24/18.
 */

public class Utils extends Activity {
    private final static String TAG = "DEV -> UTILS ";

    String dataResponse, dataResponseLineValue;
    String rsa;
    String data;
    String dataResponseMachineSearched; // Save String response from WS for machine searched details

    Context context = this;
    AuxDataHolder auxDataHolder = new AuxDataHolder();

    public String getStringJson(String inDatum){
        try {
            JSONObject obj = new JSONObject(inDatum);
            rsa = obj.getString("rsaToken");
            Log.d(TAG, "Rsa = " + rsa);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rsa;
    }

    public String getRsaBle(String IdBle){
        // Create a job to run on background
        try {
            dataResponse = new btm.app.Network.NetActions(context).getBleRsa(IdBle);

            Log.d(TAG, " oKHttp response: " + dataResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }  catch (JSONException e) {
            e.printStackTrace();
        }
        return dataResponse;
    }

    public String buyRsaBle(String IdBle){
        // Create a job to run on background
        try {
            dataResponse = new btm.app.Network.NetActions(context).getBleBuyRsa(IdBle);

            Log.d(TAG, " oKHttp response: " + dataResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }  catch (JSONException e) {
            e.printStackTrace();
        }
        return dataResponse;
    }

    /*
    * MachineId is the unique number for each Blee Machine
    * You can get it using the WS endpoint: https://www.bleecard.com/api/getMachines.do
    * For more information you can check the bleecard documentation
    * */
    public String getLinesData(String MachineId){
        // Create a job to run on background
        try {
            dataResponseLineValue = new btm.app.Network.NetActions(context).getBlePriceAndNames(MachineId);
            Log.d(TAG, " oKHttp response: " + dataResponseLineValue);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataResponseLineValue;
    }

    public String getPowerLinesData(String MachineId){
        try {
            dataResponseLineValue = new btm.app.Network.NetActions(context).btmPowerPriceAndNames(MachineId);
            Log.d(TAG, " oKHttp response: " + dataResponseLineValue);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataResponseLineValue;
    }

    public String gettingBackMoney(String inDatum){
        try {
            String dataResponse = new btm.app.Network.NetActions(context).btmMiniChargeBack(inDatum);
            Log.d(TAG, " oKHttp response: " + dataResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataResponse;
    }

    public String getVendingPriceData(String MachineId){
        try {
            dataResponseLineValue = new btm.app.Network.NetActions(context).btmVendingPriceAndNames(MachineId);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataResponseLineValue;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        try {
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i + 1), 16));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public void wsgetMachines(String mac_addr){
        try {
            data = new btm.app.Network.NetActions(context).getBleDetails(mac_addr);
            Log.d(TAG, " => " + data);
            bleeDetailsConverter(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void bleeDetailsConverter(String data) {
        JSONArray jsonArray = null;

        if(data.contains("Machines Found 0")){
            //nothing to do
        }else {
            try {
                jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject json_obj = jsonArray.getJSONObject(i);
                    Log.d(TAG, json_obj.toString());
                    String id      = json_obj.getString("id");
                    String img_uri = json_obj.getString("image");
                    String type  = json_obj.getString("type");

                    auxDataHolder.setMachine_id(id);
                    auxDataHolder.setMachine_image(img_uri);
                    auxDataHolder.setMachine_type(type);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static int convert(int n) {
        return Integer.valueOf(String.valueOf(n), 16);
    }

    public String getSearchDetails(String machineId){
        try{
            Log.w(TAG, " Machine Id: " + machineId);
            dataResponseMachineSearched = new btm.app.Network.NetActions(context).getListMachineSearched(machineId);

        } catch (IOException e){
            e.printStackTrace();
        }

        return dataResponseMachineSearched;
    }

    public String getMachine(){
        try{
            dataResponseMachineSearched = new btm.app.Network.NetActions(context).getListMachine();

        } catch (IOException e){
            e.printStackTrace();
        }

        return dataResponseMachineSearched;
    }

}
