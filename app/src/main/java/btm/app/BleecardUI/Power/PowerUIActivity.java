package btm.app.BleecardUI.Power;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import btm.app.BleecardUI.BleListActivity;
import btm.app.BleecardUI.BluetoothLeService;
import btm.app.BleecardUI.SampleGattAttributes;
import btm.app.CC;
import btm.app.DataHolder.DataHolder;
import btm.app.DataHolder.DataHolderBleData;
import btm.app.MainActivity;
import btm.app.Network.NetActions;
import btm.app.R;
import btm.app.Utils.Utils;

import static btm.app.Utils.Utils.hexStringToByteArray;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */

public class PowerUIActivity extends AppCompatActivity {

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

    private ArrayList<CC> listTc;

    AlertDialog dialog_pass_ui;
    ProgressDialog dialog2;

    public final static UUID HM_RX_TX = UUID.fromString(SampleGattAttributes.HM_RX_TX);
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    String deviceName, macAddress, machineId, machineImg, machineType;
    String price1, price2, price3, price4, price5;
    String prod1, prod2, prod3, prod4, prod5;
    String num_hours_1, num_hours_2, num_hours_3, num_hours_4, num_hours_5;
    String Line, GlobalDataLine = "", rsaBuyLine;

    String ProductPrice;

    String modo, data, password_dialog_value;
    String webResponse, dataResponse;
    String creditCardData, walletData, subscriptionData;
    String action;

    String card_info, card_id;
    String globalProduct, globalPrice;

