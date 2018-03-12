package com.example.kushal.mc535;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Create these global variables to be accesses between methods within the MainActivity class.
    static boolean pause_flag = false; // Status flag for pausing
    private final Handler handler_obj = new Handler();
    private Runnable runnable_obj;
    public double xMax;


    public int runCount = 0;
    Button run_button_var, stop_button_var, download_button_var, upload_button_var;
    static TextView debugText;
    TextView lst;

    //private DatabaseHelper dbHelper;
    private DBHandler dbHandler= new DBHandler(this);
    private long timeStamp;

    // Grab sensor data from SensorlistenerService class for plotting
    static float X, Y, Z;
    static void set_sensor_vals(float x, float y, float z) {
        X = x;
        Y = y;
        Z = z;
    }

    // Number of data-points to graph
    int num_data_points = 10;
    int min_index = 0;
    int max_index = num_data_points - 1;
    int delay_val = 1000;

    // Counter to display only ten datapoints
    int times_through_run_func;

    // Graphing stuffs
    GraphView graph;
    private LineGraphSeries<DataPoint> lineGraphSeries_x, lineGraphSeries_y, lineGraphSeries_z, lineGraphSeries_1, lineGraphSeries_2;

    public ArrayList<DataPoint> lastPlotted_x = new ArrayList<DataPoint>();
    public ArrayList<DataPoint> lastPlotted_y = new ArrayList<DataPoint>();
    public ArrayList<DataPoint> lastPlotted_z = new ArrayList<DataPoint>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize the view.
        initView();
        graph = (GraphView) findViewById(R.id.graph_id);

        //initial graph random values
        lineGraphSeries_x = new LineGraphSeries<>(initializeData(0));
        lineGraphSeries_y = new LineGraphSeries<>(initializeData(1));
        lineGraphSeries_z = new LineGraphSeries<>(initializeData(2));

        initializeGraph(graph, lineGraphSeries_x, Color.BLUE,  "x", 15);
        initializeGraph(graph, lineGraphSeries_y, Color.GREEN, "y", 15);
        initializeGraph(graph, lineGraphSeries_z, Color.RED, "z", 15);

        // Initialize number of run itterations
        times_through_run_func = 0;

        //initialize the db helper
        //dbHelper = new DatabaseHelper(this);
    }
    private DataPoint[] initializeData( int axis_choice ) {

        DataPoint[] values = new DataPoint[num_data_points];
        for (int x = 0; x < num_data_points; x++)
        {

            values[x] = new DataPoint(x, 0);
            if (axis_choice == 0)      { lastPlotted_x.add( values[x] ); }
            else if (axis_choice == 1) { lastPlotted_y.add( values[x] ); }
            else if (axis_choice == 2) { lastPlotted_z.add( values[x] ); }
        }
        return values;
    }
    //===========================================
    private void initializeGraph(GraphView graph,
                                 LineGraphSeries<DataPoint> lineGraphSeries,
                                 int color, String title, int yBounds) {
        lineGraphSeries.setColor(color);
        graph.addSeries(lineGraphSeries);
        graph.getViewport().setBackgroundColor(Color.BLACK);
        graph.setTitle(title);
        graph.setTitleColor(Color.BLUE);
        graph.setTitleTextSize(50);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(min_index);
        graph.getViewport().setMaxX(max_index);
        graph.getViewport().setMinY(-yBounds);
        graph.getViewport().setMaxY(yBounds);
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
    @SuppressLint("NewApi")
    @Override
    public void onClick(View view) {
        // Use switch statement to determine which button was clicked
        switch (view.getId()) {
            case R.id.run_button:

                pause_flag = false;

                // When run button is pressed reset the counter
                times_through_run_func = 0;


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
                    //TODO
                    /*A validation for checking that name starts with alphabet should be made*/
                    String tablename = patient_name + "_" + patient_id + "_" + patient_age + "_" + sex;
                    //TODO
                    /*use StringBuilder instead*/
                    //TODO
                    /*same method called in sensor class*/
                    dbHandler.createPatientTable(tablename);
                    SensorlistnerService.setDbName(tablename);
                    //debug to maker sure entries exist in database
                    //findPatient(1,2,3,4);//show how this works
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

                // CHANGED THIS:
                pause_flag = false;
                runCount = 0;

                // RESET THE COUNTER
                times_through_run_func = 0;



                /* Optional :
                    Erase Input Boxes for fresh entry
                 */

                //we might not need running. As now we have actual sensordata and not the random data
                running(pause_flag);
                break;
            case R.id.download_button:

                /*
                     Would be better of implemented with asynctask. So we can show download progress.
                 */
                //TODO
                /* use async task instead*/
                Thread downloadThread = new Thread() {
                    public void run() {
                        downLoad(DBHandler.DATABASE_NAME);
                    }
                };
                if (!downloadThread.isAlive())
                    downloadThread.start();
                else {
                    Toast.makeText(this, "Other Download in already progress! Try after few seconds!", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.upload_button:
                String fileName = DBHandler.DATABASE_PATH+DBHandler.DATABASE_NAME;
                File sourceFile = new File(fileName);
                if (!sourceFile.exists()) {
                    Log.d("KUSHAL","error opening file");
                }
                else {
                    Log.d("KUSHAL","Database File size: "+sourceFile.length()+"   (bytes)");
                    Thread uploadThread = new Thread() {
                        public void run() {
                            uploadDataBase();
                        }
                    };
                    if (!uploadThread.isAlive())
                        uploadThread.start();
                    else {
                        Toast.makeText(this, "Other Upload in progress! Try after few seconds!", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    //
    public void addPatient(int timestamp, int x, int y, int z){//(String timestamp, String x, String y, String z) {
        Patient patient = new Patient(timestamp, x, y, z);
        dbHandler.addHandler(patient);
    }

    //debug: check if added to database
    //IMPORTANT: Find patient will check to see if an entry for a given timestamp has already been made.
    //if it hasn't been made, it will create a field.

    // TODO
    /*unnecessary methods and calls*/
    public void findPatient(int timestamp, int x, int y, int z) {
        Patient patient = dbHandler.findHandler(timestamp);
        if (patient != null) {
            //print debug to app
            Toast.makeText(this, String.valueOf(patient.getTimestamp()) + " " + patient.getXValues() + System.getProperty("line.separator"), Toast.LENGTH_LONG).show();
        }
        else {
            //if the patient isn't in the database, add a patient to the database
            addPatient(timestamp,x,y,z);
        }
    }


    public void running(boolean pause_flag) {
        // Check to see if pause_flag has been set, if not, then draw graph
        if (pause_flag) {
            // Clear graph when paused
            lineGraphSeries_x.resetData(new DataPoint[0]);
            handler_obj.removeCallbacks(runnable_obj);
        } else {

            Toast.makeText(this, "DEBUG - JOSH", Toast.LENGTH_LONG).show();
            for (int i = 0; i < 10; i++) {
                max_index++;
                lineGraphSeries_x.appendData(new DataPoint(max_index, X), true, 50);
                lineGraphSeries_y.appendData(new DataPoint(max_index, Y), true, 50);
                lineGraphSeries_z.appendData(new DataPoint(max_index, Z), true, 50);
                handler_obj.postDelayed(runnable_obj, 100);
            }
        }
    }

    public void initView() {
        setContentView(R.layout.activity_main);
        // Assign buttons to variables
        debugText = findViewById(R.id.debug);
        run_button_var = findViewById(R.id.run_button);
        stop_button_var = findViewById(R.id.stop_button);
        download_button_var = findViewById(R.id.download_button);
        upload_button_var = findViewById(R.id.upload_button);

        // Set on-click listeners to buttons (described here: https://www.youtube.com/watch?v=GtxVILjLcw8K)
        run_button_var.setOnClickListener(this);
        stop_button_var.setOnClickListener(this);
        download_button_var.setOnClickListener(this);
        upload_button_var.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        if(pause_flag==false)
            handler_obj.postDelayed(runnable_obj, 300);
        //if(accelerometer!=null)
        //    sensorManager.registerListener(this,accelerometer,sensor_sampling_rate);
        super.onResume();
    }

    @Override
    public void onPause() {
        handler_obj.removeCallbacks(runnable_obj);
        //sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //kill the service when app closes
        stopService(new Intent(this,SensorlistnerService.class));
        dbHandler.close();
        super.onDestroy();
    }

    private void uploadDataBase()
    {
                /*
                    The uploading file code snippet is borrowed from following reference.
                    https://androidexample.com/Upload_File_To_Server_-_Android_Example/index.php
                 */

        String fileName = DBHandler.DATABASE_PATH+DBHandler.DATABASE_NAME;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        File sourceFile = new File(fileName);

        if (!sourceFile.exists()) {

            Log.d("KUSHAL","error opening file");
        }
        else {
            //upload
            FileInputStream fileInputStream = null;
            URL url = null;
            try {
                fileInputStream = new FileInputStream(sourceFile);
                url = new URL("http://impact.asu.edu/CSE535Spring18Folder/UploadToServer.php");

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file",fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                Log.d("KUSHAL","Bytes read: "+bytesRead);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                int serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("KUSHAL", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                fileInputStream.close();
                dos.flush();
                dos.close();
                conn.disconnect();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void downLoad(String dbName)
    {
        //To-DO::
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        SQLiteDatabase database = null;
        DataPoint[] xVals = new DataPoint[10];
        DataPoint[] yVals = new DataPoint[10];
        DataPoint[] zVals = new DataPoint[10];
        try {
            Log.d("database name",dbName+"");
            URL url = new URL("http://impact.asu.edu/CSE535Spring18Folder/"+dbName);
            connection = (HttpURLConnection) url.openConnection();

            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                Log.d("KUSHAL","Server returned HTTP " + connection.getResponseCode()+ " " + connection.getResponseMessage());
                return;
            }

            int fileLength = connection.getContentLength();
            Log.d("KUSHAL","Download File length:: "+fileLength);

            input = connection.getInputStream();
            output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/CSE535_ASSIGNMENT2_DOWN");
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }

            //To:DO  TASK C
            /*
                    @ashni
                    When download finishes. Fetch the last 10 timestamp values from the downloaded database
                    And pass those readings to Josh's Function so that they could be plotted on the graph.
                    This can be done in asynctask onPostExecute().
             */

            database = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getPath()+"/CSE535_ASSIGNMENT2_DOWN", null, 0);
            Log.d("no exception","no exception");
            String patient_id = ((EditText)findViewById(R.id.idText)).getText().toString();
            String patient_age = ((EditText)findViewById(R.id.ageText)).getText().toString();
            String patient_name = ((EditText)findViewById(R.id.nameText)).getText().toString();
            String sex = "Male";
            int checked = ((RadioGroup)findViewById(R.id.rg1)).getCheckedRadioButtonId();
            if (checked == R.id.female)
                sex = "Female";
            String tableName = patient_name + "_" + patient_id + "_" + patient_age + "_" + sex;
            String query = "Select * from "+ tableName+ " ORDER BY TIMESTAMP DESC limit 10";
            try{
                Cursor cursor = database.rawQuery(query, null);
                int i=0;
                int j=10;
                if (cursor.moveToFirst() ){
                    do{
                        i++;
                        j--;
                        String x = cursor.getString(cursor.getColumnIndex("XValues"));
                        String y = cursor.getString(cursor.getColumnIndex("YValues"));
                        String z = cursor.getString(cursor.getColumnIndex("ZValues"));
                        DataPoint xval = new DataPoint(Double.parseDouble(x),i);
                        DataPoint yval = new DataPoint(Double.parseDouble(y),i);
                        DataPoint zval = new DataPoint(Double.parseDouble(z),i);
                        xVals[j] = xval;
                        yVals[j] =yval;
                        zVals[j] = zval;
                        Log.d("getting x y z"," "+x+" "+y+" "+z);
                    }while (cursor.moveToNext());
                }
                Log.d("xarr",xVals+"");
                Log.d("yarr",yVals+"");
                Log.d("zarr",zVals+"");
                //TODO
            /*use three arrays to plot graphseries*/
            }
            catch(SQLiteException e){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Records for this person does not exist",Toast.LENGTH_SHORT).show();
                    }
                });

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
            if (connection != null)
                connection.disconnect();
        }
    }
}