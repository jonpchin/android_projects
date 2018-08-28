package com.goplaychess.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static BluetoothAdapter mBluetoothAdapter;
    public static CustomAdapter mAdapter;
    public static List<BluetoothDevice> mBluetoothDevices;
    public static BluetoothLeService mBleService;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private boolean mScanning = false;
    private Handler mHandler = new Handler();

    private ListView mListView;
    private List<Integer> mImageIDs;

    private final static int REQUEST_ENABLE_BT = 1;
    private TextView mText;

    public boolean scanLeDevice() {
        final BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();

        if (!mScanning) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    scanner.stopScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;

            scanner.startScan(mLeScanCallback);
        } else {
            mScanning = false;
            scanner.stopScan(mLeScanCallback);
        }
        return mScanning;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = (TextView) findViewById(R.id.main_text_field);

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        List<String> bluetoothClients = new ArrayList<String>();
        mBluetoothDevices = new ArrayList<BluetoothDevice>();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        mImageIDs = new ArrayList<Integer>();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                // Name and MAC address of bluetooth device
                bluetoothClients.add(device.getName() + ": " + device.getAddress());
                mBluetoothDevices.add(device);
                // Already known devices will have a star icon next to it
                mImageIDs.add(R.drawable.star);
            }
        }

        // Initialize the button to perform device discovery
        final Button scanButton = (Button) findViewById(R.id.ble_button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mScanning){
                    scanButton.setText("Scan");
                }else {
                    scanButton.setText("Disable Scan");
                }
                scanLeDevice();
            }
        });

        TextView knownBluetoothTitle = new TextView(this);
        knownBluetoothTitle.setTextSize(18);
        knownBluetoothTitle.setPadding(150, 0, 0, 0);
        knownBluetoothTitle.setText("Available Bluetooth devices:");

        mListView = (ListView) findViewById(R.id.menuList);
        mListView.addHeaderView(knownBluetoothTitle);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                final Intent intent = new Intent(MainActivity.this, ControlPanel.class);
                //intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
                // intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());

                mBleService = new BluetoothLeService(getApplicationContext(),
                        mBluetoothDevices.get(position-1));
                startActivity(intent);

            }
        });

        mAdapter = new CustomAdapter(this, bluetoothClients, mImageIDs);
        mListView.setAdapter(mAdapter);
    }

    // Device scan callback.
    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String bluetoothClient = device.getName() +  ": " + device.getAddress();

            if(!mBluetoothDevices.contains(device)){
                mImageIDs.add(R.drawable.lightbulb);
                mAdapter.updateListView(bluetoothClient,
                        R.drawable.lightbulb);
                mAdapter.notifyDataSetChanged();
                mBluetoothDevices.add(device);
            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.cancelDiscovery();
    }
}
