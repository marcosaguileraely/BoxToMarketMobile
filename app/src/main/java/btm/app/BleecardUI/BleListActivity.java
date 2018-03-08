package btm.app.BleecardUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import btm.app.Adapters.BluethAdapter;
import btm.app.DataHolder.DataHolderBleData;
import btm.app.Model.BleeDetails;
import btm.app.Model.Bluethoot;
import btm.app.R;

public class BleListActivity extends AppCompatActivity {
    public static final String TAG = "DEV -> Scan ble *** ";
    Context context = this;

    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    BluethAdapter adapter;
    BleeDetails details =  new BleeDetails();

    ArrayList<Bluethoot> items = new ArrayList<Bluethoot>();
    ListView blueList;

    Button getDevices;
    AlertDialog.Builder builder;
    AlertDialog dialogBle;
    private boolean mScanning;
    String deviceName, deviceMac, deviceMacAux = "empty";
    String data;

    // Stops scanning after 10 seconds.
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

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

        //setContentView(R.layout.activity_device_scan);
        //getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();

        blueList    = (ListView) findViewById(R.id.blue_listview1);
        getDevices  = (Button) findViewById(R.id.search_devices_button);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Device not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }else {
            scanLeDevice();
        }

        blueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String typeBlee = adapter.getType(position);
                String imgBlee  = adapter.getImage(position);
                String nameBlee = adapter.getName(position);
                String adrBlee  = adapter.getAddress(position);
                String idBlee  = adapter.getId(position);
                //Toast.makeText(context, "Item clicked, "+" pos: " + position + " Id: " + idBlee + " Type: " + typeBlee + " Mac: " + adrBlee, Toast.LENGTH_SHORT).show();

                DataHolderBleData.setId(idBlee);
                DataHolderBleData.setImg(imgBlee);
                DataHolderBleData.setMac(adrBlee);
                DataHolderBleData.setName(nameBlee);
                DataHolderBleData.setType(typeBlee);

                Intent goToMiniUi = new Intent(BleListActivity.this, BleMiniUI.class);
                startActivity(goToMiniUi);
            }
        });

        getDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleeDialog();
                scanLeDevice();
            }
        });

        bleeDialog();
    }

    /*Activity Actions*/
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        mBluetoothAdapter.startLeScan(mLeScanCallback);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }, SCAN_PERIOD);
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
                    String prices  = json_obj.getString("type");

                    details.setId(id);
                    details.setImg_uri(img_uri);
                    details.setType(prices);

                    items.add(new Bluethoot(deviceMac, deviceName, details.getId(),details.getImg_uri(),details.getType()));
                    adapter = new BluethAdapter(context, items);
                    blueList.setAdapter(adapter);
                    //adapter.notifyDataSetChanged();
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogBle.dismiss();
                totalListItems();
            }
        }, 5000); //Timer is in ms here.
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

    /*
    * =======================
    * All bluethoot actions *
    * =======================
    * */

    // Start scanning Ble
    private void scanLeDevice() {
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }, SCAN_PERIOD);
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    deviceName = nullNameConverter(device.getName());
                    deviceMac  = nullNameConverter(device.getAddress());
                    Log.d(TAG, " -> Device name: " + deviceName + " Device Mac Address: " + deviceMac);

                    //if(deviceName.startsWith("Bl")){ //<-- You need to change it
                    if(deviceName.contains("HM")){
                        if(!deviceMac.equals(deviceMacAux)){  //it's not working good, as expected!
                            wsgetMachines(deviceMac);
                        }else{
                            //nothing to-do
                        }
                        deviceMacAux = deviceMac;
                    }else {
                        Log.d(TAG, "-> Not BLE device");
                    }
                }
            });
        }
    };

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
}
