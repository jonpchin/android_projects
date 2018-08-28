package com.goplaychess.mini_tank;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

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
    static final String MOTION_ON = "N";
    static final String MOTION_OFF = "F";

    static final char END_CHAR = '\0';
    static final int STOP_SPEED = 1023;

    static final  String DELIMITER = "_";

    TextView speedLabel;
    // For simplicity we will use one seekbar to control speed of both motors
    int speedOfMotors = 64;
    private boolean motionIsChecked = false;

    BluetoothService mBluetoothService;

    // Converts the 0-64 scale to 0-1023 scale of mini tank for the PWM
    public int convertSpeedToPWM(int speed){
        if(speed == 64){
            return 1023;
        }
        return (speed * 16);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MainActivity.control_panel_menu = menu;
        getMenuInflater().inflate(R.menu.control_panel_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem motionCheckable = menu.findItem(R.id.toggle_motion_sensor);
        motionCheckable.setChecked(motionIsChecked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggle_motion_sensor:
                motionIsChecked = !item.isChecked();
                if(motionIsChecked)
                {
                    mBluetoothService.write((MOTION_ON + DELIMITER + STOP_SPEED + END_CHAR).getBytes());
                    MainActivity.mBluetoothMessage.setText("");
                }
                else
                {
                    mBluetoothService.write((MOTION_OFF + DELIMITER + STOP_SPEED + END_CHAR).getBytes());
                    MainActivity.mBluetoothMessage.setText("");
                }
                item.setChecked(motionIsChecked);
                return true;
            default:
                return false;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_panel);
        mBluetoothService = MainActivity.mBluetoothService;
        MainActivity.mBluetoothMessage = (TextView) findViewById(R.id.control_panel_message);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // updated continuously as the user slides the thumb
                int convertedSpeed = convertSpeedToPWM(progress);
                speedLabel.setText("PWM: " + convertedSpeed);
                speedOfMotors = convertedSpeed;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        // set a change listener on the SeekBar
        SeekBar speedBar = (SeekBar)findViewById(R.id.speedBar);
        speedBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int progress = speedBar.getProgress();
        int convertedSpeed = convertSpeedToPWM(progress);
        speedLabel = (TextView)findViewById(R.id.textSpeed);
        speedLabel.setText("PWM: " + convertedSpeed);
        speedOfMotors = convertedSpeed;

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
                mBluetoothService.write((FORWARD +  DELIMITER + speedOfMotors + END_CHAR).getBytes());
            }
            else if (e.getAction()  == MotionEvent.ACTION_UP) {
                mBluetoothService.write((STOP + DELIMITER + STOP_SPEED + END_CHAR).getBytes());
            }
            return false;
            }
        });

        rightArrow.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
            if(e.getAction() == MotionEvent.ACTION_DOWN) {
                mBluetoothService.write((SPIN_RIGHT +  DELIMITER + speedOfMotors + END_CHAR).getBytes());
            }
            else if (e.getAction()  == MotionEvent.ACTION_UP) {
                mBluetoothService.write((STOP + DELIMITER + STOP_SPEED + END_CHAR).getBytes());
            }
            return false;
            }
        });


        backArrow.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    mBluetoothService.write((BACKWARD +  DELIMITER + speedOfMotors + END_CHAR).getBytes());
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    mBluetoothService.write((STOP + DELIMITER + STOP_SPEED + END_CHAR).getBytes());
                }
                return false;
            }
        });

        leftArrow.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    mBluetoothService.write((SPIN_LEFT +  DELIMITER + speedOfMotors+ END_CHAR).getBytes());
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    mBluetoothService.write((STOP + DELIMITER + STOP_SPEED + END_CHAR).getBytes());
                }
                return false;
            }
        });

        // Top left and diagonal movment only has one wheel moving and that will be handled on the tank side
        // so concating PWMA and PWMB speeds is fine for now
        topLeft.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    mBluetoothService.write((TOP_LEFT + DELIMITER + speedOfMotors + END_CHAR).getBytes());
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    mBluetoothService.write((STOP + DELIMITER + STOP_SPEED + END_CHAR).getBytes());
                }
                return false;
            }
        });

        topRight.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    mBluetoothService.write((TOP_RIGHT  + DELIMITER + speedOfMotors + END_CHAR).getBytes());
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {
                    mBluetoothService.write((STOP + DELIMITER + STOP_SPEED + END_CHAR).getBytes());
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
                    mBluetoothService.write((STOP + DELIMITER + STOP_SPEED + END_CHAR).getBytes());
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
                    mBluetoothService.write((STOP + DELIMITER + STOP_SPEED + END_CHAR).getBytes() );
                }
                return false;
            }
        });
    }
}
