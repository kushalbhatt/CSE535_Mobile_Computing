package com.example.kushal.mc535;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Create these global variables to be accesses between methods within the MainActivity class.
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private LineGraphSeries<DataPoint> mSeries1;
    public double xMax;
    public int runCount = 0;

    Button run_button_var, stop_button_var;
    static TextView debugText;
    TextView lst;
    // Status flag for pausing
    private boolean pause_flag = false;
    public ArrayList<DataPoint> lastPlotted = new ArrayList<DataPoint>();
    public DataPoint[] temp = new DataPoint[30];

    //private DatabaseHelper dbHelper;
    DBHandler dbHandler = new DBHandler(this, null, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize the view.
        initView();
        com.jjoe64.graphview.GraphView graph = (com.jjoe64.graphview.GraphView) findViewById(R.id.graph);
        //initial graph random values
        mSeries1 = new LineGraphSeries<>(generateData());
        mSeries1.setColor(Color.GREEN);
        graph.addSeries(mSeries1);

        //graphview UI setup
        graph.getViewport().setBackgroundColor(Color.BLACK);
        graph.setTitle("ECG");
        graph.setTitleColor(Color.BLUE);
        graph.setTitleTextSize(50);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxX(30);
        graph.getViewport().setMaxY(100);

        //initialize the db helper
        //dbHelper = new DatabaseHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    //This button listener class will initiate actions when buttons are pressed.
    @Override
    public void onClick(View view) {
        // Use switch statement to determine which button was clicked
        switch (view.getId()) {
            case R.id.run_button:
                pause_flag = false;
                runCount++;
                /*
                    fetch patient data
                */
                String patient_id = ((EditText)findViewById(R.id.idText)).getText().toString();
                String patient_age = ((EditText)findViewById(R.id.ageText)).getText().toString();
                String patient_name = ((EditText)findViewById(R.id.nameText)).getText().toString();
                String sex = "Male";
                int checked = ((RadioGroup)findViewById(R.id.rg1)).getCheckedRadioButtonId();
                if (checked == R.id.female)
                    sex = "Female";

                /*
                    Create table if not already for this patient so that we can dump data into it
                 */
                //input sanity check to avoid errors
                if(!patient_age.isEmpty() && !patient_name.isEmpty() && !patient_id.isEmpty()) {
                    String tablename = patient_name + "_" + patient_id + "_" + patient_age + "_" + sex;
                    //dbHelper.createPatientTable(tablename);
                    findPatient(1,2,3,4);
                    debugText.setText("Starting Service");
                    //Start the senorlistner to sample accelerometer data
                    Intent sensorService = new Intent(MainActivity.this,SensorlistnerService.class);
                    /*Use Bundle if any data needs to be passed along with this intent*/
                    sensorService.putExtra("table_name",tablename);
                    startService(sensorService);

                    //we might not need running. As now we have actual sensordata and not the random data
                    running(pause_flag);
                }
                else {
                    Toast.makeText(this, "Please input all the patient info. Try again!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.stop_button:
                pause_flag = true;
                runCount = 0;
                /* Optional :
                    Erase Input Boxes for fresh entry
                 */

                //we might not need running. As now we have actual sensordata and not the random data
                running(pause_flag);
                break;
        }
    }
    public void loadPatient(View view) {
        DBHandler dbHandler = new DBHandler(this, null, null, 1);
        lst.setText(dbHandler.loadHandler());
    }
    //
    public void addPatient(int timestamp, int x, int y, int z){//(String timestamp, String x, String y, String z) {
        Patient patient = new Patient(timestamp, x, y, z);
        dbHandler.addHandler(patient);
    }

    //IMPORTANT: Find patient will check to see if an entry for a given timestamp has already been made.
    //if it hasn't been made, it will create a field.
    public void findPatient(int timestamp, int x, int y, int z) {
        Patient patient = dbHandler.findHandler(timestamp);
        if (patient != null) {
            Toast.makeText(this, String.valueOf(patient.getTimestamp()) + " " + patient.getXValues() + System.getProperty("line.separator"), Toast.LENGTH_LONG).show();
        }
        else {
            addPatient(timestamp,x,y,z);
        }
    }
    public void running(boolean pause_flag) {
        // Check to see if pause_flag has been set, if not, then draw graph
        if (pause_flag) {
            mSeries1.resetData(new DataPoint[0]);
            mHandler.removeCallbacks(mTimer1);
        } else {
            if (runCount == 1) {
                mTimer1 = new Runnable() {
                    @Override
                    public void run() {
                        int j = 29;
                        //copy previously plotted values to temp array
                        for(int i=(int)xMax;i>(int)xMax-30;i--){
                            temp[j]=lastPlotted.get(i);
                            j--;
                        }

                        mSeries1.resetData(temp);
                        double y = mRand.nextDouble() * 200 + 0.3;
                        DataPoint newData = new DataPoint(++xMax, y);
                        //aapend newly created point
                        mSeries1.appendData(newData, true, 50);
                        lastPlotted.add(newData);
                        mHandler.postDelayed(this, 300);
                    }
                };
                //call this thread at spcified interval so new values will be added continuously
                mHandler.postDelayed(mTimer1, 300);
            }
        }
    }
    public void initView() {
        setContentView(R.layout.activity_main);
        // Assign buttons to variables
        debugText = findViewById(R.id.debug);
        run_button_var = findViewById(R.id.run_button);
        stop_button_var = findViewById(R.id.stop_button);

        // Set on-click listeners to buttons (described here: https://www.youtube.com/watch?v=GtxVILjLcw8K)
        run_button_var.setOnClickListener(this);
        stop_button_var.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        if(pause_flag==false)
            mHandler.postDelayed(mTimer1, 300);
        //if(accelerometer!=null)
        //    sensorManager.registerListener(this,accelerometer,sensor_sampling_rate);
        super.onResume();
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);
        //sensorManager.unregisterListener(this);
        super.onPause();
    }

    private DataPoint[] generateData() {
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i = 0; i < count; i++) {
            double x = i;
            double y = mRand.nextDouble() * 200 + 0.3;
            xMax = x;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
            lastPlotted.add(v);
        }
        return values;
    }

    Random mRand = new Random();

    @Override
    protected void onDestroy() {
        //kill the service when app closes
        stopService(new Intent(this,SensorlistnerService.class));
        super.onDestroy();
    }
}
