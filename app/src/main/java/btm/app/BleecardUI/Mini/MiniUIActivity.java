package btm.app.BleecardUI.Mini;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import btm.app.BleecardUI.BluetoothLeService;
import btm.app.BleecardUI.SampleGattAttributes;
import btm.app.DataHolder.DataHolderBleData;
import btm.app.R;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.widget.SeekBar;
import android.widget.Toast;

import btm.app.DataHolder.AuxDataHolder;
import btm.app.DataHolder.DataHolderBleecardPay;
import btm.app.Utils.Utils;

import static btm.app.Utils.Utils.hexStringToByteArray;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */

public class MiniUIActivity extends AppCompatActivity {

    private final static String TAG = "DEV -> BLE CONT DEVICE";

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private int[] RGBFrame = {0,0,0};
    private TextView isSerial;
    private TextView mConnectionState;
    private TextView mDataField;
    private SeekBar  mRed,mGreen,mBlue;
    private String   mDeviceName;
    private String   mDeviceAddress;

    //Private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;

    Context context = this;

    public final static UUID HM_RX_TX = UUID.fromString(SampleGattAttributes.HM_RX_TX);
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    String deviceName, macAddress, machineId, machineImg, machineType;
    String price1, price2, price3, price4, price5;
    String prod1, prod2, prod3, prod4, prod5;
    String Lines, GlobalData;

    TextView namepr1, namepr2, namepr3, namepr4, namepr5;
    TextView pr1, pr2, pr3, pr4, pr5, ble_id;
    TextView text1, text2, text3, text4, text5;

