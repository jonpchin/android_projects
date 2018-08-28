package com.goplaychess.bt_pi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by jonc on 10/15/2017.
 */

public class ControlPanel extends AppCompatActivity {

    static final String FORWARD = "U";
    static final String STOP = "S";
    static final String SPIN_RIGHT = "R";
    static final String SPIN_LEFT = "L";
    static final String BACKWARD = "B";
    static final String TOP_LEFT = "1";
    static final String TOP_RIGHT = "2";
    static final String BOTTOM_LEFT = "3";
    static final String BOTTOM_RIGHT = "4";

    static final char END_CHAR = '\0';
    static final int DEFAULT_SPEED = 255;

    static final  String DELIMITER = "_";

    int speedOfMotors = 255;
    TextView speedLabel;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_panel);
        final BluetoothService mBluetoothService = MainActivity.mBluetoothService;
        MainActivity.mBluetoothMessage = (TextView) findViewById(R.id.btMessage);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // updated continuously as the user slides the thumb
                speedLabel.setText("Progress: " + progress);
                speedOfMotors = progress;
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

        // set a change listener on the SeekBar
        SeekBar seekBar = (SeekBar)findViewById(R.id.speedBar);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int progress = seekBar.getProgress();
        speedLabel = (TextView)findViewById(R.id.textSpeed);
        speedLabel.setText("Progress: " + progress);

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

        OnCheckedChangeListener changeChecker = new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)  {

                // If its enabled then disable distance sensor
                mBluetoothService.write(("dist" + DELIMITER + speedOfMotors + END_CHAR).getBytes());
                MainActivity.mBluetoothMessage.setText("Distance: ");

            }
        };

        ToggleButton toggleSensor = (ToggleButton)findViewById(R.id.toggleSensor);
        toggleSensor.setOnCheckedChangeListener(changeChecker);

        upArrow.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
            if(e.getAction() == MotionEvent.ACTION_DOWN){
                // Do something that should only happen when the user is touching the screen
                mBluetoothService.write((FORWARD + DELIMITER + speedOfMotors + END_CHAR).getBytes());
            }
            else if (e.getAction()  == MotionEvent.ACTION_UP) {
                mBluetoothService.write((STOP + DELIMITER + DEFAULT_SPEED + END_CHAR).getBytes());
            }
            return false;
            }
        });

        rightArrow.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
            if(e.getAction() == MotionEvent.ACTION_DOWN) {
                mBluetoothService.write((SPIN_RIGHT + DELIMITER + speedOfMotors + END_CHAR).getBytes());
            }
            else if (e.getAction()  == MotionEvent.ACTION_UP) {
                mBluetoothService.write((STOP + DELIMITER + DEFAULT_SPEED + END_CHAR).getBytes());
            }
            return false;
            }
        });


        backArrow.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    mBluetoothService.write((BACKWARD + DELIMITER + speedOfMotors + END_CHAR).getBytes());
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    mBluetoothService.write((STOP + DELIMITER + DEFAULT_SPEED + END_CHAR).getBytes());
                }
                return false;
            }
        });

        leftArrow.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    mBluetoothService.write((SPIN_LEFT + DELIMITER + speedOfMotors + END_CHAR).getBytes());
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    mBluetoothService.write((STOP + DELIMITER + DEFAULT_SPEED + END_CHAR).getBytes());
                }
                return false;
            }
        });

        topLeft.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    mBluetoothService.write((TOP_LEFT + DELIMITER + speedOfMotors + END_CHAR).getBytes());
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    mBluetoothService.write((STOP + DELIMITER + DEFAULT_SPEED + END_CHAR).getBytes());
                }
                return false;
            }
        });

        topRight.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    mBluetoothService.write((TOP_RIGHT + DELIMITER + speedOfMotors + END_CHAR).getBytes());
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    mBluetoothService.write((STOP + DELIMITER + DEFAULT_SPEED + END_CHAR).getBytes());
                }
                return false;
            }
        });

        bottomRight.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    mBluetoothService.write((BOTTOM_RIGHT + DELIMITER + speedOfMotors + END_CHAR).getBytes());
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    mBluetoothService.write((STOP + DELIMITER + DEFAULT_SPEED + END_CHAR).getBytes());
                }
                return false;
            }
        });

        bottomLeft.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    mBluetoothService.write((BOTTOM_LEFT + DELIMITER + speedOfMotors + END_CHAR).getBytes());
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    mBluetoothService.write((STOP + DELIMITER + DEFAULT_SPEED + END_CHAR).getBytes());
                }
                return false;
            }
        });
    }
}
