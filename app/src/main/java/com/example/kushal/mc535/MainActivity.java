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

import android.database.sqlite.SQLiteDatabase;
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


import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Create these global variables to be accesses between methods within the MainActivity class.
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private LineGraphSeries<DataPoint> mSeries1;
    public double xMax;
    public int runCount = 0;

    Button run_button_var, stop_button_var, download_button_var, upload_button_var;
    static TextView debugText;
    TextView lst;
    // Status flag for pausing
    private boolean pause_flag = false;
    public ArrayList<DataPoint> lastPlotted = new ArrayList<DataPoint>();
    public DataPoint[] temp = new DataPoint[30];

    //private DatabaseHelper dbHelper;
    private DBHandler dbHandler= new DBHandler(this);
    private long timeStamp;
    SensorlistnerService SLS = new SensorlistnerService();

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
                    dbHandler.createPatientTable(tablename);
                    SLS.setDbName(tablename);
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
                pause_flag = true;
                runCount = 0;
                /* Optional :
                    Erase Input Boxes for fresh entry
                 */

                //we might not need running. As now we have actual sensordata and not the random data
                running(pause_flag);
                break;
            case R.id.download_button:

                // Later to be moved to ASyncTask.
                Thread downloadThread = new Thread() {
                    public void run() {
                        downLoad(DBHandler.DATABASE_NAME);
                    }
                };
                if (!downloadThread.isAlive())
                    downloadThread.start();
                else {
                    Toast.makeText(this, "Upload in progress! Try after few seconds!", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(this, "Upload in progress! Try after few seconds!", Toast.LENGTH_LONG).show();
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

        try {
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

