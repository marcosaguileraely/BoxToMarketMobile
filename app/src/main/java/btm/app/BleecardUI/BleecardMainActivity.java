package btm.app.BleecardUI;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
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

import com.android.volley.Response;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import btm.app.Adapters.BluethAdapter;
import btm.app.Model.BleeDetails;
import btm.app.Model.Bluethoot;
import btm.app.R;

public class BleecardMainActivity extends AppCompatActivity {

    public static final String TAG = "DEV -> Bluethoot ";

    AlertDialog.Builder builder;
    AlertDialog dialog;

    public Context context = this;
    ArrayList<BleeDetails> items = new ArrayList<BleeDetails>();
    BluethAdapter adapter;
    BluetoothAdapter mBluetoothAdapter;

    String data, uuid0_unique, id, img_uri, type;
    ListView blueList;
    Button getDevices;
    public View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleecard_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        blueList    = (ListView) findViewById(R.id.blue_listview);
        getDevices  = (Button) findViewById(R.id.search_devices_button);

        Log.d(TAG, "forzar encendido del BT");
        enableBTFull(v);
        bleeDialog();
        Log.d(TAG, "inicio reconocimeinto dispositivos");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        Log.d(TAG, "Inicio busqueda");
        //registerReceiver(mReceiver, filter); //this options isn't efficient
        btnDiscover(v);

