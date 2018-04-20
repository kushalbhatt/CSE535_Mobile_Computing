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

import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;

import java.util.concurrent.TimeUnit;



public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // Note: Names of buttons in the GUI is now:
    // Record  (was called train)
    // Train   (was called test)
    // Predict (did not exist)

    // Training Dataset:
    static int M = 60; // Rows - examples
    static int N = 150;  // Cols - features
    public static Mat Y;// = Mat.zeros(M, 1, CvType.CV_32SC1);   // Integer {-1, 0, +1}
    public static Mat X;// = Mat.zeros(M, N, CvType.CV_32FC1); // Float
    public static float[][] dataset_2Darr;

    private static final String TAG = "mytag";
    static {
        if(OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV successfully loaded");
        }
        else {
            Log.d(TAG, "OpenCV not loaded");
        }
    }


    //Instatiate the button variables
    static Button train_button_var, test_button_var, predict_button_var;

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
    
    
    

    //public native String stringFromJNI();
    //public native float[] test(float[] arr1, float[] arr2);
    //public static native float[] svm(float[] X, float[] Y);        // train model
    //public native float[] svm_predict(float[] x);



    // Instantiate SVM object globally
    static SVM classifier = SVM.create();


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

        // SVM Stuff:
        classifier.setKernel(SVM.CHI2);
        classifier.setType(SVM.C_SVC);
        classifier.setGamma(0.5);
        classifier.setNu(0.5);
        classifier.setC(3);
        //classifier.setTermCriteria(criteria);

        // Dataset stuff:
        //Y = new Mat(new Size(1,M),CvType.CV_32SC1); // Integer {-1, 0, +1}
        Y = Mat.zeros(M, 1, CvType.CV_32SC1);
        int Y_rows = Y.rows(); // 60
        int Y_cols = Y.cols(); // 1

        //X = new Mat(new Size(N,M),CvType.CV_32FC1); // Float
        X = Mat.zeros(M, N, CvType.CV_32FC1);
        int X_rows = X.rows(); // 60
        int X_cols = X.cols(); // 150

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
                Toast.makeText(getApplicationContext(), "Training model. Hold on!", Toast.LENGTH_SHORT).show();
                fetchData fd = new fetchData();
                fd.executeTask();


                int temp0 = 0;

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

            case R.id.predict_button:


                Toast.makeText(getApplicationContext(), "Predict Pressed", Toast.LENGTH_SHORT).show();


                // PROTOTYPE of feature vector:
                Mat x = Mat.zeros(1, N, CvType.CV_32FC1);
                int x_rows = x.rows(); // 1
                int x_cols = x.cols(); // 150
                for (int i = 0; i < 150; i++) {
                    x.put(0, i, 0.2f);
                }



                // Train the model using X and Y
                classifier.train(X, Ml.ROW_SAMPLE, Y);


                Mat getClassWeights = classifier.getClassWeights();
                TermCriteria getTermCriteria = classifier.getTermCriteria();
                boolean isTrained = classifier.isTrained();

                Mat outMat = new Mat();
                float response = classifier.predict(x, outMat, 0);



                x = new Mat(new Size(1,150),CvType.CV_32SC1); // Integer {-1, 0, +1}
                int Y_rows = Y.rows(); // 60
                int Y_cols = Y.cols(); // 1


                break; // end predict_button
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
        train_button_var = findViewById(R.id.train_button);
        predict_button_var = findViewById(R.id.predict_button);


        // Set on-click listeners to buttons (described here: https://www.youtube.com/watch?v=GtxVILjLcw8K)
        test_button_var.setOnClickListener(this);
        train_button_var.setOnClickListener(this);
        predict_button_var.setOnClickListener(this);

    }
    //==============================================================================================
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public static float[] two2oneD(float[][] mat, int rows, int cols) {
        float[] arr = new float[rows * cols]; // Data matrix with M rows and N cols
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j){
                arr[i * cols + j] = mat[i][j]; // Count up in row-major fashion
            } // end for over j
        } // end for over i
        return arr;
    }
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public static void train() {
        // Method train does the following:
        // Step 1: Hand me the 2D data matrix and 1D target vector
        // Step 2: Convert X to a 1D array
        // Step 3: Pass array into the svm function
        // Step 4: Generate model W,b from X,Y
        // Step 5: Store W,b
        //
        // Input args:
        /// dataset_2Darr: 60 x 151 matrix. Right most column contains target vector.
        //      Extract: X:  M x N data matrix
        //               Y:  M x 1 target vector
        //                   M:  Number of training examples
        //                   N:  Number of features
        // Output args:
        //  none
        Log.d("inside testOpenCV",dataset_2Darr[0][0]+"");

        /// Extract right most column from Ashni's Matrix
        ///     Place data matrix in X and target vector in Y
        float temp;
        double[] temp2;
        for (int i=0; i < M; i++) {
            for(int j=0; j < N; j++) {
                X.put(i, j, dataset_2Darr[i][j]); // Copy 2D array into mat object
            }
        }


        int debug2 = 0;;

        // Itterate down rows of right-most column
        for (int i = 0; i < M; ++i)
            Y.put(i,0, (int)dataset_2Darr[i][150]); // Copy 2D array into mat object



        // Train the model using X and Y
        classifier.train(X, Ml.ROW_SAMPLE, Y);


    }
    //==============================================================================================
    public float accuracy() {

        /*
        /// Extract right most column from Ashni's Matrix
        ///     Place data matrix in X and target vector in Y
        float temp;
        double[] temp2;
        for (int i=0; i < M; i++) {
            for(int j=0; j < N; j++) {
                temp = dataset_2Darr[i][j];
                temp2 = X.get(i, j);
                int debug = i * j;
                //X.put(i, j, dataset_2Darr[i][j]); // Copy 2D array into mat object
            }
        }
        */

        // Feature vector (1xN)
        Mat x = Mat.zeros(1, N, CvType.CV_32FC1);
        float response = classifier.predict(x);

        // Try with X
        // Try with x


        //Mat results = new Mat();
        //classifier.predict(x, results, 0);


        //int count = 0;

        // Iterate over the examples
        for (int i = 0; i < M; ++i) {

            // Extract row of X

            for (int j = 0; j < N; ++j) {
                int tempDebug = 0;
                //x.put(0, j, X.get(i, j)); // Individual feature vector for one example
            }



            // Problem is right here
            //float p = classifier.predict(x);

            // Questions:
            //  -What form is predict expecting?



            //cv::Size size(N, 1);
            //Mat mat1 = Mat::zeros(size, CV_32FC1);
            //mat1.at<float>(0, 1) = 256;



            //double[] labels = Y.get(i,0);

            // NOT SURE OF THIS
            //double t = labels[0];

            // There is a ternary split in the real line for our classification:
            // (-infinity)---------(-1)---------(0)---------(+1)---------(+infinity)
            //                   Class 1  |   Class 2   |  Class 3
            // Activity 1: -infinity < prediction < -0.5
            // Activity 2: -0.5 < prediction < +0.5
            // Activity 3: +0.5 < prediction < infinity

/*
            if ( ( p < -0.5  &&  t < -0.5 ) ||
               ( (-0.5 <= p  &&  p < 0.5) && (-0.5 <= t  &&  t < 0.5) ) ||
               ( ( 0.5 <= p  &&  0.5 <= t ) ) ) {
                count++;
            }
*/

        }
        return 0.0f;//(float)count / (float)M;
    }
}