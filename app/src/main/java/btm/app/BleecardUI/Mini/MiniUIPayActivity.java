package btm.app.BleecardUI.Mini;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import btm.app.BleecardUI.BluetoothLeService;
import btm.app.BleecardUI.SampleGattAttributes;
import btm.app.DataHolder.AuxDataHolder;
import btm.app.DataHolder.DataHolderBleData;
import btm.app.DataHolder.DataHolderBleecardPay;
import btm.app.MainActivity;
import btm.app.R;
import btm.app.Utils.Utils;

import static btm.app.Utils.Utils.hexStringToByteArray;

public class MiniUIPayActivity extends AppCompatActivity {
    private final static String TAG = "DEV -> BLE CONT DEVICE";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private int[] RGBFrame = {0,0,0};
    private TextView isSerial;
    private TextView mConnectionState;
    private TextView mDataField;
    private SeekBar mRed,mGreen,mBlue;
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

    String macAddress, deviceName, rsaChain, machineId;
    String lineNumber;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            Log.d(TAG, " mBluetoothLeService " + mBluetoothLeService);

            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            Log.w(TAG, " Device Addr to connect: " + macAddress);
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
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a result of read
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
            }
        }
    };

    private void clearUI() {
        mDataField.setText(R.string.no_data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_uipay);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final Intent intent = getIntent();
        mDeviceName         = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress      = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        macAddress = DataHolderBleData.getMac();
        deviceName = DataHolderBleData.getName();
        machineId  = AuxDataHolder.getMachine_id();
        lineNumber = DataHolderBleecardPay.getLineSelected();
        rsaChain   = DataHolderBleecardPay.getRsaLineSelected();

        Log.d(TAG, " /////////// " + macAddress );

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(macAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        // is serial present?
        isSerial    = (TextView) findViewById(R.id.isSerial);
        mRed        = (SeekBar) findViewById(R.id.seekRed);
        mGreen      = (SeekBar) findViewById(R.id.seekGreen);
        mBlue       = (SeekBar) findViewById(R.id.seekBlue);

        mDataField  = (TextView) findViewById(R.id.data_value);

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
                passRSAChange(machineId);
            }
        }, 2000); //Timer 1500/2000 (between 1,5 secs and 2 secs) is a optimal time.
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, " onResume lifeCycle ");
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(macAddress);
            Log.d(TAG, "Connect request result = " + result);
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

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);

            if(data.equals("9")){


            }if(data.equals("1")){
                Log.w(TAG, "It's returning the 1 number");
                customDialogPaymentResult("La máquina ha procesado exitosamente tu pedido.");
                Toast.makeText(context, "It's returning the 1 number because the machine have sensed correctly the product.", Toast.LENGTH_LONG).show();

            }if(data.equals("0")){
                Log.w(TAG, "It's returning the 0 number");
                Toast.makeText(context, "It's returning the 0 number because the machine doesn't sense the product.", Toast.LENGTH_LONG).show();
                customDialogPaymentResult("Ups! La máquina NO ha procesado exitosamente tu pedido.");
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

    private void passRSAChange(String machineId) {
        String rsa = utils.getStringJson(utils.buyRsaBle(machineId));
        Log.d(TAG, "////// RSA Passed = " + rsa);

        final byte[] tx = hexStringToByteArray(rsa);
        Log.d(TAG, "Sending byte[] = " + Arrays.toString(tx));

        if(mConnected) {
            Log.w(TAG, " It's entering : ok ");
            characteristicTX.setValue(tx);
            Log.w(TAG, " characteristicTX : ok ");
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            Log.w(TAG, " writeCharacteristic : ok");
            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
            Log.w(TAG, " setCharacteristicNotification : ok");
            mBluetoothLeService.readCharacteristic(characteristicRX);
        }
    }

    private void passingLineNumber(String lineNumber) {
        Log.w(TAG, "Sending line number = " + lineNumber);
        if(mConnected) {
            characteristicTX.setValue(lineNumber);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
            mBluetoothLeService.readCharacteristic(characteristicRX);
        }
    }

    //////////////////////////
    /// Dialog windows ///////
    //////////////////////////

    public void customDialog(String inDatum){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(inDatum);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                //mBluetoothLeService.disconnect();
                mBluetoothLeService.close(); //I thing i could fix some stuff
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void customPasswordDialog(String inDatum){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(inDatum);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                //mBluetoothLeService.disconnect();
                //Intent intent = new Intent(context, MainActivity.class);
                //startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void customDialogPaymentResult(String inDatum){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(inDatum);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                mBluetoothLeService.close(); //After recieve (1=product delivered) or (0=product not delivered), We disconnect the gatt server for other users can use it
                //mBluetoothLeService.disconnect(); //After (1=product delivered) or (0=product not delivered), We disconnect the gatt server for other users can use it
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



}
