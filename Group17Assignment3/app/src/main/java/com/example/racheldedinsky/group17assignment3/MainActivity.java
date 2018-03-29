package com.example.racheldedinsky.group17assignment3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ProgressBar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //Instatiate the button variables
    Button train_button_var, test_button_var;
    //This is the number of training data sets taken
    int trainWalkCount, trainRunCount, trainJumpCount;
    //train button can't be hit unless train_enabled is true
    boolean train_enabled=true;
    //The progress bar associated with the training data
    ProgressBar progressBar_walk_var, progressBar_run_var, progressBar_jump_var;
    //Error text box that appears when train is pressed when other processes are running
    TextView wait_for_not_clicked;
    //Instantiate an instance of the database
    DBHandler dbHandler = new DBHandler(this);
    //table name for the database
    String tablename = "TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialize variables
        wait_for_not_clicked = (TextView)findViewById(R.id.waitText);
        wait_for_not_clicked.setText("");
        trainWalkCount=0;
        trainJumpCount=0;
        trainRunCount=0;
        //Initialize the app view
        initView();
    }
    @Override
    protected void onDestroy() {
        //kill the service when app closes
        stopService(new Intent(this,SensorListener.class));
        dbHandler.close();
        super.onDestroy();
    }
    public void onClick(View view)
    {
        //switch statement based on which button
        switch (view.getId())
        {
            case R.id.train_button:
                //if the train button is enabled based on if data is being collected
                if(train_enabled) {
                    //find which radio button is activated
                    int checked = ((RadioGroup) findViewById(R.id.rg1)).getCheckedRadioButtonId();
                    View radioButton = findViewById(checked);
                    int idx = ((RadioGroup) findViewById(R.id.rg1)).indexOfChild(radioButton);
                    String activity="";

                    boolean empty_string=false;
                    //based on index determine which button is being pressed
                    if (idx == 0) {
                        activity = "Walk";
                    } else if (idx == 1) {
                        activity = "Run";
                    } else if(idx==2) {
                        activity = "Jump";
                    }
                    else
                    {
                        empty_string=true;
                    }
                    try {
                        //train_enabled will be disabled while the data is being taken
                        train_enabled=false;
                        train_button_var.setEnabled(train_enabled);
                        //the string id will be the concatenation of the first letter of the activity and the trainCount
                        String activity_id = "";
                        //if a radio button has been selected, the following will run
                        if (!(empty_string)) {
                            if (activity == "Walk" && trainWalkCount<=20) {
                                trainWalkCount++;
                                progressBar_walk_var.setProgress(0);
                                progressBar_walk_var.setProgress(trainWalkCount);
                                activity_id = "w"+trainWalkCount;
                            } else if (activity == "Run" && trainRunCount<=20) {
                                trainRunCount++;
                                progressBar_run_var.setProgress(0);
                                progressBar_run_var.setProgress(trainRunCount);
                                activity_id = "r"+trainRunCount;
                            } else if (activity == "Jump" && trainJumpCount<=20){
                                trainJumpCount++;
                                progressBar_jump_var.setProgress(0);
                                progressBar_jump_var.setProgress(trainJumpCount);
                                activity_id = "j"+trainJumpCount;
                            }
                            else//catch all statement, should never run
                            {
                                Toast.makeText(getApplicationContext(), "Invalid data.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        //If a radio button was never selected, the else will run
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Invalid data. Please enter the details correctly.", Toast.LENGTH_SHORT).show();
                        }
                        //TODO
                        //Check if sesnor values works more than one time of radio button being hit
                        Log.d("SENSOR:","START SENSOR");
                        //Start the sensorlistner to sample accelerometer data
                        Intent sensorService = new Intent(MainActivity.this,SensorListener.class);
                        //Use Bundle if any data needs to be passed along with this intent
                        startService(sensorService);
                        //set the sensor listener to take the table name for the db, the activity ID, and the activity
                        SensorListener.setNames(tablename,activity_id,activity);
                        Toast.makeText(getApplicationContext(), activity_id, Toast.LENGTH_LONG).show();
                        //Wait while app collects the data
                        //TODO
                        //This makes the app buggy for some reason?
                        TimeUnit.SECONDS.sleep(5);
                        Log.d("SENSOR:","STOP Sensor");
                        stopService(new Intent(this,SensorListener.class));
                        train_enabled=true;
                        train_button_var.setEnabled(train_enabled);
                        wait_for_not_clicked.setText("");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Invalid data! Please enter the details correctly.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    wait_for_not_clicked.setText("Please wait while collecting data");
                }
                break;
            case R.id.test_button:
                break;
        }
    }
    public void initView() {

        // Assign buttons to variables
        progressBar_walk_var = (ProgressBar) findViewById(R.id.progressBarWalk);
        progressBar_walk_var.setProgress(trainWalkCount);
        progressBar_walk_var.setMax(20);
        progressBar_run_var = (ProgressBar) findViewById(R.id.progressBarRun);
        progressBar_run_var.setProgress(trainRunCount);
        progressBar_run_var.setMax(20);
        progressBar_jump_var = (ProgressBar) findViewById(R.id.progressBarJump);
        progressBar_jump_var.setProgress(trainJumpCount);
        progressBar_jump_var.setMax(20);

        test_button_var = findViewById(R.id.test_button);
        train_button_var= findViewById(R.id.train_button);

        // Set on-click listeners to buttons (described here: https://www.youtube.com/watch?v=GtxVILjLcw8K)
        test_button_var.setOnClickListener(this);
        train_button_var.setOnClickListener(this);

    }
}
