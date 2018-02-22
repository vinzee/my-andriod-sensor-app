package com.example.vinzee.mysensorapplication;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private EditText xaccelEditText, yaccelEditText, zaccelEditText;
    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;
    public static Handler myHandler = new Handler();

    private class AccelWork implements Runnable {
        private float x,y,z;

        public AccelWork(float _x, float _y, float _z){
            x = _x;
            y = _y;
            z = _z;
        }

        @Override
        public void run() {
            xaccelEditText.setText(String.valueOf(x));
            yaccelEditText.setText(String.valueOf(y));
            zaccelEditText.setText(String.valueOf(z));
            calculateVelocity(x,y,z);
        }

    }

    private void calculateVelocity(float x, float y, float z){
        long lastUpdate = 0;
        float last_x = 0.0f,last_y = 0.0f,last_z = 0.0f;
//        float lastSpeed = 0.0f;
        long curTime = System.currentTimeMillis();

        if ((curTime - lastUpdate) > 100) {
            long diffTime = (curTime - lastUpdate);
            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
            float distance = (speed * diffTime) / 1000;  // milliseconds to seconds

            Log.d("MySensorApp","Speed : " + speed + " , Distance : " + distance);

            last_x = x;
            last_y = y;
            last_z = z;
//            lastSpeed = speed;
            lastUpdate = curTime;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        xaccelEditText = findViewById(R.id.xaccelEditText);
        yaccelEditText = findViewById(R.id.yaccelEditText);
        zaccelEditText = findViewById(R.id.zaccelEditText);
    }

    protected void onStart() { super.onStart(); }

    // It's good practice to unregister the sensor when the application hibernates and register the sensor again when the application resumes.
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
//            AccelWork accelWork = new AccelWork(x, y, z);

            Log.d("MySensorApp", "Raw accelerometer values : " + x + " : " + y + " : " + z);

            float gravity[] = new float[3];
            float linear_acceleration[] = new float[3];

            final float alpha = 0.8f;

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = sensorEvent.values[0] - gravity[0];
            linear_acceleration[1] = sensorEvent.values[1] - gravity[1];
            linear_acceleration[2] = sensorEvent.values[2] - gravity[2];

            Log.d("MySensorApp", "Linear acceleration : " + linear_acceleration[0] + " : " + linear_acceleration[1] + " : " + linear_acceleration[2]);

            AccelWork accelWork = new AccelWork(linear_acceleration[0], linear_acceleration[1], linear_acceleration[2]);
            myHandler.post(accelWork);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
