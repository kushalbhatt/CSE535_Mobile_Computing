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
    static Button train_button_var, test_button_var;
    //This is the number of training data sets taken
    int trainWalkCount, trainRunCount, trainJumpCount;
    //train button can't be hit unless train_enabled is true
    static boolean train_enabled=true;
    //The progress bar associated with the training data
    ProgressBar progressBar_walk_var, progressBar_run_var, progressBar_jump_var;
    //Error text box that appears when train is pressed when other processes are running
    TextView wait_for_not_clicked;
    //Instantiate an instance of the database
    DBHandler dbHandler = new DBHandler(this);
    //table name for the database
    String tablename = "TEST";
    public static float[][] floatArray;

    public native String stringFromJNI();
    public native float[] svm(float[] X, float[] Y);
    public native float[] test(float[] arr1, float[] arr2);
    public native float[] SVM_train(float[] X, float[] Y); // MxN data matrix and Mx1 target vector
    public native float[] SVM_predict(float[] x); // pass in single feature vector


    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }
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
                        //the string id will be the concatenation of the first letter of the activity and the trainCount
                        String activity_id = "";
                        //if a radio button has been selected, the following will run
                        if (!(empty_string)) {
                            train_enabled=false;
                            enableButton(train_enabled);
                            Log.d("RACHEL","Enable button false");
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
                        //Check if sesnor values works more than one time of radio button being hit
                        Log.d("RACHEL:","START SENSOR");
                        //Start the sensorlistner to sample accelerometer data
                        Intent sensorService = new Intent(MainActivity.this,SensorListener.class);
                        //Use Bundle if any data needs to be passed along with this intent
                        startService(sensorService);
                        //set the sensor listener to take the table name for the db, the activity ID, and the activity
                        SensorListener.setNames(tablename,activity_id,activity);
                        Toast.makeText(getApplicationContext(), activity_id, Toast.LENGTH_LONG).show();
                        //Wait while app collects the data
                        //TODO
                        //This may be useless button
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
                fetchData fd = new fetchData();
                fd.executeTask();

                // JOSH - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                // JOSH - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                // JOSH - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                // JOSH - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                // JOSH - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                // JOSH - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                // JOSH - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -


                //testCpp_interop();
                //testOpenCV_interop();

                break;
        }
    }
    static public void enableButton(boolean train_enable)
    {
        train_enabled= train_enable;
        train_button_var.setClickable(train_enabled);
        train_button_var.setEnabled(train_enable);
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
    //==============================================================================================
    public float[][] one2twoD(float[] arr, int rows, int cols) {
        float[][] mat = new float[rows][cols]; // Data matrix with M rows and N cols
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j){
                mat[i][j] = arr[i * cols + j]; // Count up in row-major fashion
            } // end for over j
        } // end for over i
        return mat;
    }
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public float[] two2oneD(float[][] mat, int rows, int cols) {
        float[] arr = new float[rows * cols]; // Data matrix with M rows and N cols
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j){
                arr[i * cols + j] = mat[i][j]; // Count up in row-major fashion
            } // end for over j
        } // end for over i
        return arr;
    }
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void testCpp_interop() {
        // Test the C-function:
        int n = 4;
        float[] array3 = new float[n];
        float[] array1 = new float[n];
        float[] array2 = new float[n];

        array1[0] = 0;      array1[1] = 1;
        array1[2] = 2;      array1[3] = 3;

        //array1[0] = 0;      array1[1] = 255;    array1[2] = 0;
        //array1[3] = 255;    array1[4] = 0;      array1[5] = 0;
        //array1[6] = 512;    array1[7] = 0;      array1[8] = 0;
        //array1[9] = 512;    array1[10] = 255;   array1[11] = 0;

        for (int i = 0; i < n; i++) {
            array2[i] = array1[i];
            array3[i] = 0;
        }

        //String temp_string = stringFromJNI();
        array3 = test(array1, array2);
        // if x = [0 1; 2 3]
        // then x^2 = [2 3; 6 11]

        int debug1 = 0;
    }
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void testOpenCV_interop() {
        int M = 4, N = 150;
        float[] X_lin = new float[M * N]; // Data matrix with M rows and N cols
        for (int i = 0; i < M * N; ++i)
            X_lin[i] = 0;
        //  z=0
        //     \  x=0   x=1  ...  x=N-1
        //      \----------------------
        // y=0  |_____|_____|...|______|
        // y=1  |_____|_____|...|______|
        //  .   |_____|_____|...|______|
        //  .   |_____|_____|...|______|
        //  .   |_____|_____|...|______|
        // y=M-1|_____|_____|...|______|


        X_lin[0] = 0;      X_lin[1] = 256;    // 0,   1,  ...,149  (+1)
        X_lin[150] = 256;  X_lin[151] = 0;    // 150, 151,...,299  (-1)
        X_lin[300] = 512;  X_lin[301] = 0;    // 300, 301,...,449  (-1)
        X_lin[450] = 512;  X_lin[451] = 256;  // 450, 451,...,599  (-1)
        //
        //  x     \  o
        //         \
        //          \
        //  x        \
        //            \
        //             \
        //              \
        //  x         x  \ (-1)
        //            (+1)\

        // Copy 1D array into 2D array:
        float[][] X_mat = new float[M][N];
        X_mat = one2twoD(X_lin, M, N);

        // Copy 2D array into 1D array:
        float[] X = new float[M * N];
        X = two2oneD(X_mat, M, N);


        // Target vector:
        float[] Y = new float[M];
        Y[0] = 1;
        Y[1] = -1;
        Y[2] = -1;
        Y[3] = -1;

        // Pass data matrix to svm in C++
        float[] arrayTest = new float[M * N];
        arrayTest = svm(X, Y);

        int temp0 = 0;
    }
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void train(float[][] X, float[] Y, int M, int N) {
        // Method train does the following:
        // Step 1: Hand me the 2D data matrix and 1D target vector
        // Step 2: Convert X to a 1D array
        // Step 3: Pass array into the svm function
        // Step 4: Generate model W,b from X,Y
        // Step 5: Store W,b
        //
        // Input args:
        //  X:  M x N data matrix
        //  Y:  M x 1 target vector
        //  M:  Number of training examples
        //  N:  Number of features
        // Output args:
        //  none

        // Copy 2D array into 1D array:
        float[] X_lin = new float[M * N];
        X_lin = two2oneD(X, M, N);

        // Pass into C++
        SVM_train(X_lin, Y);
    }
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public void predict(float[] x, int N) {
        // Function SVM_predict:
        // Input args:
        //  x:    N-dimensional feature vector
        //  N:      Number of dimensions of x
        // Output args:
        //  none

        // Pass into C++
        SVM_predict(x);
    }
    //==============================================================================================
}