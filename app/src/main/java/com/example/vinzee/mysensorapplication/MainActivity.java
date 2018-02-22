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

        //        Float.valueOf(editTextEuro.getText().toString()).floatValue();
//        Intent myIntent = new Intent(this, Main2Activity.class);
//        startActivity(myIntent);
//        Toast.makeText(getApplicationContext(), "Not a valid option!", Toast.LENGTH_LONG).show();
    }

    protected void onStart() { super.onStart(); }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            System.out.println("onSensorChanged : " + x + " : " + y + " : " + z);

            AccelWork accelWork = new AccelWork(x, y, z);
            myHandler.post(accelWork);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
