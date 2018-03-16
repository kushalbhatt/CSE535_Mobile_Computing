package com.example.kushal.mc535;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by kushal on 2/19/2018.
 */

public class SensorlistnerService extends Service implements SensorEventListener{

    //variables to set up the database
    private DBHandler dbHandler= new DBHandler(this);
    static String dbName="ID_AGE_NAME_SEX";
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private int sensor_sampling_rate = 1000000;  // 1 sec
    private String current_patient = "";
    public static String LOG_TAG = "SensorListenerService";
    private long timestamp;

    @Override
    public void onCreate() {
        //get sensor status and register for updates
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d(LOG_TAG,"SerivceOncreate()");
        if(accelerometer==null)
        {
            //oops
            Toast.makeText(this,"Accelerometer is not available!",Toast.LENGTH_LONG);
            // AlertDialog can also be shown -- Optional
        }
        sensorManager.registerListener(this,accelerometer,sensor_sampling_rate);
        //dbHelper = new DatabaseHelper(this);
        Toast.makeText(this,"Service Started",Toast.LENGTH_LONG);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG,"onStartCommand()");
        current_patient = intent.getStringExtra("table_name");
        Toast.makeText(this,"Service Started"+ current_patient,Toast.LENGTH_LONG);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        if(!MainActivity.pause_flag)
        {
            // Pass sensor data to MainActivity class for plotting
            MainActivity.set_sensor_vals(x,y,z);
            //Log.d("x y z",x +" "+ y+" " + z);
            //    Add this readings along with timestamp to the database table
            //    But only if graph is being drawn (Run was pressed.)
            //    Name of database is local variable = current_patient
            sendToDatabase(x, y, z);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Most probably not needed
    }


    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        Log.d(LOG_TAG,"Service Stopped!");
        super.onDestroy();
    }
    //This function is called in the main activity after the user information has been entered in order
    //to set up the database with the correct name
    public static void setDbName(String name)
    {
        dbName = name;
    }
    //This should be called only after the run button is pressed in the GUI so the correct table
    //name is passed.
    public void sendToDatabase(float x, float y, float z)
    {
        // x, y, and z are values retrieved from the sensor in this class

        //Initialize table using table name
        dbHandler.createPatientTable(dbName);
        //create a timestamp that the x, y, and z values are generated in
        timestamp=System.currentTimeMillis()/1000;
        //Create an object using the current timestamp, x, y, and z
        Patient patient = new Patient(timestamp, x, y, z);
        //Send the patient to the database
        dbHandler.addHandler(patient);
    }
}
