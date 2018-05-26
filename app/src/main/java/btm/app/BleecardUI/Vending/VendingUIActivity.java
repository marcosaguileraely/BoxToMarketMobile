package btm.app.BleecardUI.Vending;

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

public class VendingUIActivity extends AppCompatActivity {

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
    String Line, GlobalDataLine = "";

    String modo, data, password_dialog_value;
    String webResponse, dataResponse;
    String creditCardData, walletData, subscriptionData;
    String action;

    String card_info, card_id;
    String globalProduct, globalPrice;
    String allowEdit;
    String value;

    TextView MachineIdTitle;

    EditText valueCharge;
    Button charge;

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
        setContentView(R.layout.activity_vending_ui);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        dialog2 = new ProgressDialog(VendingUIActivity.this);

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
        charge         = (Button) findViewById(R.id.button_charge);
        valueCharge    = (EditText) findViewById(R.id.value_charge);

        Log.d(TAG, "Main Thread Id: " + Thread.currentThread().getId());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.ui_ble_mini_select));
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        MachineIdTitle.setText("Vending Machine \n# " + machineId);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                makeChange(machineId);
            }
        }, 2000); //Timer 1500/2000 (1,5 secs / 2 secs) is a optimal time.

        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setMessage(getString(R.string.inf_dialog));
        dialog2.show();

        charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = "Charging";
                String valueToCharge = valueCharge.getText().toString();
                payUIDialog();
                //makeCharging(value);
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

    private void displayData(String data) {
        if (data != null) {

            mDataField.setText(data);
            Log.w(TAG, " //// ACTION VALUE : " + action);
            Log.w(TAG, " //// DATA VALUE : " + data);

            String is8 = is8Validate(data);
            Log.w(TAG, " //// ¿GETTING 8 VALUE?: " + is8);

            String isNoSession = isNoSession(data);
            Log.w(TAG, " //// ¿GETTING NO SESION VALUE?: " + isNoSession);

            switch (action) {
                case "StablishConx":
                    if(data.equals("9")){
                        Log.w(TAG, "It's returning the 9 number");
                        getPriceActivator(utils.getVendingPriceData(DataHolderBleData.getId()));
                    }
                    break;

                case "Charging":
                    Log.w(TAG, "It's returning " + data);

                    break;

                case "Getting8":
                    customDialogGetting8("Falla en la comunicación con la máquina. Vuelve a intentarlo nuevamente.");

                    break;

                case "NoSession":
                    customDialogGetting8("Falla en la sesión con la máquina. Vuelve a intentarlo nuevamente.");

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

    Utils utils = new Utils();

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

    private void makeCharging(String valueToCharge) {
        action = "Charging";
        // This value is the Machine ID eg. 5003, change and automatically and pass via to the method

        Log.w(TAG, "////// Charging value = " + valueToCharge);

        //final byte[] tx = hexStringToByteArray(Hex);
        //Log.d(TAG, "Sending byte[] = " + Arrays.toString(tx));

        if(mConnected) {
            characteristicTX.setValue(valueToCharge);
            Log.w(TAG, " Setting characteristicTX value ");
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            Log.w(TAG, " writeCharacteristic ");
            mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
            Log.w(TAG, " setCharacteristicNotification ");
        }
    }

    ////////////////////////////////////
    //////// Other Actions /////////////
    ////////////////////////////////////

    private String is8Validate(String inDatum){
        if(inDatum.equals("8")){
            action = "Getting8";
            return "Yes, it's 8 :(";
        }else{
            // Nothing to-do
            return "Nope, isn't 8 :)";
        }
    }

    private String isNoSession(String inDatum){
        if(inDatum.equals("NO SESION")){
            action = "NoSession";
            return "Yes, NO_SESSION returned :(";
        }else{
            // Nothing to-do
            return "Nope, isn't NO_SESSION :)";
        }
    }

    //////////////////////////
    /// Dialog windows ///////
    //////////////////////////

    public void payUIDialog(){
        AlertDialog.Builder builder_payment_dialog = new AlertDialog.Builder(context);
        final LayoutInflater inflater = VendingUIActivity.this.getLayoutInflater();

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
                            + "|" // Empty value for token bleecard
                            + "|" + DataHolderBleData.getMac()
                            + "|" + value
                            + "|" + paymentMethod(modo)
                            + "|" + card_id
                            + "|" ;

                    Log.d(TAG, " --> Datos: " + creditCardData);
                    passwordDialog("cc_payment");

                }if(modo.equals("My Wallet") || modo.equals("Mi Billetera")){
                    walletData = DataHolder.getUsername()
                            + "|" + DataHolder.getPass()
                            + "|" // Empty value for token bleecard
                            + "|" + DataHolderBleData.getMac()
                            + "|" + value
                            + "|" + paymentMethod(modo)
                            + "|" + card_id
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
        final LayoutInflater inflater = VendingUIActivity.this.getLayoutInflater();

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
                    webResponse = new NetActions(context).vendingPayment(creditCardData);
                    Log.d(TAG, " oKHttp response: " + webResponse);

                    if(webResponse.equals("Consumo exitoso")){
                        dialog_pass_ui.dismiss();
                        dialog2.setCanceledOnTouchOutside(false);
                        dialog2.setMessage(getString(R.string.inf_dialog));
                        dialog2.show();

                        //passRsaToBuyChange();
                        makeCharging(value);

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
                    webResponse = new NetActions(context).vendingPayment(walletData);
                    Log.w(TAG, " oKHttp response: " + webResponse);

                    if(webResponse.equals("Consumo exitoso")){

                        dialog_pass_ui.dismiss();
                        dialog2.setCanceledOnTouchOutside(false);
                        dialog2.setMessage(getString(R.string.inf_dialog));
                        dialog2.show();

                        //buyProductByLine(DataHolderBleBuy.getLiSelected()); //this execute the Ble trigger
                        //passRsaToBuyChange();
                        makeCharging(value);

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
                            //passRsaToBuyChange();

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

    ////////////////////////////////////
    //////// Other Actions /////////////
    ////////////////////////////////////

    public void getPriceActivator(String inDatum){
        String[] mainData = inDatum.split(",");

        allowEdit = mainData[0];
        value     = mainData[1];

        valueCharge.setText("$" + value);

        if(allowEdit.equals("1")){
            valueCharge.setEnabled(false);
        }else {
            valueCharge.setEnabled(true);
        }

        dialog2.dismiss();
    }
}
