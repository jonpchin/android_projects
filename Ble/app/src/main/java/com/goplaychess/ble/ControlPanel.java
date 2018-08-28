package com.goplaychess.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.UUID;

/**
 * Created by jonc on 11/4/2017.
 */


public class ControlPanel extends AppCompatActivity {

    private TextView speedLabel;
    static TextView lightLevelLabel;
    private int ledToggle;

    class Direction {
        static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3, NORTHEAST = 4,
            SOUTHEAST = 5, SOUTHWEST = 6, NORTHWEST = 7, STOP = 8;
    }

    public static final String baseBluetoothUuidPostfix = "0000-1000-8000-00805F9B34FB";

    public static UUID uuidFromShortCode16(String shortCode16) {
        return UUID.fromString("0000" + shortCode16 + "-" + baseBluetoothUuidPostfix);
    }

    public static UUID uuidFromShortCode32(String shortCode32) {
        return UUID.fromString(shortCode32 + "-" + baseBluetoothUuidPostfix);
    }

    public void writeLEDTogleCharacteristic() {

        BluetoothGatt bluetoothGatt = MainActivity.mBleService.getBluetoothGatt();
        if (MainActivity.mBluetoothAdapter == null || bluetoothGatt == null) {
            Log.w("test", "BluetoothAdapter not initialized");
            return;
        }

        // SENSOR_SERVICE_UUID
        BluetoothGattService mCustomService = bluetoothGatt.getService(uuidFromShortCode16("A003"));
        if(mCustomService == null){
            Log.w("Test", "Custom BLE Service not found");
            return;
        }

        // LED_TOGGLE_CHARACTERISTIC_UUID
        BluetoothGattCharacteristic mWriteCharacteristic = mCustomService.getCharacteristic(uuidFromShortCode16("A004"));
        mWriteCharacteristic.setValue(1-ledToggle, BluetoothGattCharacteristic.FORMAT_UINT8,0);

        if(ledToggle == 1){
            ledToggle = 0;
        }else{
            ledToggle = 1;
        }

        if(bluetoothGatt.writeCharacteristic(mWriteCharacteristic) == false){
            Log.w("Test", "Failed to write characteristic");

        }
    }

    public void writeMotorDirectionCharacteristic(int direction) {

        BluetoothGatt bluetoothGatt = MainActivity.mBleService.getBluetoothGatt();
        if (MainActivity.mBluetoothAdapter == null || bluetoothGatt == null) {
            Log.w("test", "BluetoothAdapter not initialized");
            return;
        }

        // MOTOR_SERVICE_UUID
        BluetoothGattService mCustomService = bluetoothGatt.getService(uuidFromShortCode16("A000"));
        if(mCustomService == null){
            Log.w("Test", "Custom BLE Service not found");
            return;
        }

        // MOTOR_DIRECTION_CHARACTERISTIC_UUID
        BluetoothGattCharacteristic mWriteCharacteristic = mCustomService.getCharacteristic(uuidFromShortCode16("A001"));
        mWriteCharacteristic.setValue(direction, android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8,0);
        if(bluetoothGatt.writeCharacteristic(mWriteCharacteristic) == false){
            Log.w("Test", "Failed to write characteristic");
        }
    }

