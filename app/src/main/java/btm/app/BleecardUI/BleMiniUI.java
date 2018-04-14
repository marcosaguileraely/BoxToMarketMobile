package btm.app.BleecardUI;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import btm.app.DataHolder.DataHolder;
import btm.app.DataHolder.DataHolderBleBuy;
import btm.app.DataHolder.DataHolderBleData;
import btm.app.MainActivity;
import btm.app.Model.Subscriptions;
import btm.app.R;

public class BleMiniUI extends AppCompatActivity {

    Context context = this;
    private final static String TAG = "DEV -> Ble UI";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private int[] RGBFrame = {0,0,0};

    AlertDialog.Builder builder;
    AlertDialog dialogBle;

    private TextView isSerial;
    private TextView mConnectionState;
    private TextView mDataField;
    private TextView text1, text2, text3, text4, text5;
    TextView ble_id;


    private TextView namepr1, namepr2, namepr3, namepr4, namepr5;
    private TextView pr1, pr2, pr3, pr4, pr5;
    private String mDeviceName;
    private String mDeviceAddress;
    String action = "list";
    String dataResponse;

    String rsa;
    String price1, price2, price3, price4, price5;
    String prod1, prod2, prod3, prod4, prod5;


    Button b1, b2, b3, b4, b5;

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;