        Log.d(TAG, "onClick: enabling/disabling bluetooth.");
        //enableDisableBT();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {  // <-- Do something after 2000ms
                btnDiscover(v);
            }
        }, 2000);

        getDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchAgainManually(v);
            }
        });

        blueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String macAddr = adapter.getMacAddr(position);
                Toast.makeText(context, "Item clicked, "+" pos: " + position + " Id: " + macAddr, Toast.LENGTH_SHORT).show();
                String object = getBleecardData(macAddr);
                Log.d(TAG, "Json: " + object);
                getBleeDetails(view, macAddr.toLowerCase());
            }
        });
    }

    public void bleeDetailsConverter(String data) {

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(data);

            for (int i=0; i < jsonArray.length(); i++){
                JSONObject json_obj = jsonArray.getJSONObject(i);
                Log.d(TAG, json_obj.toString());
                id      = json_obj.getString("id");
                img_uri = json_obj.getString("image");
                type    = json_obj.getString("type");
                //new BleeDetails(id, img_uri, prices);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getBleecardData(String macaddr){
        JSONArray jsonArray   = new JSONArray();
        JSONArray jsonArray2  = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("mac", macaddr);
            jsonArray.put(jsonObject);
            jsonArray2.put(jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "-> "+jsonArray2.toString());
        return jsonArray2.toString();
    }

    public JSONObject getMacsJsonObject(String macaddr){
        //JSONArray jsonArray   = new JSONArray();
        //JSONArray jsonArray2  = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("mac", macaddr);
            //jsonArray.put(jsonObject);
            //jsonArray2.put(jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "-> " + jsonObject.toString());
        return jsonObject;
    }

    public void searchAgainManually(View view){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "-> Way manually");
                //Do something after 2000ms
                //clearing the data before new search of devices
                if(adapter.isEmpty()){
                    //nothing to do
                }else {
                    adapter.clear();
                }
                adapter.notifyDataSetChanged();
                bleeDialog();

                //Enabling the bluethoot hardware
                enableBTFull(v);
                //Starting a new search
                btnDiscover(v);
            }
        }, 2000);
    }

    //This options need the user permission
    public void enableBT(View view){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()){
            Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // The REQUEST_ENABLE_BT constant passed to startActivityForResult()
            // is a locally defined integer (which must be greater than 0),
            // that the system passes back to you in your onActivityResult()

            // implementation as the requestCode parameter.
            int REQUEST_ENABLE_BT = 1;
            startActivityForResult(intentBtEnabled, REQUEST_ENABLE_BT);
        }
    }

    //This options don't needs the user permission
    public void enableBTFull(View view){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // If the adapter is null it means that the device does not support Bluetooth
            Toast.makeText(context, "Parece que tu dispositivo no soporta Bluethoot.", Toast.LENGTH_LONG).show();
        }else{
            //If the adapter isn't null, it means that the device support Bluethoot
            if (!mBluetoothAdapter.isEnabled()){
                mBluetoothAdapter.enable();
                Toast.makeText(context,"Bluethoot encendido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    * Cuando se llame este método navegará del listado blee a el detalle del
    * bleecard seleccionado.
    * */
    public void navigateToDetails(){
        Intent goToBleeDetails = new Intent(context, BleecardDetailsActivity.class);
        startActivity(goToBleeDetails);
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "-> Way automatic");

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                String object = getBleecardData(deviceName);
                Log.d(TAG, "Object " + object);
                Log.d(TAG, "Data " + "Name: " + deviceName + " Mac: "+ deviceHardwareAddress);
            }
        }
    };

    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) throws NullPointerException {
            JSONArray jsonArray2  = new JSONArray();
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: " + device.getName() + " : " + device.getAddress() );

                try {
                    uuid0_unique = getUuid();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                try {
                    data = new btm.app.Network.NetActions(context).getBleDetails(uuid0_unique);
                    Log.d(TAG, "=> " + data);
                    bleeDetailsConverter(data);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                items.add( new BleeDetails( id, img_uri, type ));

                /*if(deviceName == null){
                    Log.d(TAG, "onReceive: " + "Device with null name filtered");
                }
                else{
                    if (deviceName.equals("BLECard")){
                        Log.d(TAG, "onReceive: " + "hay al menos un dispositivo blee" );

                        items.add(new Bluethoot(device.getName(), device.getAddress(), device.getAddress()));
                        //items.add(new Bluethoot("BLECard1", "50:8C:B1:6A:68:0E", "50:8C:B1:6A:68:0E"));
                        //items.add(new Bluethoot("BLECard2", "18:93:D7:54:F7:A4", "18:93:D7:54:F7:A4"));
                        //items.add(new Bluethoot("BLECard3", "50:8C:B1:6A:68:0E", "50:8C:B1:6A:68:0E"));
                        //items.add(new Bluethoot("BLECard4", "34:15:13:f4:59:f1", "34:15:13:d4:59:f1"));

                    }else {
                        Log.d(TAG, "onReceive: " + "No BLECard device detected");
                    }
                }*/
            }

            adapter = new BluethAdapter(context, items);
            blueList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    };

    public void getBleeDetails(View view, String bleeaddr){
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.inf_dialog));
        //progress.show();

        Response.Listener<String> response = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "1 "+ response);
                progress.dismiss();

                if(response.contains(".jpg")){
                    Log.d(TAG, "Tiene al menos un resultado. " + response + " length: "+response.length());
                    bleeDetailsConverter(response);
                    navigateToDetails();

                } else {
                    Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                }
            }
        };

        try {
            new btm.app.Network.NetActions(this).getBleeCardData(response, bleeaddr, progress);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUuid() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
        ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter, null);

        String uuid0 = "";

        for(int i = 0; i<uuids.length; i++){
            uuid0 = uuids[0].getUuid().toString();
            Log.d(TAG, "UUID: " + uuid0);
        }

        return uuid0;
    }

    /**
     *
     * This method initialized the Bluethoot devices search
     */
    public void btnDiscover(View view) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            bleeDialog();
            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){
            dialog.dismiss();
            //check BT permissions in manifest
            checkBTPermissions();
            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    public void btnEnableDisable_Discoverable(View view) {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2,intentFilter);

    }

    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){

            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

    }

    /**
     * This method is required for all devices running API 23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    public void bleeDialog(){
        builder = new AlertDialog.Builder(BleecardMainActivity.this);

        LayoutInflater factory = LayoutInflater.from(BleecardMainActivity.this);
        View view              = factory.inflate(R.layout.ui_aux_ble_loader, null);
        ImageView gifImageView = (ImageView) view.findViewById(R.id.gif_loader);

        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);
        Glide.with(BleecardMainActivity.this)
             .load(R.drawable.bleecard_load)
             .into(imageViewTarget);

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        // Don't forget to unregister the ACTION_FOUND receiver.
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver1);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver2);
    }

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
