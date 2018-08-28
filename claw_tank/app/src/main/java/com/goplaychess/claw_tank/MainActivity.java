package com.goplaychess.claw_tank;

import android.os.AsyncTask;
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

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    // Standing behind claw (for left and right position)
    ToggleButton clawServo;
    ToggleButton leftServo;
    ToggleButton rightServo;
    ToggleButton baseServo;
    ToggleButton motors;
    TextView pwmOrAngle;

    SeekBar speedBar;
    int speedBarValue = 0;

    final String CLAW = "claw";
    final String LEFT = "left";
    final String RIGHT = "right";
    final String BASE = "base";

    final String baseURL = "http://192.168.4.1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clawServo  = (ToggleButton) findViewById(R.id.claw_servo);
        leftServo  = (ToggleButton) findViewById(R.id.left_servo);
        rightServo = (ToggleButton) findViewById(R.id.right_servo);
        baseServo  = (ToggleButton) findViewById(R.id.base_servo);
        motors     = (ToggleButton) findViewById(R.id.motors);

        clawServo.setOnCheckedChangeListener(changeChecker);
        leftServo.setOnCheckedChangeListener(changeChecker);
        rightServo.setOnCheckedChangeListener(changeChecker);
        baseServo.setOnCheckedChangeListener(changeChecker);
        motors.setOnCheckedChangeListener(changeChecker);

        pwmOrAngle = (TextView) findViewById(R.id.pwm_or_angle);
        speedBar = (SeekBar)findViewById(R.id.speed_bar);

        SeekBar.OnSeekBarChangeListener servoSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // updated continuously as the user slides the thumb
                pwmOrAngle.setText("Angle: " + progress);
                setServoAngle(Integer.toString(progress));
                speedBarValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        speedBar.setOnSeekBarChangeListener(servoSeekBarChangeListener);

        ImageView rightArrow = (ImageView)findViewById(R.id.arrow_right);
        ImageView leftArrow = (ImageView)findViewById(R.id.arrow_left);

        rightArrow.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    if(motors.isChecked()){
                        if(speedBarValue < 255){ // PWM
                            ++speedBarValue;
                            pwmOrAngle.setText("PWM: " + speedBarValue);
                        }

                    }else{
                        if(speedBarValue < 180){ // Servo angle
                            ++speedBarValue;
                            pwmOrAngle.setText("Angle: " + speedBarValue);
                            setServoAngle(Integer.toString(speedBarValue));
                        }
                    }
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {

                }
                return false;
            }
        });

        leftArrow.setOnTouchListener(new View.OnTouchListener() {
            //@Override
            public boolean onTouch(View v, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    if(motors.isChecked()){
                        if(speedBarValue > 0){ // PWM
                            --speedBarValue;
                            pwmOrAngle.setText("PWM: " + speedBarValue);
                        }

                    }else{
                        if(speedBarValue > 0){ // Servo angle
                            --speedBarValue;
                            pwmOrAngle.setText("Angle: " + speedBarValue);
                            setServoAngle(Integer.toString(speedBarValue));
                        }
                    }
                }
                else if (e.getAction()  == MotionEvent.ACTION_UP) {

                }
                return false;
            }
        });
    }

    OnCheckedChangeListener changeChecker = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)  {
            if (isChecked){
                if (buttonView == clawServo) {
                    leftServo.setChecked(false);
                    rightServo.setChecked(false);
                    baseServo.setChecked(false);
                    motors.setChecked(false);

                    try {
                        attachServo(CLAW);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    setServoButtonsVisibility(false);
                }
                if (buttonView == leftServo) {
                    clawServo.setChecked(false);
                    rightServo.setChecked(false);
                    baseServo.setChecked(false);
                    motors.setChecked(false);

                    try {
                        attachServo(LEFT);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    setServoButtonsVisibility(false);
                }
                if (buttonView == rightServo) {
                    clawServo.setChecked(false);
                    leftServo.setChecked(false);
                    baseServo.setChecked(false);
                    motors.setChecked(false);

                    try{
                        attachServo(RIGHT);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    setServoButtonsVisibility(false);
                }
                if (buttonView == baseServo) {
                    clawServo.setChecked(false);
                    leftServo.setChecked(false);
                    rightServo.setChecked(false);
                    motors.setChecked(false);

                    try{
                        attachServo(BASE);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    setServoButtonsVisibility(false);
                }
                if (buttonView == motors) {
                    clawServo.setChecked(false);
                    leftServo.setChecked(false);
                    rightServo.setChecked(false);
                    baseServo.setChecked(false);
                    initMotorText();
                    setServoButtonsVisibility(true);
                    detatchServo();
                }
            }else{
                detatchServo();
            }
        }
    };

    private void setServoButtonsVisibility(boolean isVisible){

        ImageView topArrow = (ImageView)findViewById(R.id.arrow_up);
        ImageView bottomArrow = (ImageView)findViewById(R.id.arrow_down);
        ImageView topRightArrow = (ImageView)findViewById(R.id.arrow_top_right);
        ImageView topLeftArrow = (ImageView)findViewById(R.id.arrow_top_left);
        ImageView bottomLeftArrow = (ImageView)findViewById(R.id.arrow_bottom_left);
        ImageView bottomRightArrow = (ImageView)findViewById(R.id.arrow_bottom_right);

        if(isVisible){
            topArrow.setVisibility(View.VISIBLE);
            bottomArrow.setVisibility(View.VISIBLE);
            topRightArrow.setVisibility(View.VISIBLE);
            topLeftArrow.setVisibility(View.VISIBLE);
            bottomRightArrow.setVisibility(View.VISIBLE);
            bottomLeftArrow.setVisibility(View.VISIBLE);
        }else{
            topArrow.setVisibility(View.INVISIBLE);
            bottomArrow.setVisibility(View.INVISIBLE);
            topRightArrow.setVisibility(View.INVISIBLE);
            topLeftArrow.setVisibility(View.INVISIBLE);
            bottomRightArrow.setVisibility(View.INVISIBLE);
            bottomLeftArrow.setVisibility(View.INVISIBLE);
        }
    }

    private void initMotorText(){
        speedBarValue = 255;
        speedBar.setMax(speedBarValue);
        speedBar.setProgress(speedBarValue);
    }

    private void setServoAngle(String angle){
        String fullURL = baseURL + "turnservo?degrees=" + angle;
        AsyncTask<String, Integer, String> result  = new GetUrlContentTask().execute(fullURL);
    }

    private void detatchServo(){
        String fullURL = baseURL + "detachservo";
        AsyncTask<String, Integer, String> result  = new GetUrlContentTask().execute(fullURL);
    }

    private void attachServo(String servoName) throws ExecutionException, InterruptedException {
        String fullURL = baseURL + "attachservo?servoname=" + servoName;
        String servoAngle  = new GetUrlContentTask().execute(fullURL).get();

        // Return servo angle in String format
        speedBarValue = Integer.valueOf(servoAngle);
        speedBar.setMax(180);
        speedBar.setProgress(speedBarValue);
        pwmOrAngle.setText("Angle: " + servoAngle);
    }
}
