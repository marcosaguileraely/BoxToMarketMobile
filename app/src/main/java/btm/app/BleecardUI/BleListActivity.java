package btm.app.BleecardUI;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.view.MenuItem;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import btm.app.Adapters.BluethAdapter;
import btm.app.BleecardUI.Mini.MiniUIActivity;
import btm.app.DataHolder.DataHolderBleData;
import btm.app.MainActivity;
import btm.app.Model.BleeDetails;
import btm.app.Model.Bluethoot;
import btm.app.R;
import btm.app.Utils.Utils;

/**
 * Developer: Marcos Aguilera Ely
 * Email: marcosaguileraely@gmail.com
 * BleeCard.com
 *
 * I found some solutions for issues here:
 * https://github.com/bluzDK/android-app/blob/master/app/src/main/java/com/banc/BLEManagement/BLEScanner.java
 * android-app/app/src/main/java/com/banc/BLEManagement/BLEScanner.java
 * Thanks a lot to: https://github.com/bluzDK
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP) // This is needed so that we can use Marshmallow API calls
public class BleListActivity extends AppCompatActivity {
    public static final String TAG = "DEV -> Scan ble *** ";
    Context context = this;

    private Handler          mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner       mLEScanner;
    BluetoothLeScanner       bluetoothLeScanner;
    private ScanCallback     mScanCallback;

    BluethAdapter            adapter;
    BleeDetails              details =  new BleeDetails();
    View                     v;

    ArrayList<Bluethoot>     items = new ArrayList<Bluethoot>();
    List<BluetoothDevice> mBluetoothDevice;

    ListView                 blueList;
    Button                   getDevices;
    AlertDialog.Builder      builder;
    AlertDialog              dialogBle;
    String                   deviceName, deviceMac;
    String                   data;
    //final ArrayList<String>  macAddress = new ArrayList<String>();
    final List<String>  macAddress = new ArrayList<String>();
    final List<String>  filteredMacAddress = new ArrayList<String>();

    // Stops scanning after 10 seconds.
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 3000;
    Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.ui_ble_mini_blee_list_title));

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        int android_version  = Build.VERSION.SDK_INT;
        int android_lollipop = Build.VERSION_CODES.LOLLIPOP;

        Log.d(TAG, " ========> SDK INT: " + android_version);
        Log.d(TAG, " ========> SDK LOLLIPOP: " + android_lollipop);

        //setContentView(R.layout.activity_device_scan);
        //getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();

        blueList    = (ListView) findViewById(R.id.blue_listview1);
        getDevices  = (Button) findViewById(R.id.search_devices_button);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            customDialogYesMove(getString(R.string.error_bluetooth_ble_not_supported));
        }

        // Initializes a Bluetooth adapter.
        // For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter(); // This BluethootAdapter works for versions < LOLLIPOP (21)
        //mLEScanner        = mBluetoothAdapter.getBluetoothLeScanner(); // This BluethootAdaptos works for versions > LOLLIPOP (21)

        // Checks if Bluetooth is supported on the device.
        // if mBluetoothAdapter == null the device not support Bluethoot
        // else, we need to check if the bluethoot is enabled or not
        if (mBluetoothAdapter == null) {
            customDialogYesMove(getString(R.string.error_bluetooth_not_supported));
        }else {
            //
            if (!isBluetoothEnabled()) {
                // Bluetooth is not enable :)
                Log.d(TAG, " ========> BLUETHOOT TURNED OFF");
                enableBTAutomatically(v);
            }else {
                // Bluetooth is not enable :)
                Log.d(TAG, " ========> BLUETHOOT TURNED ON XDXDXDXD ");
                bleeDialog();
                scanLeDevice();
            }
        }

        blueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String typeBlee = adapter.getType(position);
                String imgBlee  = adapter.getImage(position);
                String nameBlee = adapter.getName(position);
                String adrBlee  = adapter.getAddress(position);
                String idBlee   = adapter.getId(position);

                Log.w(TAG, "Position: "+ position +" Set mac addr: " + adrBlee + " - Machine Id: " + idBlee);

                DataHolderBleData.setId(idBlee);
                DataHolderBleData.setImg(imgBlee);
                DataHolderBleData.setMac(adrBlee);
                DataHolderBleData.setName(nameBlee);
                DataHolderBleData.setType(typeBlee);

                utils.wsgetMachines(deviceMac);


                Intent goToMiniUi = new Intent(BleListActivity.this, MiniUIActivity.class);
                startActivity(goToMiniUi);
            }
        });

        getDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //restartBluetooth();
                int totalFound = blueList.getChildCount();
                Log.d(TAG, "-> total ble found: " + totalFound);

                if(totalFound >= 1){
                    adapter.clear();
                    bleeDialog();
                    scanLeDevice();
                }else{
                    bleeDialog();
                    scanLeDevice();
                }
            }
        });

    }

    /*Activity Actions*/
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        //Log.d(TAG, "////////////// Hey, I'm comming from resume.");
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User choose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    * ======================================
    * All WebServices or HTTP call actions *
    * ======================================
    * */

    public void wsgetMachines(String mac_addr){
        try {
            data = new btm.app.Network.NetActions(context).getBleDetails(mac_addr);

            Log.w(TAG, " => " + data);

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

                    details.setId(id);
                    details.setImg_uri(img_uri);
                    details.setType(type);

                    items.add(new Bluethoot( deviceMac, deviceName, details.getId(), details.getImg_uri(), details.getType()) );
                    adapter = new BluethAdapter( context, items );
                    blueList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String nullNameConverter(String name){
        if(name == null){
            return "NoName";
        }else{
            return name;
        }
    }

    public void totalListItems(){
        try{
            int totalFound = blueList.getChildCount();
            Log.d(TAG, "-> total ble found: " + totalFound);

            if(totalFound >= 1){
                //nothing to do
            }else{
                customDialogNoMove(getString(R.string.ui_ble_mini_message_found));
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /*
    * =========================================
    * All Dialogs, Alerts Messages or Loaders *
    * =========================================
    * */

    public void bleeDialog(){
        builder = new AlertDialog.Builder(BleListActivity.this);

        LayoutInflater factory = LayoutInflater.from(BleListActivity.this);
        View view              = factory.inflate(R.layout.ui_aux_ble_loader, null);
        ImageView gifImageView = (ImageView) view.findViewById(R.id.gif_loader);

        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);
        Glide.with(BleListActivity.this)
                .load(R.drawable.bleecard_load)
                .into(imageViewTarget);

        builder.setView(view);
        builder.setCancelable(false);
        dialogBle = builder.create();
        dialogBle.show();
    }

    public void customDialogNoMove(String inDatum){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(inDatum);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void customDialogYesMove(String inDatum){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(inDatum);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Intent gotoHome =  new Intent(context, MainActivity.class);
                startActivity(gotoHome);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
    * =======================
    * All bluethoot actions *
    * =======================
    * */

    // Start scanning Ble
    private void scanLeDevice() {
        //final BluetoothLeScanner

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        //noinspection deprecation
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        Log.d(TAG, " -> -> -> -> stoping LESCAN < LOLLIPOP");
                        listValuesMacAddr();
                    } else {
                        //mLEScanner.stopScan(mScanCallback);
                        //bluetoothLeScanner.stopScan(mScanCallback);
                        //mBluetoothAdapter.getBluetoothLeScanner().stopScan();
                    }
                }
        }, SCAN_PERIOD);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, " -> -> -> -> starting LESCAN < LOLLIPOP");
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
            Log.d(TAG, " -> -> -> -> starting LESCAN > LOLLIPOP");
            Log.d(TAG, " -> -> -> -> RUN METHOD");
            run();
        }
    }

    /**
     * This is the callback for BLE scanning on versions prior to LOLLIPOP
     * It is called each time a device is found so we need to add it to the list
     */
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "/////////////// < LOLLIPOP");
                String device_macaddr = device.toString();
                String device_name    = device.getName();

                deviceName = nullNameConverter(device_name);
                Log.d(TAG, "/////////////// " + device_macaddr + " /////////// " + deviceName);

                if(deviceName.contains("Blee")){ // Only add to ArrayList<String> the Bleemini devices
                    Log.d(TAG, " %%%%%%%%%%%%%%% BLEEMINI FOUND " + device.toString() + " %%%%%%%%%%%%% " + device.getName());
                    macAddress.add(device_macaddr);
                }
            }
        });
        }
    };

    public boolean isBluetoothEnabled(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }

    //This options don't needs the user permission
    public void enableBTAutomatically(View view){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // If the adapter is null it means that the device does not support Bluetooth
            Toast.makeText(context, "Parece que tu dispositivo no soporta Bluethoot.", Toast.LENGTH_LONG).show();
        }else{
            //If the adapter isn't null, it means that the device support Bluethoot
            if (!mBluetoothAdapter.isEnabled()){
                mBluetoothAdapter.enable();
                Toast.makeText(context,"Bluethoot encendido", Toast.LENGTH_SHORT).show();
                bleeDialog();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanLeDevice();
                    }
                }, 2000); //Timer is in ms here.
            }
        }
    }

    /*
    * =======================
    * Another methods *******
    * =======================
    * */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This is the callback for BLE scanning for LOLLIPOP and later
     * It is called each time a devive is found so we need to add it to the list
     */
    @SuppressWarnings("deprecation")
    public void run() {

        Log.d(TAG, " %%%%%%%%%%%%%%%%%%%%%%%%% BLEScanner " + " Running Scan!");
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Log.d(TAG, "BLE Scan Started");
        //Log.d(TAG, "1");

        BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        //Log.d(TAG, "2");

            mScanCallback = new ScanCallback() {
                //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice device = result.getDevice();

                    deviceName = nullNameConverter(device.getName());

                    if(deviceName.contains("Blee")){ // Only add to ArrayList<String> the Bleemini devices
                        Log.w(TAG, " %%%%%%%%%%%%%%% BLEEMINI FOUND " + device.toString() + " %%%%%%%%%%%%% " + device.getName());
                        macAddress.add(device.toString());
                    }
                }

                //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                    //for (ScanResult result : results) {
                        //BluetoothDevice device = result.getDevice();
                        //Log.d(TAG, " oooooooooooooo Devices " + device.toString());

                        //onLeScan(device, result.getRssi(), null);
                    //}
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                }
            };

            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build();
            List<ScanFilter> filters = new ArrayList<>();
            if (scanner != null) {
                scanner.startScan(filters, settings, mScanCallback);
            }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stop();
                listValuesMacAddr();
            }
        }, SCAN_PERIOD);

        //Log.d(TAG, "10");
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        Log.d(TAG, " -> -> -> -> stoping LESCAN > LOLLIPOP ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
            if (scanner != null) {
                scanner.stopScan(mScanCallback);
            }
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    public void listValuesMacAddr(){
        Log.d(TAG, " XXXXXXXXX SIZE: " + macAddress.size());
        for(int i=0; i < macAddress.size(); i++){
            String mac = macAddress.get(i);
            Log.d(TAG, " XXXXXXXXX : " + mac);

            if(!findItemInTheList(mac)){
                filteredMacAddress.add(mac);
                Log.d(TAG, "Item " + mac + " was not found in the list, it's added");
            }else {
                Log.d(TAG, "Item " + mac + " was found in the list, it's ignored.");
            }
        }
        finalListValuesMacAddrFiltered();
    }

    public void finalListValuesMacAddrFiltered(){

        Log.w(TAG, "  SIZE: " + filteredMacAddress.size());

        for(int i=0; i<filteredMacAddress.size(); i++){

            deviceMac = filteredMacAddress.get(i);

            Log.w(TAG, " FILTERED : " + deviceMac);

            wsgetMachines(deviceMac);
        }
        /*
         * After Rendering list ends, we gonna take
         * 1.5 seconds to dismiss the loader message, and check how much
         * Devices were taken
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogBle.dismiss();
                totalListItems();
            }
        }, 1200); //Timer is in ms here.
    }

    private boolean findItemInTheList(String itemToFind) {
        if (filteredMacAddress.contains(itemToFind)) {
            Log.d(TAG, "Item " + itemToFind + " was found in the list");
            return true;
        } else {
            Log.d(TAG, "Item to find" + itemToFind + " was not found in the list");
            return false;
        }
    }

    /*******************
     * THREADS ACTIONS *
     *******************/

    public void restartBluetooth(){
        if (mBluetoothAdapter == null) {
            customDialogYesMove(getString(R.string.error_bluetooth_not_supported));
        }else {
            //
            if (!isBluetoothEnabled()) {
                // Bluetooth is not enable :)
                Log.d(TAG, " ========> BLUETHOOT TURNED OFF");
                enableBTAutomatically(v);
            }else {
                // Bluetooth is not enable :)
                Log.d(TAG, " ========> BLUETHOOT TURNED ON XDXDXDXD ");
                bleeDialog();
                scanLeDevice();
            }
        }
    }
}