    public void writeMotorSpeedCharacteristic(int speed) {

        BluetoothGatt bluetoothGatt = MainActivity.mBleService.getBluetoothGatt();
        if (MainActivity.mBluetoothAdapter == null || bluetoothGatt == null) {
            Log.w("test", "BluetoothAdapter not initialized");
            return;
        }

        // LED_SERVICE_UUID
        BluetoothGattService mCustomService = bluetoothGatt.getService(uuidFromShortCode16("A000"));
        if(mCustomService == null){
            Log.w("Test", "Custom BLE Service not found");
            return;
        }

        // MOTOR_SPEED_CHARACTERISTIC_UUID
        BluetoothGattCharacteristic mWriteCharacteristic = mCustomService.getCharacteristic(uuidFromShortCode16("A002"));
        mWriteCharacteristic.setValue(speed, BluetoothGattCharacteristic.FORMAT_UINT8,0);
        if(bluetoothGatt.writeCharacteristic(mWriteCharacteristic) == false){
            Log.w("Test", "Failed to write characteristic");
        }
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_panel);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // updated continuously as the user slides the thumb
                speedLabel.setText("Speed: " + progress);
                writeMotorSpeedCharacteristic(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // called when the user first touches the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // called after the user finishes moving the SeekBar
            }
        };

        OnCheckedChangeListener changeChecker = new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)  {

                writeLEDTogleCharacteristic();
            }
        };

        ToggleButton toggleSensor = (ToggleButton)findViewById(R.id.toggleSensor);
        toggleSensor.setOnCheckedChangeListener(changeChecker);

        ledToggle = 1;

        // set a change listener on the SeekBar
        SeekBar seekBar = (SeekBar)findViewById(R.id.speedBar);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int progress = seekBar.getProgress();
        speedLabel = (TextView)findViewById(R.id.textSpeed);
        speedLabel.setText("Speed: " + progress);

        lightLevelLabel = (TextView)findViewById(R.id.btLightSensor);
        lightLevelLabel.setText("Light: " + progress);

        ImageView upArrow = (ImageView)findViewById(R.id.arrow_up);
        upArrow.setImageResource(R.drawable.arrow);

        ImageView rightArrow = (ImageView)findViewById(R.id.arrow_right);
        rightArrow.setImageResource(R.drawable.arrow);

        ImageView backArrow = (ImageView)findViewById(R.id.arrow_back);
        backArrow.setImageResource(R.drawable.arrow);

        ImageView leftArrow = (ImageView)findViewById(R.id.arrow_left);
        leftArrow.setImageResource(R.drawable.arrow);

        ImageView topLeft = (ImageView)findViewById(R.id.top_left);
        topLeft.setImageResource(R.drawable.arrow);

        ImageView topRight = (ImageView)findViewById(R.id.top_right);
        topRight.setImageResource(R.drawable.arrow);

        ImageView bottomRight = (ImageView)findViewById(R.id.bottom_right);
        bottomRight.setImageResource(R.drawable.arrow);

        ImageView bottomLeft = (ImageView)findViewById(R.id.bottom_left);
        bottomLeft.setImageResource(R.drawable.arrow);

        upArrow.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN){
                    // Do something that should only happen when the user is touching the screen
                    writeMotorDirectionCharacteristic(Direction.NORTH);
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    writeMotorDirectionCharacteristic(Direction.STOP);
                }
                return false;
            }
        });

        backArrow.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    writeMotorDirectionCharacteristic(Direction.SOUTH);
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    writeMotorDirectionCharacteristic(Direction.STOP);
                }
                return false;
            }
        });

        rightArrow.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    writeMotorDirectionCharacteristic(Direction.EAST);
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    writeMotorDirectionCharacteristic(Direction.STOP);
                }
                return false;
            }
        });

        leftArrow.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    writeMotorDirectionCharacteristic(Direction.WEST);
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    writeMotorDirectionCharacteristic(Direction.STOP);
                }
                return false;
            }
        });


        topLeft.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    writeMotorDirectionCharacteristic(Direction.NORTHWEST);
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    writeMotorDirectionCharacteristic(Direction.STOP);
                }
                return false;
            }
        });

        topRight.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    writeMotorDirectionCharacteristic(Direction.NORTHEAST);
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    writeMotorDirectionCharacteristic(Direction.STOP);
                }
                return false;
            }
        });

        bottomRight.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    writeMotorDirectionCharacteristic(Direction.SOUTHEAST);
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    writeMotorDirectionCharacteristic(Direction.STOP);
                }
                return false;
            }
        });

        bottomLeft.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    writeMotorDirectionCharacteristic(Direction.SOUTHWEST);
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    writeMotorDirectionCharacteristic(Direction.STOP);
                }
                return false;
            }
        });

    }
}