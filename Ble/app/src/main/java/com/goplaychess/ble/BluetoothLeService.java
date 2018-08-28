package com.goplaychess.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.UUID;

/**
 * Created by jonc on 11/2/2017.
 */

// A service that interacts with the BLE device via the Android BLE API.
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private Context mContext;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString("C6A2B7CC-B19A-42B1-A67F-A7C28B741B28");

    public BluetoothLeService(Context context, BluetoothDevice device){
        mBluetoothDevice = device;
        mContext = context;

        if (device == null) {
            return;
        }

        mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // Various callback methods defined by the BLE API.
    private final BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    String intentAction;
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        intentAction = ACTION_GATT_CONNECTED;
                        mConnectionState = STATE_CONNECTED;
                        broadcastUpdate(intentAction);
                        Log.i("testing123", "Connected to GATT server.");
                        Log.i("testing123", "Attempting to start service discovery:" +
                                mBluetoothGatt.discoverServices());

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        intentAction = ACTION_GATT_DISCONNECTED;
                        mConnectionState = STATE_DISCONNECTED;
                        Log.i(TAG, "Disconnected from GATT server.");
                        broadcastUpdate(intentAction);
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {

                            public void run(){
                                ControlPanel.lightLevelLabel.setText("Service(s) found!");
                            }
                        });
                    } else {
                        Log.w(TAG, "onServicesDiscovered received: " + status);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 final BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {

                            public void run(){
                                ControlPanel.lightLevelLabel.setText("Light: " + (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0).toString()));
                            }
                        });
                    }
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt,
                                                 final BluetoothGattCharacteristic characteristic,
                                                 int status){
                    BluetoothGattCharacteristic mCharacteristicLEDToggle =
                            gatt.getService(ControlPanel.uuidFromShortCode16("A003")).getCharacteristic(
                                    ControlPanel.uuidFromShortCode16("A004"));

                    if(characteristic.equals(mCharacteristicLEDToggle)){

                        // SENSOR_SERVICE_UUID
                        BluetoothGattService mCustomService = gatt.getService(ControlPanel.uuidFromShortCode16("A003"));
                        if (mCustomService == null) {
                            Log.w("Test", "Custom SENSOR_SERVICE_UUID not found");
                            return;
                        }

                        // LIGHT_LEVELS_CHARACTERISTIC_UUID
                        BluetoothGattCharacteristic mReadCharacteristic = mCustomService.getCharacteristic(
                                ControlPanel.uuidFromShortCode16("A005"));

                        if(gatt.readCharacteristic(mReadCharacteristic) == false){
                            Log.w("Test", "Failed to read light characteristic");
                        }
                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt,
                                                    final BluetoothGattCharacteristic characteristic){
                }
            };


    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        mContext.sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        mContext.sendBroadcast(intent);
    }

    public BluetoothGatt getBluetoothGatt(){
        return mBluetoothGatt;
    }
}