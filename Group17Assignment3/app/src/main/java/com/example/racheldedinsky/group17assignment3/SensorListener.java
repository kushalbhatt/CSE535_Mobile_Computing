package com.example.racheldedinsky.group17assignment3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import org.json.*;

import java.util.ArrayList;

/**
 * Created by RachelDedinsky on 3/28/18.
 */

public class SensorListener  extends Service implements SensorEventListener {

    private DBHandler dbHandler= new DBHandler(this);
    static String dbName="Test";
    static String dbActivityLabel = "";
    static String dbActivityId = "";
    String activityData = "";
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private int sensor_sampling_rate = 100000;  // 10 hz
    private String current_activity = "";
    public static String LOG_TAG = "SensorListener";
    private int counter=0;
    ArrayList<Float[]> al = new ArrayList<Float[]>();

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
       // sensorManager.registerListener(this,accelerometer,sensor_sampling_rate);
        //dbHelper = new DatabaseHelper(this);
        Toast.makeText(this,"Service Started",Toast.LENGTH_LONG);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("RACHEL","onStartCommand() called");
        current_activity = intent.getStringExtra("table_name");
        Toast.makeText(this,"Service Started"+ current_activity,Toast.LENGTH_LONG);
        counter=0;
        al.clear();
        startListner();
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
        //TODO
        //store type to send to database
        Float array1[] = {x, y, z};
        al.add(array1);
        //Log.d("x y z: ", counter+" "+String.valueOf(al.get(counter)[0])+ " "+String.valueOf(al.get(counter)[1]) + " " + String.valueOf(al.get(counter)[1]));
        counter++;
        if(counter>=50)
        {
            stopListener();
            trainActivity.enableButton(true);
            sendToDatabase(dbActivityId,al,dbActivityLabel);
            al.clear();
            dbActivityId="";
            activityData="";
            dbActivityLabel="";
        }
        //    Read activity data from db by converting string to json and json to arraylist of float ints
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
    public static void setNames(String name, String id, String label)
    {
        dbName = name;
        dbActivityId = id;
        dbActivityLabel = label;
    }
    //This should be called only after the run button is pressed in the GUI so the correct table
    //name is passed.
    public void sendToDatabase(String id, ArrayList<Float[]> data, String label)
    {
        dbHandler.createTable(dbName);
        dbHandler.addHandler(id,data,label);
    }

    public void startListner()
    {
        sensorManager.registerListener(this,accelerometer,sensor_sampling_rate);
    }
    public void stopListener()
    {
        sensorManager.unregisterListener(this);
    }
}