    Button b1, b2, b3, b4, b5;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(macAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
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
                supportInvalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                //invalidateOptionsMenu();
                supportInvalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(mBluetoothLeService.EXTRA_DATA));
                GlobalData = intent.getStringExtra(mBluetoothLeService.EXTRA_DATA);
            }
        }
    };

    private void clearUI() {
        mDataField.setText(R.string.no_data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_ui);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final Intent intent = getIntent();
        mDeviceName         = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress      = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        macAddress = DataHolderBleData.getMac();
        //macAddress = "50:8C:B1:69:B2:67";
        deviceName = DataHolderBleData.getName();
        machineId  = AuxDataHolder.getMachine_id();

        Log.w(TAG, "////// Machine Id: " + machineId + " Name: " + deviceName + " Mac Addr: " + macAddress);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(macAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        // is serial present?
        isSerial = (TextView) findViewById(R.id.isSerial);

        mRed        = (SeekBar) findViewById(R.id.seekRed);
        mGreen      = (SeekBar) findViewById(R.id.seekGreen);
        mBlue       = (SeekBar) findViewById(R.id.seekBlue);

        mDataField  = (TextView) findViewById(R.id.data_value);
        text1       = (TextView) findViewById(R.id.textView1);
        text2       = (TextView) findViewById(R.id.textView2);
        text3       = (TextView) findViewById(R.id.textView3);
        text4       = (TextView) findViewById(R.id.textView4);
        text5       = (TextView) findViewById(R.id.textView5);
        //ble_id    = (TextView) findViewById(R.id.ble_id);

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

        b1          = (Button) findViewById(R.id.button1);
        b2          = (Button) findViewById(R.id.button2);
        b3          = (Button) findViewById(R.id.button3);
        b4          = (Button) findViewById(R.id.button4);
        b5          = (Button) findViewById(R.id.button5);

        Log.d(TAG, "Main Thread Id: " + Thread.currentThread().getId());

        readSeek(mRed,0);
        readSeek(mGreen,1);
        readSeek(mBlue,2);

        getSupportActionBar().setTitle(deviceName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                makeChange(machineId);
            }
        }, 2000); //Timer 1500/2000 (1,5 secs / 2 secs) is a optimal time.

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.disconnect();
                Log.w(TAG, "Gatt disconnected");
                DataHolderBleecardPay.setLineSelected("1");
                DataHolderBleecardPay.setLineNameSeleted(prod1);
                DataHolderBleecardPay.setRsaLineSelected(utils.getStringJson(utils.getRsaBle(machineId)));
                Log.w(TAG, "//// Machine Id: " + machineId);

                Intent goToBuy =  new Intent(context, MiniUIPayActivity.class);
                startActivity(goToBuy);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.disconnect();
                DataHolderBleecardPay.setLineSelected("2");
                DataHolderBleecardPay.setLineNameSeleted(prod2);
                DataHolderBleecardPay.setRsaLineSelected(utils.getStringJson(utils.getRsaBle(machineId)));

                Intent goToBuy =  new Intent(context, MiniUIPayActivity.class);
                startActivity(goToBuy);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.disconnect();
                DataHolderBleecardPay.setLineSelected("3");
                DataHolderBleecardPay.setLineNameSeleted(prod3);
                DataHolderBleecardPay.setRsaLineSelected(utils.getStringJson(utils.getRsaBle(machineId)));

                Intent goToBuy =  new Intent(context, MiniUIPayActivity.class);
                startActivity(goToBuy);
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.disconnect();
                DataHolderBleecardPay.setLineSelected("4");
                DataHolderBleecardPay.setLineNameSeleted(prod4);
                DataHolderBleecardPay.setRsaLineSelected(utils.getStringJson(utils.getRsaBle(machineId)));

                Intent goToBuy =  new Intent(context, MiniUIPayActivity.class);
                startActivity(goToBuy);
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.disconnect();
                DataHolderBleecardPay.setLineSelected("5");
                DataHolderBleecardPay.setLineNameSeleted(prod5);
                DataHolderBleecardPay.setRsaLineSelected(utils.getStringJson(utils.getRsaBle(machineId)));

                Intent goToBuy =  new Intent(context, MiniUIPayActivity.class);
                startActivity(goToBuy);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(macAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
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
                mBluetoothLeService.connect(macAddress);
                // Calling makeChange() method after 1.5 secs
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        makeChange(machineId);
                    }
                }, 1500);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                // Setting in blank all fields first
                blankAll();
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

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
            if(data.length() > 1) {                                     // If 'data' variable changes and detects a 'data'.length up to 1 (eg. 1234567890 -> length = 5) it means that 'data' has a stock
                settingValuesToLines(data);                             // It triggers the settingValuesToLines() method and set the stock in the UI
                getMachinesPriceList(utils.getLinesData(machineId));    // It triggers the getMachinesPriceList() method which get product and price via WS
                Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
            }else if(data.equals("9")) {
                Log.d(TAG, "It's returning the 9 number");
                Toast.makeText(context, "It's returning the 9 number", Toast.LENGTH_SHORT).show();

            }else if(data.equals("8")) {
                Log.d(TAG, "It's returning the 8 number");
                Toast.makeText(context, "It's returning the 8 number", Toast.LENGTH_SHORT).show();
            }
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
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));

            // If the service exists for HM 10 Serial, say so.
            if(SampleGattAttributes.lookup(uuid, unknownServiceString) == "HM 10 Serial") { isSerial.setText("Yes, serial :-)"); } else {  isSerial.setText("No, serial :-("); }
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

    private void readSeek(SeekBar seekBar,final int pos) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                RGBFrame[pos]=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                //Log.d(TAG, "////// Start getting Stock from Machine ");
                //makeChange("5003");
            }
        });
    }

    Utils utils = new Utils();

    /*
    * MachineId is the unique number for each Blee Machine
    * You can get it using the WS endpoint: https://www.bleecard.com/api/getMachines.do
    * For more information you can check the bleecard documentation
    * */
    private void makeChange(String MachineId) {
        // This value is the Machine ID eg. 5003, change and automatically and pass via to the method
        String Data  = utils.getStringJson(utils.getRsaBle(MachineId));

        Log.d(TAG, "////// WS Response= " + Data);
        //Log.d(TAG, "Sending result=" + str);

        final byte[] tx = hexStringToByteArray(Data);
        Log.d(TAG, "Sending byte[] = " + Arrays.toString(tx));

        //final byte[] tx = str.getBytes();
        if(mConnected) {
            characteristicTX.setValue(tx);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
        }
    }

    private void settingValuesToLines(String data) {
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

    public void getMachinesPriceList(String inDatum){
        String[] mainToken = inDatum.split("\\|");
        Log.d(TAG, "---->" + inDatum);

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

        Log.d(TAG, "-> Line #1 - Name: " + prod1 + " Price: " + price1);
        Log.d(TAG, "-> Line #2 - Name: " + prod2 + " Price: " + price2);
        Log.d(TAG, "-> Line #3 - Name: " + prod3 + " Price: " + price3);
        Log.d(TAG, "-> Line #4 - Name: " + prod4 + " Price: " + price4);
        Log.d(TAG, "-> Line #5 - Name: " + prod5 + " Price: " + price5);

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

    private void blankAll(){
        text1.setText("Disponible: ");
        text2.setText("Disponible: ");
        text3.setText("Disponible: ");
        text4.setText("Disponible: ");
        text5.setText("Disponible: ");

        pr1.setText("");
        pr2.setText("");
        pr3.setText("");
        pr4.setText("");
        pr5.setText("");

        namepr1.setText("");
        namepr2.setText("");
        namepr3.setText("");
        namepr4.setText("");
        namepr5.setText("");
    }
}