    public final static UUID HM_RX_TX =
            UUID.fromString(SampleGattAttributes.HM_RX_TX);

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    // Code to manage Service lifecycle.
    // Here is where the Device is conected vía Mac Address
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, ":( Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            String mac_address_dataholder = DataHolderBleData.getMac();
            Log.d(TAG, ":) *************** Mac DatHolder: " + mac_address_dataholder);
            mBluetoothLeService.connect(mac_address_dataholder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, ":( *************** Service disconnected:");
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                //invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                //invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(mBluetoothLeService.EXTRA_DATA));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_mini_ui);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.ui_ble_mini_machines));

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);

        // is serial present?
        isSerial    = (TextView) findViewById(R.id.isSerial);
        mDataField  = (TextView) findViewById(R.id.data_value);
        text1       = (TextView) findViewById(R.id.textView1);
        text2       = (TextView) findViewById(R.id.textView2);
        text3       = (TextView) findViewById(R.id.textView3);
        text4       = (TextView) findViewById(R.id.textView4);
        text5       = (TextView) findViewById(R.id.textView5);
        ble_id      = (TextView) findViewById(R.id.ble_id);

        pr1         = (TextView) findViewById(R.id.price1);
        pr2         = (TextView) findViewById(R.id.price2);
        pr3         = (TextView) findViewById(R.id.price3);
        pr4         = (TextView) findViewById(R.id.price4);
        pr5         = (TextView) findViewById(R.id.price5);

        namepr1     = (TextView) findViewById(R.id.product_name1);
        namepr2     = (TextView) findViewById(R.id.product_name2);
        namepr3     = (TextView) findViewById(R.id.product_name3);
        namepr4     = (TextView) findViewById(R.id.product_name4);
        namepr5     = (TextView) findViewById(R.id.product_name5);

        b1 = (Button) findViewById(R.id.button1);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);
        b5 = (Button) findViewById(R.id.button5);

        ble_id.setText("# " + DataHolderBleData.getId().toUpperCase());

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        bleeDialog(); //Starts the Blee loader
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initBleDataSearch();
            }
        }, 1500); //Timer is in ms here.

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHolderBleBuy.setLiSelected("1");
                DataHolderBleBuy.setLiNameSeleted(prod1);
                //mBluetoothLeService.disconnect();

                Intent goToBuy =  new Intent(BleMiniUI.this, BleMiniUiBuyActivity.class);
                startActivity(goToBuy);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHolderBleBuy.setLiSelected("2");
                DataHolderBleBuy.setLiNameSeleted(prod2);
                mBluetoothLeService.disconnect();

                Intent goToBuy =  new Intent(BleMiniUI.this, BleMiniUiBuyActivity.class);
                startActivity(goToBuy);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHolderBleBuy.setLiSelected("3");
                DataHolderBleBuy.setLiNameSeleted(prod3);
                mBluetoothLeService.disconnect();

                Intent goToBuy =  new Intent(BleMiniUI.this, BleMiniUiBuyActivity.class);
                startActivity(goToBuy);
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHolderBleBuy.setLiSelected("4");
                DataHolderBleBuy.setLiNameSeleted(prod4);
                mBluetoothLeService.disconnect();

                Intent goToBuy =  new Intent(BleMiniUI.this, BleMiniUiBuyActivity.class);
                startActivity(goToBuy);
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHolderBleBuy.setLiSelected("5");
                DataHolderBleBuy.setLiNameSeleted(prod5);
                mBluetoothLeService.disconnect();

                Intent goToBuy =  new Intent(BleMiniUI.this, BleMiniUiBuyActivity.class);
                startActivity(goToBuy);
            }
        });
        Log.d(TAG, "*********** Acceso a la Actividad iniciado");
    }

    private void clearUI() {
        mDataField.setText(R.string.no_data);
        Log.d(TAG, "*********** NO DATA");

    }

    @Override
    protected void onResume() {
        super.onResume();
        //bleeDialog();
        /*Log.d(TAG, ":) *********** Acceso a la Actividad iniciado");
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        Log.d(TAG, ":) *********** mBluetoothLeService: " + mBluetoothLeService);
        if (mBluetoothLeService != null) {
            Log.d(TAG, ":) *********** mBluetoothLeService verificado");
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, ":) *********** mBluetoothLeService conectado");
            Log.d(TAG, "Connect request result=" + result);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initBleDataSearch();
                }
            }, 1500); //Timer is in ms here. it was 3000 ms
        }else{
            Log.d(TAG, ":( *********** mBluetoothLeService es null");
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void displayData(String data) {
        Log.d(TAG, "*********** Data incoming: " + data);
        if (data != null) {
            mDataField.setText(data);
            if(data.length() > 1) {
                settingValuesTolines(data);
            }else {
                Log.d(TAG, "It's returning the 9 number");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                //mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                //mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void initBleDataSearch(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listProduct();
                //Toast.makeText(context, "The ble status is: " + mConnected, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void settingValuesTolines(String data) {
        // This kind of Mini Btm has 5 lines, it goes since 1 to 5
        // the data is ordered in couples, in e.x.
        // For a response like: 1201232010 means 12 - 01 - 23 - 20 - 10
        // it means that for line 1 there are 12 products, line 2 there is 01 (1) product and so on...

        String subStringDataSure = beSureDataString(data);
        Log.d(TAG, "-> Sub Full String: " + "full string " + subStringDataSure);

        String subString1 = subStringDataSure.substring(0, 2);
        String subString2 = subStringDataSure.substring(2, 4);
        String subString3 = subStringDataSure.substring(4, 6);
        String subString4 = subStringDataSure.substring(6, 8);
        String subString5 = subStringDataSure.substring(8, 10);

        int value1 = Integer.valueOf(subString1);
        int value2 = Integer.valueOf(subString2);
        int value3 = Integer.valueOf(subString3);
        int value4 = Integer.valueOf(subString4);
        int value5 = Integer.valueOf(subString5);

        Log.d(TAG, "-> Stock now: " + "Line #1 " + value1);
        Log.d(TAG, "-> Stock now: " + "Line #2 " + value2);
        Log.d(TAG, "-> Stock now: " + "Line #3 " + value3);
        Log.d(TAG, "-> Stock now: " + "Line #4 " + value4);
        Log.d(TAG, "-> Stock now: " + "Line #5 " + value5);

        text1.setText("Disponible: " + String.valueOf(value1));
        text2.setText("Disponible: " + String.valueOf(value2));
        text3.setText("Disponible: " + String.valueOf(value3));
        text4.setText("Disponible: " + String.valueOf(value4));
        text5.setText("Disponible: " + String.valueOf(value5));

        try {
            //data = new btm.app.Network.NetActions(context).getBlePrice(DataHolderBleData.getId());
            data = new btm.app.Network.NetActions(context).getBlePriceAndNames(DataHolderBleData.getId());
            getMachinesPriceList(data);

            pr1.setText(price1);
            pr2.setText(price2);
            pr3.setText(price3);
            pr4.setText(price4);
            pr5.setText(price5);

            namepr1.setText(prod1);
            namepr2.setText(prod2);
            namepr3.setText(prod3);
            namepr4.setText(prod4);
            namepr5.setText(prod5);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(value1==0){
            b1.setEnabled(false);
            text1.setText("No Disponible");
            pr1.setText("$0");
        }if(value2==0){
            b2.setEnabled(false);
            text2.setText("No Disponible");
            pr2.setText("$0");
        }if(value3==0){
            b3.setEnabled(false);
            text3.setText("No Disponible");
            pr3.setText("$0");
        }if(value4==0){
            b4.setEnabled(false);
            text4.setText("No Disponible");
            pr4.setText("$0");
        }if(value5==0){
            b5.setEnabled(false);
            text5.setText("No Disponible");
            pr5.setText("$0");
        }
    }

    private String beSureDataString(String data) {
         if(data.contains("/")){
             String newString = data.replace("/", "0");
             return newString;
         } else if(data.contains(".")){
             String newString2 = data.replace("/", "0");
             return newString2;
         }
         else {
             return data;
         }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();


        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));

            // If the service exists for HM 10 Serial, say so.
            if(SampleGattAttributes.lookup(uuid, unknownServiceString) == "HM 10 Serial") {
                isSerial.setText("Yes, serial :-)");
            }
            else {
                isSerial.setText("No, serial :-(");
            }
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            // get characteristic when UUID matches RX/TX UUID
            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    // on change of bars write char
    private void listProduct() {
        String rsaValue = getStringJson(getRsaBle());
        Log.d(TAG, "Sending result = " + rsaValue);
        final byte[] tx = hexStringToByteArray(rsaValue);
        Log.d(TAG, "Sending byte[] = " + Arrays.toString(tx));

        Log.d(TAG, "//////////Is Connected? = " + mConnected);

        if(mConnected) {
            characteristicTX.setValue(tx);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
        }else {
            mBluetoothLeService.disconnect();
            mBluetoothLeService.close();
            Intent goToList = new Intent(BleMiniUI.this, BleListActivity.class);
            startActivity(goToList);
        }
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

    public String getRsaBle(){
        try {
            dataResponse = new btm.app.Network.NetActions(context).getBleRsa(DataHolderBleData.getId());
            Log.d(TAG, " oKHttp response: " + dataResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }  catch (JSONException e) {
            e.printStackTrace();
        }

        return dataResponse;
    }

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

    public void bleeDialog(){
        builder = new AlertDialog.Builder(BleMiniUI.this);

        LayoutInflater factory = LayoutInflater.from(BleMiniUI.this);
        View view              = factory.inflate(R.layout.ui_aux_ble_loader, null);
        ImageView gifImageView = (ImageView) view.findViewById(R.id.gif_loader);

        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);
        Glide.with(BleMiniUI.this)
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
            }
        }, 7000); //Timer is in ms here.
    }

    public void getMachinesPriceList(String response){
        String[] mainToken = response.split("\\|");
        Log.d(TAG, "---->" + response);

        String values1 = mainToken[0];
        String values2 = mainToken[1];
        String values3 = mainToken[2];
        String values4 = mainToken[3];
        String values5 = mainToken[4];

        Log.d(TAG, "---->" + values1);
        Log.d(TAG, "---->" + values2);
        Log.d(TAG, "---->" + values3);
        Log.d(TAG, "---->" + values4);
        Log.d(TAG, "---->" + values5);

        String subValues1[] = values1.split(",");
        String subValues2[] = values2.split(",");
        String subValues3[] = values3.split(",");
        String subValues4[] = values4.split(",");
        String subValues5[] = values5.split(",");

        price1 = subValues1[0];
        prod1  = subValues1[1];

        price2 = subValues2[0];
        prod2  = subValues2[1];

        price3 = subValues3[0];
        prod3  = subValues3[1];

        price4 = subValues4[0];
        prod4  = subValues4[1];

        price5 = subValues5[0];
        prod5  = subValues5[1];

        Log.d(TAG, "-> " + price1 + " " + price2 + " " + price3 + " " + price4 + " " + price5);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent goToSearch = new Intent(context, MainActivity.class);

        boolean isConnected = mBluetoothLeService.connect(DataHolderBleData.getMac());
        Log.d(TAG, "Is connected?: " + isConnected);
        if(isConnected){
            mBluetoothLeService.close();
            //mBluetoothLeService.disconnect();
            startActivity(goToSearch);
        }else {
            startActivity(goToSearch);
        }
    }
}