package btm.app.BleecardUI;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Observable;

import btm.app.R;

import static btm.app.BleecardUI.BleMiniUI.ConnectionState.CONNECT_GATT;
import static btm.app.BleecardUI.BleMiniUI.ConnectionState.DISCOVER_SERVICES;
import static btm.app.BleecardUI.BleMiniUI.ConnectionState.FAILED;
import static btm.app.BleecardUI.BleMiniUI.ConnectionState.IDLE;
import static btm.app.BleecardUI.BleMiniUI.ConnectionState.READ_CHARACTERISTIC;
import static btm.app.BleecardUI.BleMiniUI.ConnectionState.SUCCEEDED;

public class BleMiniUI extends AppCompatActivity {
    public static final String TAG = "DEV -> UI Mini ";

    // The connection to the device, if we are connected.
    private BluetoothGatt mGatt;
    Context context = this;
    Button connect_ble, disconnect_ble;

    // When the logic state changes, State.notifyObservers(this) is called.
    public final StateObservable State = new StateObservable();
    private ConnectionState mState = ConnectionState.IDLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_mini_ui);

        connect_ble = (Button) findViewById(R.id.connect);
        disconnect_ble = (Button) findViewById(R.id.disconnect);

        // Actually set it in response to ACTION_PAIRING_REQUEST.
        final IntentFilter pairingRequestFilter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
        pairingRequestFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        context.getApplicationContext().registerReceiver(mPairingRequestRecevier, pairingRequestFilter);

        // Update the UI.
        State.notifyChanged();

        // Note that we don't actually need to request permission - all apps get BLUETOOTH and BLUETOOTH_ADMIN permissions.
        // LOCATION_COARSE is only used for scanning which I don't need (MAC is hard-coded).

        connect_ble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Connect to the device.
                connectGatt();
            }
        });

        disconnect_ble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disconnect Ble device
                disconnectGatt();
            }
        });


    }

    // This is used to allow GUI fragments to subscribe to state change notifications.
    public static class StateObservable extends Observable {
        private void notifyChanged() {
            setChanged();
            notifyObservers();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        // Disconnect from the device if we're still connected.
        disconnectGatt();

        // Unregister the broadcast receiver.
        context.getApplicationContext().unregisterReceiver(mPairingRequestRecevier);
    }

    // Internal state machine.
    public enum ConnectionState{
        IDLE,
        CONNECT_GATT,
        DISCOVER_SERVICES,
        READ_CHARACTERISTIC,
        FAILED,
        SUCCEEDED,
    }

    /*public byte[] macAddress(){
        return getArguments().getByteArray("mac");
    }*/
    /*public int pinCode(){
        return getArguments().getInt("pin", -1);
    }*/

    // Start the connection process.
    private void connectGatt(){
        // Disconnect if we are already connected.
        disconnectGatt();

        // Update state.
        mState = CONNECT_GATT;
        State.notifyChanged();

        //BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(macAddress());
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("34:15:13:CD:C0:7C");

        // Connect!
        mGatt = device.connectGatt(BleMiniUI.this, false, mBleCallback);

        // WRITE
        characteristic.setWriteType(BluetoothGattCharacteristic.PROPERTY_WRITE);
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        characteristic.setWriteType(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE);

        mWriteCharacteristic = characteristic;

        String str = "TEST";
        byte[] strBytes = str.getBytes();
        characteristic.setValue(strBytes);
        writeCharacteristic(characteristic)
    }

    private void disconnectGatt() {
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
            mGatt = null;
        }
    }

    // The state used by the UI to show connection progress.
    public ConnectionState getConnectionState() {
        return mState;
    }

    private static final int GATT_ERROR = 0x85;
    private static final int GATT_AUTH_FAIL = 0x89;

    private android.bluetooth.BluetoothGattCallback mBleCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    // Connected to the device. Try to discover services.
                    if (gatt.discoverServices()) {
                        // Update state.
                        mState = DISCOVER_SERVICES;
                        State.notifyChanged();
                    } else {
                        // Couldn't discover services for some reason. Fail.
                        disconnectGatt();
                        mState = FAILED;
                        State.notifyChanged();
                    }
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    // If we try to discover services while bonded it seems to disconnect.
                    // We need to debond and rebond...

                    switch (mState) {
                        case IDLE:
                            // Do nothing in this case.
                            break;
                        case CONNECT_GATT:
                            // This can happen if the bond information is incorrect. Delete it and reconnect.
                            deleteBondInformation(gatt.getDevice());
                            connectGatt();
                            break;
                        case DISCOVER_SERVICES:
                            // This can also happen if the bond information is incorrect. Delete it and reconnect.
                            deleteBondInformation(gatt.getDevice());
                            connectGatt();
                            break;
                        case READ_CHARACTERISTIC:
                            // Disconnected while reading the characteristic. Probably just a link failure.
                            gatt.close();
                            mState = FAILED;
                            State.notifyChanged();
                            break;
                        case FAILED:
                        case SUCCEEDED:
                            // Normal disconnection.
                            break;
                    }
                    break;
            }
        }
    };

    public void deleteBondInformation(BluetoothDevice device) {
        try {
            // FFS Google, just unhide the method.
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.d("TAG", e.getMessage());
        }
    }

    private final BroadcastReceiver mPairingRequestRecevier = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(intent.getAction())) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);

                Log.d("++++", "++++" + type);
                /*if (type == BluetoothDevice.PAIRING_VARIANT_PIN) {
                    device.setPin(Util.IntToPasskey(pinCode()));
                    abortBroadcast();
                }
                else {
                    L.w("Unexpected pairing type: " + type);
                }*/
            }
        }
    };

    /*public void sendData(String message) {
        try {
            outStream.write(​message.getBytes());
            outStream.flush();
        } catch (NullPointerException e) {
            Log.e("sendData: nullPointer",e.toString());

        } catch (IOException e) {
            Log.e("sendData: IO",e.toString());
        }
        Log.d(TAG, "SendData => " + message);
    }*/




}
