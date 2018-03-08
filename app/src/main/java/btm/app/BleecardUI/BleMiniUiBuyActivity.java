package btm.app.BleecardUI;

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
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import btm.app.CC;
import btm.app.CompraToken;
import btm.app.DataHolder.DataHolder;
import btm.app.DataHolder.DataHolderBleBuy;
import btm.app.DataHolder.DataHolderBleData;
import btm.app.MainActivity;
import btm.app.Network.NetActions;
import btm.app.R;

public class BleMiniUiBuyActivity extends AppCompatActivity {

    private final static String TAG = "Dev -> Btm Details ";
    private ArrayList<CC> listTc;
    Context context = this;

    AlertDialog.Builder builder;
    ProgressDialog dialog2;
    AlertDialog dialogBle;
    AlertDialog dialog_pass_ui;

    private TextView isSerial;
    private TextView mConnectionState;
    private TextView mDataField;
    private TextView line_num;

    private String mDeviceName;
    private String mDeviceAddress;
    private String modo, data;
    String rsa;
    String bleeResponse;
    String password_dialog;
    String card_info;
    String card_id, webResponse, dataResponse;
    String creditCardData, walletData, subscriptionData;
    String action = "list";
    Spinner tC, metodo;
    Button buy;

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;

    public final static UUID HM_RX_TX = UUID.fromString(SampleGattAttributes.HM_RX_TX);
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
            String mac_address_dataholder = DataHolderBleData.getMac();
            Log.d(TAG, " -> Mac DatHolder: " + mac_address_dataholder);
            mBluetoothLeService.connect(mac_address_dataholder);
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
                bleeResponse = intent.getStringExtra(mBluetoothLeService.EXTRA_DATA);
                Log.d(TAG, "---------->" + bleeResponse);
                displayData(intent.getStringExtra(mBluetoothLeService.EXTRA_DATA));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_mini_ui_buy);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.ui_ble_mini_buy));

        dialog2 = new ProgressDialog(BleMiniUiBuyActivity.this);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        // is serial present?
        isSerial    = (TextView) findViewById(R.id.isSerial);
        mDataField  = (TextView) findViewById(R.id.data_value);
        line_num    = (TextView) findViewById(R.id.ble_id);
        metodo      = (Spinner) findViewById(R.id.payment_method);
        tC          = (Spinner) findViewById(R.id.spinnerTipoTC);
        buy         = (Button) findViewById(R.id.buy);

        listTc = new ArrayList<CC>();
        line_num.setText(getString(R.string.ui_ble_mini_line_selected) + " #" + DataHolderBleBuy.getLiSelected());

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               // initBleDataSearch();
            }
        }, 3000); //Timer is in ms here.

        metodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, final View view, int position, long id) {
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
                            //addCreditCard.setVisibility(View.VISIBLE);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //addCreditCard.setVisibility(View.GONE);
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
                            + "|" + getIdSubscription()
                            + "|" + card_id
                            + "|" + paymentMethod(modo)
                            + "|" + DataHolderBleBuy.getLiSelected()
                            + "|" ;

                    Log.d(TAG, "--> Datos: " + creditCardData);
                    passwordDialog("cc_payment");

                }if(modo.equals("My Wallet") || modo.equals("Mi Billetera")){
                    walletData = DataHolder.getUsername()
                            + "|" + DataHolder.getPass()
                            + "|" + DataHolderBleData.getId()
                            + "|" + getIdSubscription()
                            + "|"
                            + "|" + paymentMethod(modo)
                            + "|" + DataHolderBleBuy.getLiSelected()
                            + "|" ;

                    Log.d(TAG, "--> Datos: " + walletData);
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
                                + "|" + DataHolderBleBuy.getLiSelected()
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
    }

    public void payWithCreditCard(){
        try {
            webResponse = new btm.app.Network.NetActions(context).btmMiniPayment(creditCardData);
            Log.d(TAG, " oKHttp response: " + webResponse);

            if(webResponse.equals("Consumo exitoso")){
                initBleDataSearch();
                dialog_pass_ui.dismiss();
                dialog2.setCanceledOnTouchOutside(false);
                dialog2.setMessage(getString(R.string.inf_dialog));
                dialog2.show();
            }else{
                dialog_pass_ui.dismiss();
                customDialog(webResponse);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void payWithWallet(){
        try {
            webResponse = new btm.app.Network.NetActions(context).btmMiniPayment(walletData);
            Log.d(TAG, " oKHttp response: " + webResponse);

            if(webResponse.equals("Consumo exitoso")){
                initBleDataSearch();
                dialog_pass_ui.dismiss();
                dialog2.setCanceledOnTouchOutside(false);
                dialog2.setMessage(getString(R.string.inf_dialog));
                dialog2.show();
            }else{
                dialog_pass_ui.dismiss();
                customDialog(webResponse);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void payWithSubscriptions(){
        try {
            webResponse = new btm.app.Network.NetActions(context).btmMiniPayment(subscriptionData);
            Log.d(TAG, " oKHttp response: " + webResponse);

            if(webResponse.equals("Consumo exitoso")){
                initBleDataSearch();
                dialog_pass_ui.dismiss();
                dialog2.setCanceledOnTouchOutside(false);
                dialog2.setMessage(getString(R.string.inf_dialog));
                dialog2.show();
            }else{
                dialog_pass_ui.dismiss();
                customDialog(webResponse);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void customDialog(String inDatum){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(inDatum);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                mBluetoothLeService.disconnect();
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

    public void customDialogNoMove(String inDatum){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(inDatum);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                mBluetoothLeService.disconnect();
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void customDialogOkPayment(String inDatum){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(inDatum);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                mBluetoothLeService.disconnect();
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void passwordDialog(final String inDatum){
        AlertDialog.Builder builder_pass_dialog = new AlertDialog.Builder(context);
        final LayoutInflater inflater = BleMiniUiBuyActivity.this.getLayoutInflater();

        //final EditText input = (EditText) findViewById(R.id.password);
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.ui_aux_pass, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.password);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder_pass_dialog.setCancelable(false);
        builder_pass_dialog.setView(viewInflated)
                .setPositiveButton(R.string.ui_general_dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        password_dialog = input.getText().toString();
                        if(password_dialog.equals(DataHolder.getPass())){
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
            dataResponse = new btm.app.Network.NetActions(context).btmMiniActiveSubscription(datos);
            Log.d(TAG, " oKHttp response: " + data);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataResponse;
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
            if(data.equals("1") ) {
                Log.d(TAG, "It's returning 1 number");
                customDialogOkPayment("La máquina ha procesado exitosamente tu pedido.");
            }else if (data.equals("0")){
                Log.d(TAG, "It's returning 0 number");
                String datos_ws = DataHolder.getUsername();
                try {
                    datos_ws = new btm.app.Network.NetActions(context).btmMiniChargeBack(datos_ws);
                    Log.d(TAG, " ***///*** oKHttp response: " + datos_ws);

                    if( datos_ws.equals("Pago Chargeback exitoso")){
                        customDialog("La máquina no procesó tu pedido. Si usaste como método de pago Tarjeta de Crédito ó Mi Billetera el valor pagado se reintegrará a Mi Billetera, sino la cantidad debitada se reintegrará a tu suscripcion.");
                    }else {
                        customDialog("La máquina no procesó tu pedido. Si usaste como método de pago Tarjeta de Crédito ó Mi Billetera el valor pagado se reintegrará a Mi Billetera, sino la cantidad debitada se reintegrará a tu suscripcion.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //mBluetoothLeService.disconnect();
                //Intent gotoHome = new Intent(context, MainActivity.class);
                //startActivity(gotoHome);
            }
            else {
                Log.d(TAG, "It's returning the 9 number");
            }
        }
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
                buyProductByLine(DataHolderBleBuy.getLiSelected());
            }
        });
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
        String rsaValue = getStringJson(getRsaBuyBle());
        Log.d(TAG, "Sending result = " + rsaValue);
        //String str = "0a6455df41b578fdcf0115d61b0043c9";
        //Log.d(TAG, "Sending result = " + str);
        final byte[] tx = hexStringToByteArray(rsaValue);
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
        Log.d(TAG, "Sending result = " + lineNumber);
        if(mConnected) {
            characteristicTX.setValue(lineNumber);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
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

    public String getRsaBuyBle(){
        try {
            dataResponse = new btm.app.Network.NetActions(context).getBleBuyRsa(DataHolderBleData.getId());
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
}