    TextView MachineIdTitle;
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
            }
        }
    };

    private void clearUI() {
        mDataField.setText(R.string.no_data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_ui);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        dialog2 = new ProgressDialog(PowerUIActivity.this);

        final Intent intent = getIntent();
        mDeviceName         = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress      = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        macAddress          = DataHolderBleData.getMac();
        deviceName          = DataHolderBleData.getName();
        machineId           = DataHolderBleData.getId();

        GlobalDataLine      = "";
        action              = "StablishConx";

        Log.w(TAG, "////// Machine Id: " + machineId + " Name: " + deviceName + " Mac Addr: " + macAddress);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(macAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        // is serial present?
        isSerial = (TextView) findViewById(R.id.isSerial);

        mDataField     = (TextView) findViewById(R.id.data_value);
        MachineIdTitle = (TextView) findViewById(R.id.machine_id_text);

        text1          = (TextView) findViewById(R.id.textView1_txt1_power);
        text2          = (TextView) findViewById(R.id.textView1_txt2_power);
        text3          = (TextView) findViewById(R.id.textView1_txt3_power);
        text4          = (TextView) findViewById(R.id.textView1_txt4_power);
        text5          = (TextView) findViewById(R.id.textView1_txt5_power);

        pr1            = (TextView) findViewById(R.id.price1_power_1);
        pr2            = (TextView) findViewById(R.id.price1_power_2);
        pr3            = (TextView) findViewById(R.id.price1_power_3);
        pr4            = (TextView) findViewById(R.id.price1_power_4);
        pr5            = (TextView) findViewById(R.id.price1_power_5);

        b1             = (Button) findViewById(R.id.b1_power);
        b2             = (Button) findViewById(R.id.b2_power);
        b3             = (Button) findViewById(R.id.b3_power);
        b4             = (Button) findViewById(R.id.b4_power);
        b5             = (Button) findViewById(R.id.b5_power);

        Log.d(TAG, "Main Thread Id: " + Thread.currentThread().getId());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.ui_ble_mini_select));
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        MachineIdTitle.setText("#" + machineId);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                makeChange(machineId);
            }
        }, 2000); //Timer 1500/2000 (1,5 secs / 2 secs) is a optimal time.

        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setMessage(getString(R.string.inf_dialog));
        dialog2.show();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = "ReadyToPay";
                globalProduct = prod1;
                globalPrice   = price1.replace("$","");
                rsaBuyLine = utils.getStringJson(utils.getRsaBle(machineId));
                Log.w(TAG, "//// Machine Id: " + machineId + " RSA Number: " + rsaBuyLine);
                payUIDialog();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = "ReadyToPay";
                globalProduct = prod2;
                globalPrice   = price2.replace("$","");
                rsaBuyLine = utils.getStringJson(utils.getRsaBle(machineId));
                Log.w(TAG, "//// Machine Id: " + machineId + " RSA Number: " + rsaBuyLine);
                payUIDialog();
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = "ReadyToPay";
                globalProduct = prod3;
                globalPrice   = price3.replace("$","");
                rsaBuyLine = utils.getStringJson(utils.getRsaBle(machineId));
                Log.w(TAG, "//// Machine Id: " + machineId + " RSA Number: " + rsaBuyLine);
                payUIDialog();
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = "ReadyToPay";
                globalProduct = prod4;
                globalPrice   = price4.replace("$","");
                rsaBuyLine = utils.getStringJson(utils.getRsaBle(machineId));
                Log.w(TAG, "//// Machine Id: " + machineId + " RSA Number: " + rsaBuyLine);
                payUIDialog();
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = "ReadyToPay";
                globalProduct = prod5;
                globalPrice   = price5.replace("$","");
                rsaBuyLine = utils.getStringJson(utils.getRsaBle(machineId));
                Log.w(TAG, "//// Machine Id: " + machineId + " RSA Number: " + rsaBuyLine);
                payUIDialog();
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

    //////////////////////////////////////
    //////// Gatt Server Responses ///////
    //////////////////////////////////////

    Utils utils = new Utils();

    private void displayData(String data) {
        if (data != null) {

            mDataField.setText(data);
            Log.e(TAG, " //// Action : " + action);
            Log.e(TAG, " //// Data received : " + data);

            String is8 = is8Validate(data);
            Log.w(TAG, " //// ¿Getting 8 value? " + is8);

            switch (action) {
                case "StablishConx":
                    Log.e(TAG, " Entering to : " + "StablishConx");

                    if(data.equals("9")){
                        Log.d(TAG, "It's returning the 9 number");
                        dialog2.dismiss();

                    }else if(data.equals("1")){
                        Log.d(TAG, "It's returning the 1 number: Machine available");
                        getMachinesPriceList(utils.getPowerLinesData(machineId));

                    }else if(data.endsWith("2")){
                        Log.d(TAG, "It's returning the 2 number: Machine saturated");
                        dialog2.dismiss();
                        machineSaturatedbleDialog();

                    }else if(data.endsWith("3")){
                        Log.d(TAG, "It's returning the 3 number: Machine bussy");
                        dialog2.dismiss();
                        machineBussybleDialog(data);

                    }

                    break;

                case "ReadyToPay":
                    Log.e(TAG, "///// Entering to: " + "ReadyToPay");

                    if(data.equals("9")){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                passingHours("1");
                            }
                        }, 2000); //Timer is in ms here.
                    }


                    break;

                case "Working":
                    if(data.equals("9")){
                        customDialog("TRANSACCIÓN EXITOSA.\n\nEl BtM Power se encuentra activo.");
                    }

                    break;

                case "Getting8":
                    dialog2.dismiss();
                    customDialogGetting8("Falla en la comunicación con la máquina. Vuelve a intentarlo nuevamente.");
                    //customDialogGetting8("TRANSACCIÓN EXITOSA.\n\nEl BtM Power se encuentra activo.");

                    break;

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

    /*
    * MachineId is the unique number for each Blee Machine
    * You can get it using the WS endpoint: https://www.bleecard.com/api/getMachines.do
    * For more information you can check the bleecard documentation
    * */

    ////////////////////////////////////
    //////// Gatt Server Actions ///////
    ////////////////////////////////////

    private void makeChange(String MachineId) {
        action = "StablishConx";
        // This value is the Machine ID eg. 5003, change and automatically and pass via to the method
        String Data  = utils.getStringJson(utils.getRsaBle(MachineId));

        Log.d(TAG, "////// WS Response = " + Data);
        //Log.d(TAG, "Sending result=" + str);

        final byte[] tx = hexStringToByteArray(Data);
        Log.d(TAG, "Sending byte[] = " + Arrays.toString(tx));

        if(mConnected) {
            characteristicTX.setValue(tx);
            Log.w(TAG, " Setting characteristicTX value ");
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            Log.w(TAG, " writeCharacteristic ");
            mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
            Log.w(TAG, " setCharacteristicNotification ");
        }
    }

    private void passRsaToBuyChange() {
        action = "ReadyToPay";
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
            action = "ReadyToPay";
        }
    }

    private void passingHours(String lineNumber) {
        action = "Working";
        Log.w(TAG, "Sending line number = " + lineNumber);
        if(mConnected) {
            characteristicTX.setValue(lineNumber);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
        }
    }

    ////////////////////////////////////
    //////// Other Actions /////////////
    ////////////////////////////////////

    public void getMachinesPriceList(String inDatum){
        String[] mainToken = inDatum.split("\\|");

        String values1 = mainToken[0];
        String values2 = mainToken[1];
        String values3 = mainToken[2];
        String values4 = mainToken[3];
        String values5 = mainToken[4];

        Log.w(TAG, "Value 1: " + values1);
        Log.w(TAG, "Value 2: " + values2);
        Log.w(TAG, "Value 3: " + values3);
        Log.w(TAG, "Value 3: " + values4);
        Log.w(TAG, "Value 3: " + values5);

        String subValues1[] = values1.split(",");
        String subValues2[] = values2.split(",");
        String subValues3[] = values3.split(",");
        String subValues4[] = values4.split(",");
        String subValues5[] = values5.split(",");

        prod1       = subValues1[0];
        price1      = subValues1[1];
        num_hours_1 = subValues1[2];

        prod2       = subValues2[0];
        price2      = subValues2[1];
        num_hours_2 = subValues2[2];

        prod3       = subValues3[0];
        price3      = subValues3[1];
        num_hours_3 = subValues3[2];

        prod4       = subValues4[0];
        price4      = subValues4[1];
        num_hours_4 = subValues4[2];

        prod5       = subValues5[0];
        price5      = subValues5[1];
        num_hours_5 = subValues5[2];

        Log.d(TAG, "-> Line #1 - Name: " + prod1 + " Price: " + price1 + " Hours: " + num_hours_1);
        Log.d(TAG, "-> Line #2 - Name: " + prod2 + " Price: " + price2 + " Hours: " + num_hours_2);
        Log.d(TAG, "-> Line #3 - Name: " + prod3 + " Price: " + price3 + " Hours: " + num_hours_3);
        Log.d(TAG, "-> Line #4 - Name: " + prod4 + " Price: " + price4 + " Hours: " + num_hours_4);
        Log.d(TAG, "-> Line #5 - Name: " + prod5 + " Price: " + price5 + " Hours: " + num_hours_5);

        pr1.setText(price1);
        pr2.setText(price2);
        pr3.setText(price3);
        pr4.setText(price4);
        pr5.setText(price5);

        text1.setText(prod1);
        text2.setText(prod2);
        text3.setText(prod3);
        text4.setText(prod4);
        text5.setText(prod5);

        dialog2.dismiss();
    }

    private String is8Validate(String inDatum){
        if(inDatum.equals("8")){
            action = "Getting8";
            return "Yes, it's 8 :(";
        }else{
            // Nothing to-do
            return "Nope, isn't 8 :)";
        }
    }

    //////////////////////////
    /// Dialog windows ///////
    //////////////////////////

    public void payUIDialog(){
        AlertDialog.Builder builder_payment_dialog = new AlertDialog.Builder(context);
        final LayoutInflater inflater = PowerUIActivity.this.getLayoutInflater();

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.ui_aux_payment, (ViewGroup) findViewById(android.R.id.content), false);

        final Spinner metodo  = (Spinner) viewInflated.findViewById(R.id.payment_method);
        final Spinner tC      = (Spinner) viewInflated.findViewById(R.id.spinnerTipoTC);
        final Button  buy     = (Button) viewInflated.findViewById(R.id.buy);
        final TextView product= (TextView) viewInflated.findViewById(R.id.product_name);
        final TextView price  = (TextView) viewInflated.findViewById(R.id.product_price);

        product.setText(globalProduct);
        price.setText(globalPrice);

        metodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final ProgressDialog progress = new ProgressDialog(context);
                progress.setMessage(getString(R.string.inf_dialog));

                modo = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "--> "+ modo);

                if(modo.equals("Tarjeta de Crédito") || modo.equals("Credit Card")){
                    tC.setVisibility(View.VISIBLE);
                    try {
                        data = new NetActions(context).getCardList(DataHolder.getUsername());
                        Log.d(TAG, " oKHttp response: " + data);

                        if(data.contains("****")){
                            String[] inf = data.replace("|", ";").split(";");
                            for (String cc : inf) {
                                listTc.add(new CC(cc));
                            }

                            ArrayAdapter<CC> adapter = new ArrayAdapter<CC>(getApplicationContext(), R.layout.item_card, listTc);
                            tC.setAdapter(adapter);
                        }else{
                            Toast.makeText(getApplicationContext(), getString(R.string.ui_buy_token_no_credit_cards), Toast.LENGTH_LONG).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    listTc = new ArrayList<CC>();
                    tC.setVisibility(View.GONE);
                    tC.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(modo.equals("Tarjeta de Crédito") || modo.equals("Credit Card")){
                    creditCardData = DataHolder.getUsername()
                            + "|" + DataHolder.getPass()
                            + "|" + DataHolderBleData.getId()
                            + "|" + card_id
                            + "|" + paymentMethod(modo)
                            + "|" + globalPrice
                            + "|" ;

                    Log.d(TAG, " --> Datos: " + creditCardData);
                    passwordDialog("cc_payment");

                }if(modo.equals("My Wallet") || modo.equals("Mi Billetera")){
                    walletData = DataHolder.getUsername()
                            + "|" + DataHolder.getPass()
                            + "|" + DataHolderBleData.getId()
                            + "|"
                            + "|" + paymentMethod(modo)
                            + "|" + globalPrice
                            + "|" ;

                    Log.w(TAG, "--> Datos: " + walletData);
                    passwordDialog("wallet_payment");

                }if(modo.equals("Subscription") || modo.equals("Suscripción")){
                    String activeSubscription = getIdSubscription();

                    if(!activeSubscription.equals("NO")){
                        subscriptionData = DataHolder.getUsername()
                                + "|" + DataHolder.getPass()
                                + "|" + DataHolderBleData.getId()
                                + "|" + getIdSubscription()
                                + "|"
                                + "|" + paymentMethod(modo)
                                + "|" + Line
                                + "|" ;

                        Log.d(TAG, "--> Datos: " + walletData);
                        passwordDialog("subs_payment");
                    }else {
                        customDialogNoMove("Actualmente no tienes una Suscripción Abierta para este producto. Por favor compra una Suscripción.");
                    }
                }
            }
        });

        tC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                card_info = parent.getItemAtPosition(position).toString();
                card_id   = listTc.get(position).getId();
                Log.d(TAG, "card number: "+card_info + "-->"+ card_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder_payment_dialog.setCancelable(false);
        builder_payment_dialog.setView(viewInflated)
                .setNegativeButton(R.string.ui_general_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        builder_payment_dialog.create();
        builder_payment_dialog.show();
    }

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
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void customDialogGetting8(String inDatum){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(inDatum);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                mBluetoothLeService.close(); //After recieve (1=product delivered) or (0=product not delivered), We disconnect the gatt server for other users can use it
                Intent intent = new Intent(context, BleListActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void passwordDialog(final String inDatum){

        AlertDialog.Builder builder_pass_dialog = new AlertDialog.Builder(context);
        final LayoutInflater inflater = PowerUIActivity.this.getLayoutInflater();

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.ui_aux_pass, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.password);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder_pass_dialog.setCancelable(false);
        builder_pass_dialog.setView(viewInflated)
                .setPositiveButton(R.string.ui_general_dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        password_dialog_value = input.getText().toString();
                        if(password_dialog_value.equals(DataHolder.getPass())){
                            if(inDatum.equals("cc_payment")){
                                payWithCreditCard();
                            }if(inDatum.equals("wallet_payment")){
                                payWithWallet();
                            }if(inDatum.equals("subs_payment")){
                                payWithSubscriptions();
                            }
                        }else {
                            dialog_pass_ui.dismiss();
                            customPasswordDialog("Clave invalida. Intenta nuevamente.");
                        }
                    }
                })
                .setNegativeButton(R.string.ui_general_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog_pass_ui.dismiss();
                    }
                });

        dialog_pass_ui = builder_pass_dialog.create();
        dialog_pass_ui.show();
    }

    public void machineAvailableDialog(){

        AlertDialog.Builder builder_pass_dialog = new AlertDialog.Builder(context);
        final LayoutInflater inflater = PowerUIActivity.this.getLayoutInflater();

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.ui_aux_power_available, (ViewGroup) findViewById(android.R.id.content), false);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder_pass_dialog.setCancelable(false);
        builder_pass_dialog.setView(viewInflated)
                .setPositiveButton(R.string.ui_general_dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog_pass_ui.dismiss();
                    }
                });

        dialog_pass_ui = builder_pass_dialog.create();
        dialog_pass_ui.show();
    }

    public void machineSaturatedbleDialog(){

        AlertDialog.Builder builder_pass_dialog = new AlertDialog.Builder(context);
        final LayoutInflater inflater = PowerUIActivity.this.getLayoutInflater();

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.ui_aux_power_saturated, (ViewGroup) findViewById(android.R.id.content), false);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder_pass_dialog.setCancelable(false);
        builder_pass_dialog.setView(viewInflated)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog_pass_ui.dismiss();
                        mBluetoothLeService.close(); //After recieve 2=Saturated. We disconnect the gatt server for other users can use it
                        Intent goToMain = new Intent(context, MainActivity.class);
                        startActivity(goToMain);
                    }
                });

        dialog_pass_ui = builder_pass_dialog.create();
        dialog_pass_ui.show();
    }

    public void machineBussybleDialog(String inDatumTime){

        AlertDialog.Builder builder_pass_dialog = new AlertDialog.Builder(context);
        final LayoutInflater inflater = PowerUIActivity.this.getLayoutInflater();

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.ui_aux_power_bussy, (ViewGroup) findViewById(android.R.id.content), false);
        final TextView time = (TextView) viewInflated.findViewById(R.id.ui_bussy_time);

        time.setText(inDatumTime.substring(0, inDatumTime.length() - 1));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder_pass_dialog.setCancelable(false);
        builder_pass_dialog.setView(viewInflated)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog_pass_ui.dismiss();
                        mBluetoothLeService.close(); //After recieve 3=Busy. We disconnect the gatt server for other users can use it
                        Intent goToMain = new Intent(context, MainActivity.class);
                        startActivity(goToMain);
                    }
                });

        dialog_pass_ui = builder_pass_dialog.create();
        dialog_pass_ui.show();
    }

    public void customDialogNoMove(String inDatum){
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

    /*******************
     * PAYMENT METHODS *
     *******************/

    public String paymentMethod(String method){
        if(method.equals("Tarjeta de Crédito") || method.equals("Credit Card")){
            return "tarjeta";
        }
        else if(method.equals("My Wallet") || method.equals("Mi Billetera")){
            return "creditos";
        }
        else{
            return "suscripcion";
        }
    }

    public String getIdSubscription(){
        String datos = DataHolder.getUsername() + "|" + DataHolderBleData.getId() + "|";
        try {
            dataResponse = new NetActions(context).btmMiniActiveSubscription(datos);
            Log.d(TAG, " oKHttp response: " + data);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataResponse;
    }

    public void payWithCreditCard(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    webResponse = new NetActions(context).btmMiniPayment(creditCardData);
                    Log.d(TAG, " oKHttp response: " + webResponse);

                    if(webResponse.equals("Consumo exitoso")){
                        dialog_pass_ui.dismiss();
                        dialog2.setCanceledOnTouchOutside(false);
                        dialog2.setMessage(getString(R.string.inf_dialog));
                        dialog2.show();

                        passRsaToBuyChange();

                    }else{
                        dialog_pass_ui.dismiss();
                        customDialog(webResponse);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void payWithWallet(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    webResponse = new NetActions(context).btmPowerPayment(walletData);
                    Log.w(TAG, " oKHttp response: " + webResponse);

                    if(webResponse.equals("Pago exitoso")){
                        dialog_pass_ui.dismiss();
                        dialog2.setCanceledOnTouchOutside(false);
                        dialog2.setMessage(getString(R.string.inf_dialog));
                        dialog2.show();

                        passRsaToBuyChange();

                    }else{
                        dialog_pass_ui.dismiss();
                        customDialog(webResponse);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    public void payWithSubscriptions(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, " ///// Device conection status: " + mConnected);

                    try {
                        webResponse = new NetActions(context).btmMiniPayment(subscriptionData);
                        Log.d(TAG, " oKHttp response: " + webResponse);

                        if(webResponse.equals("Consumo exitoso")){
                            dialog_pass_ui.dismiss();
                            dialog2.setCanceledOnTouchOutside(false);
                            dialog2.setMessage(getString(R.string.inf_dialog));
                            dialog2.show();

                            //buyProductByLine(DataHolderBleBuy.getLiSelected()); //this execute the Ble trigger
                            passRsaToBuyChange();

                        }else{
                            dialog_pass_ui.dismiss();
                            customDialog(webResponse);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }
        });
    }
}
