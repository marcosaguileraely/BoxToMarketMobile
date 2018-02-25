package btm.app.BleecardUI;


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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import btm.app.R;

public class BleMiniUI extends AppCompatActivity {

    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    Context context = this;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private int[] RGBFrame = {0,0,0};

    private TextView isSerial;
    private TextView mConnectionState;
    private TextView mDataField;
    private TextView text1, text2, text3, text4, text5;
    private TextView pr1, pr2, pr3, pr4, pr5;
    private SeekBar mRed,mGreen,mBlue;
    private String mDeviceName;
    private String mDeviceAddress;
    String action = "list";

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
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
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


        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
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

        pr1         = (TextView) findViewById(R.id.price1);
        pr2         = (TextView) findViewById(R.id.price2);
        pr3         = (TextView) findViewById(R.id.price3);
        pr4         = (TextView) findViewById(R.id.price4);
        pr5         = (TextView) findViewById(R.id.price5);

        b1 = (Button) findViewById(R.id.button1);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);
        b5 = (Button) findViewById(R.id.button5);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initBleDataSearch();
            }
        }, 3000); //Timer is in ms here.

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyProductByLine("1");
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyProductByLine("2");
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyProductByLine("3");
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyProductByLine("4");
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyProductByLine("5");
            }
        });
    }

    private void clearUI() {
        mDataField.setText(R.string.no_data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
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
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
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
                Toast.makeText(context, "The ble status is: " + mConnected, Toast.LENGTH_LONG).show();
                listProduct();
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
            if(data.length() > 1) {
                settingValuesTolines(data);
            }else {
                Log.d(TAG, "It's returning the 9 number");
            }
        }
    }

    private void settingValuesTolines(String data) {
        // This kind of Mini Btm has 5 lines, it goes since 1 to 5
        // the data is ordered in couples, in e.x.
        // For a response like: 1201232010 means 12 - 01 - 23 - 20 - 10
        // it means that for line 1 there are 12 products, line 2 there is 01 (1) product and so on...
        //
        //
        String subString1 = data.substring(0, 2);
        String subString2 = data.substring(2, 4);
        String subString3 = data.substring(4, 6);
        String subString4 = data.substring(6, 8);
        String subString5 = data.substring(8, 10);

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
            data = new btm.app.Network.NetActions(context).getBlePrice("5001");
            Log.d(TAG, " oKHttp response: " + data);

            pr1.setText(data);
            pr2.setText(data);
            pr3.setText(data);
            pr4.setText(data);
            pr5.setText(data);

        } catch (IOException e) {
            e.printStackTrace();
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
            if(SampleGattAttributes.lookup(uuid, unknownServiceString) == "HM 10 Serial") {
                isSerial.setText("Yes, serial :-)");
            }
            else {  isSerial.setText("No, serial :-(");

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
        String str = "0b64a3cd3e6099b8ba9c59183906ee37";
        Log.d(TAG, "Sending result = " + str);
        final byte[] tx = hexStringToByteArray(str);
        Log.d(TAG, "Sending byte[] = " + Arrays.toString(tx));

        if(mConnected) {
            characteristicTX.setValue(tx);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
        }
    }

    private void buyProductByLine(final String lineNumber) {
        String str = "0a6455df41b578fdcf0115d61b0043c9";
        Log.d(TAG, "Sending result = " + str);
        final byte[] tx = hexStringToByteArray(str);
        Log.d(TAG, "Sending byte[] = " + Arrays.toString(tx));

        if(mConnected) {
            characteristicTX.setValue(tx);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                passingLineNumber(lineNumber);
            }
        }, 2000); //Timer is in ms here.
    }

    private void passingLineNumber(String lineNumber) {
        //String str = "5";
        Log.d(TAG, "Sending result = " + lineNumber);
        if(mConnected) {
            characteristicTX.setValue(lineNumber);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listProduct();
            }
        }, 3000); //Timer is in ms here.
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
}