package com.example.tiltsensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensorMagnetometer;
    private Sensor mSensorAccelerometer;


    private TextView mTextSensorAzimuth;
    private TextView mTextSensorRoll;
    private TextView mTextSensorPitch;

    private float[] mAccelerometerData = new float[3];
    private float[] mMagnometerData = new float[3];

    private static final float VALUE_DRIFT = 0.05f;

    private ImageView mSpotTop;
    private ImageView mSpotBottom;
    private ImageView mSpotRight;
    private ImageView mSpotLeft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mTextSensorAzimuth = findViewById(R.id.value_azimuth);
        mTextSensorPitch = findViewById(R.id.value_pitch);
        mTextSensorRoll = findViewById(R.id.value_roll);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSpotTop = findViewById(R.id.spot_top);
        mSpotBottom = findViewById(R.id.spot_bottom);
        mSpotRight = findViewById(R.id.spot_right);
        mSpotLeft = findViewById(R.id.spot_left);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            System.out.println("No Accelerometer Sensor");
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            System.out.println("No Magnetometer Sensor");
        }
    }

    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnometerData = sensorEvent.values.clone();
                break;
            default:
                return;

        }

        float[] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix, null,
                mAccelerometerData, mMagnometerData);

        float[] orientationValues = new float[3];

        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrix, orientationValues);
        }

        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        mTextSensorRoll.setText(getResources().getString(R.string.value_format, roll));
        mTextSensorPitch.setText(getResources().getString(R.string.value_format, pitch));
        mTextSensorAzimuth.setText(getResources().getString(R.string.value_format, azimuth));

        if (Math.abs(pitch) < VALUE_DRIFT) {
            pitch = 0;
        }
        if (Math.abs(roll) < VALUE_DRIFT) {
            roll = 0;
        }

        mSpotTop.setAlpha(0f);
        mSpotRight.setAlpha(0f);
        mSpotLeft.setAlpha(0f);
        mSpotBottom.setAlpha(0f);

        if (pitch > 0) {
            mSpotBottom.setAlpha(pitch);
        }
        else {
            mSpotTop.setAlpha(Math.abs(pitch));
        }
        if (roll > 0) {
            mSpotLeft.setAlpha(roll);
        }
        else {
            mSpotRight.setAlpha(Math.abs(roll));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}