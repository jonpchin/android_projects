package com.goplaychess.mini_tank;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.UUID.fromString;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private List<Integer> mImageIDs;

    private CustomAdapter mAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> mBluetoothDevices;
    StartBluetoothConnection mConnection;
    private TextView mText;
    public static TextView mBluetoothMessage;

    public static BluetoothService mBluetoothService;

    public static Menu control_panel_menu;
    private final static int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mText = (TextView) findViewById(R.id.main_text_field);

        // Register for broadcasts on BluetoothAdapter state change
        IntentFilter filter_state_changed = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiverStateChanged, filter_state_changed);

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            mText.setText("Bluetooth is NOT supported");
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else{
            mText.setText("Bluetooth is already turned on.");
        }

        // Initialize the button to perform device discovery
        final Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(toggleDiscovery()){
                    scanButton.setText("Disable Scan");
                }else {
                    scanButton.setText("Scan");
                }
            }
        });

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

        TextView knownBluetoothTitle = new TextView(this);
        knownBluetoothTitle.setTextSize(18);
        knownBluetoothTitle.setPadding(150,0,0,0);
        knownBluetoothTitle.setText("Available Bluetooth devices:");

        mListView = (ListView)findViewById(R.id.menuList);;
        mListView.addHeaderView(knownBluetoothTitle);

        mAdapter = new CustomAdapter(this, bluetoothClients, mImageIDs);
        mListView.setAdapter(mAdapter);

        // Register for broadcasts when a device is discovered.
        IntentFilter filter_action_found_ = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiverActionFound, filter_action_found_);

        mListView.setOnItemClickListener(new  AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                mConnection = new StartBluetoothConnection(mBluetoothDevices.get(position-1));
                if(mConnection.isSocketOpen){
                    Intent intent = new Intent(MainActivity.this, ControlPanel.class);
                    startActivity(intent);
                }else{
                    mText.setText("Unable to connect to remote bluetooth server");
                }

            }
        });
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiverActionFound = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // Discovery has found a device. Get the BluetoothDevice
            // object and its info from the Intent.
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String bluetoothClient = device.getName() + ": " + device.getAddress();

                // Don't add an already known device to the list
                if(!mBluetoothDevices.contains(bluetoothClient)){

                    // Newly discovered devices will have a lightbulb icon next to it
                    mImageIDs.add(R.drawable.lightbulb);

                    List<String> bluetoothClients = new ArrayList<String>();
                    bluetoothClients.add(bluetoothClient);
                    mBluetoothDevices.add(device);
                    // Add new bluetooth devices to list and update it dynamically
                    mAdapter.updateListView(bluetoothClient, R.drawable.lightbulb);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_STATE_CHANGED.
    private final BroadcastReceiver mReceiverStateChanged = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        mText.setText("Bluetooth off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        mText.setText("Turning Bluetooth off...");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        mText.setText("Bluetooth on");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        mText.setText("Turning Bluetooth on...");
                        break;
                }
            }
        }
    };

    /**
     * Start device discover with the BluetoothAdapter
     * Returns true if bluetooth discovery is sucessfully enabled
     */
    private boolean toggleDiscovery() {

        boolean result = false;

        // If bluetooth is not discovering then start the discovery
        if(!cancelDiscovery()){
            result = mBluetoothAdapter.startDiscovery();

            if (!result)
            {
                mText.setText("Failed to start bluetooth discovery...");
            }
        }
        return result;
    }

    // Returns true if already is discovering
    private boolean cancelDiscovery(){
        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            return true;
        }
        return false;
    }

    // Starts bluetooth connection thread
    private class StartBluetoothConnection {
        private BluetoothSocket mSocket = null;
        private static final String TAG = "StartBluetooth";
        boolean isSocketOpen = false;

        public StartBluetoothConnection(BluetoothDevice device) {

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's uuid string, also used in the server code.
                String UUID  = readUUIDFromFile();

                if(UUID == "Error: can't show file.")
                {
                    return;
                }
                // HC-06 is 00001101-0000-1000-8000-00805F9B34FB
                //mSocket = device.createRfcommSocketToServiceRecord(fromString(UUID));
                mSocket = device.createRfcommSocketToServiceRecord(fromString("0001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            isSocketOpen = run();
        }

        // Returns true if bluetooth succesfully connects to remote device
        public boolean run() {
            // Cancel discovery because it otherwise slows down the connection.
            cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                cancel();
                return false;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            mBluetoothService = new BluetoothService(mBluetoothAdapter, mHandler);
            mBluetoothService.ConnectedThread(mSocket);
            return true;
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    // Reads the uuid.txt in the res/raw directory and returns the uuid
    private String readUUIDFromFile()
    {
        String result;
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.uuid);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            result = new String(b);
        } catch (Exception e) {
            // e.printStackTrace();
            result = "Error: can't show file.";
        }
        return result;
    }

    /**
     * The Handler that gets information back from the BluetoothService
     */
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case BluetoothService.MessageConstants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mBluetoothMessage.setText(readMessage);
                    if(readMessage == "motion")
                    {
                        MenuItem motionCheckable = control_panel_menu.findItem(R.id.toggle_motion_sensor);
                        motionCheckable.setChecked(false);
                    }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiverStateChanged);
        unregisterReceiver(mReceiverActionFound);
        mBluetoothAdapter.cancelDiscovery();
    }
}
