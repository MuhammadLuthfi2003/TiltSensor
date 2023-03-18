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

    // inisialisasi variable
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

        // melock orientasi app android menjadi potrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // mendapatkan refrence textview
        mTextSensorAzimuth = findViewById(R.id.value_azimuth);
        mTextSensorPitch = findViewById(R.id.value_pitch);
        mTextSensorRoll = findViewById(R.id.value_roll);


        // mendapatkan refrence sensor manager dan sensor yang tersedia dalam device
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // mendapatkan refrence imageview
        mSpotTop = findViewById(R.id.spot_top);
        mSpotBottom = findViewById(R.id.spot_bottom);
        mSpotRight = findViewById(R.id.spot_right);
        mSpotLeft = findViewById(R.id.spot_left);

    }

    @Override
    // saat app dibuka
    protected void onStart() {
        super.onStart();
        if (mSensorAccelerometer != null) {
            // menambahkan listener accelerometer ke sensormanager apabila ada
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            System.out.println("No Accelerometer Sensor");
        }
        if (mSensorMagnetometer != null) {
            // menambahkan listener magnetometer ke sensormanager apabila ada
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            System.out.println("No Magnetometer Sensor");
        }
    }

    protected void onStop() {
        super.onStop();
        // unregister listener yang diregister ke sensormanager sebelumnya
        mSensorManager.unregisterListener(this);
    }

    @Override
    // ketika sensor berubah
    public void onSensorChanged(SensorEvent sensorEvent) {
        // dapatkan jenis sensor
        int sensorType = sensorEvent.sensor.getType();
        // membandingkan jenis sensor
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                // menduplicate nilai sensorEvent
                mAccelerometerData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnometerData = sensorEvent.values.clone();
                break;
            default:
                return;

        }

        // membuat sebuah array kosong untuk menampung nilai
        float[] rotationMatrix = new float[9];
        // nantinya hasil akan dihitung dengan accelerometer menghitung gravitasi dan mmagnometer menghitung geomagnetic
        // mengubah vektor dari device coordinate system ke world coordinate system
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix, null,
                mAccelerometerData, mMagnometerData);

        // tempat untuk menampung nilai azimuth, pitch dan roll
        float[] orientationValues = new float[3];

        // set nilai array orientationvalues dari hitungan rotation matrix
        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrix, orientationValues);
        }

        float azimuth = orientationValues[0]; // rotasi sumbu z
        float pitch = orientationValues[1]; // rotasi sumbu x
        float roll = orientationValues[2]; // rotasi sumbu y

        // set value
        mTextSensorRoll.setText(getResources().getString(R.string.value_format, roll));
        mTextSensorPitch.setText(getResources().getString(R.string.value_format, pitch));
        mTextSensorAzimuth.setText(getResources().getString(R.string.value_format, azimuth));

        // apabila kurang dari drift value
        if (Math.abs(pitch) < VALUE_DRIFT) {
            pitch = 0;
        }
        if (Math.abs(roll) < VALUE_DRIFT) {
            roll = 0;
        }

        // set visibility ke 0 ketika hp diam
        mSpotTop.setAlpha(0f);
        mSpotRight.setAlpha(0f);
        mSpotLeft.setAlpha(0f);
        mSpotBottom.setAlpha(0f);

        // ubah value
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