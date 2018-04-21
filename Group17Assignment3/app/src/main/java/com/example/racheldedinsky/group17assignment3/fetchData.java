package com.example.racheldedinsky.group17assignment3;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.ml.Ml;

import java.io.File;
import java.io.FileWriter;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by asmodi on 4/12/18.
 */

public  class fetchData extends AppCompatActivity {

    double accuracy =0 ;
    Context c = null;
    public static float myValues[][];
    fetchData(Context con){
        c = con;
    }

    public void executeTask(){
        Log.d(" fetch data","Hi");
        new CreateCSV().execute();
        new Createdataset_2Darr().execute();
    }

    class CreateCSV extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

            try {

                SQLiteDatabase database = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getPath()+"/CSE535_ASSIGNMENT3/activityDB.db", null, 0);
                Log.d("no exception","no exception");
                //String query = "Select * from TEST";
                //File db = getDatabasePath(Environment.getExternalStorageDirectory().getPath()+"/CSE535_ASSIGNMENT3/activityDB.db");
                // File db = new File(Environment.getExternalStorageDirectory().getPath()+"/CSE535_ASSIGNMENT3/activityDB.db");
                File exportDir = new File(Environment.getExternalStorageDirectory(), "");
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }
                File file = new File(exportDir, "excerDB.csv");

                try {
                    file.createNewFile();
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                    Cursor curCSV=database.rawQuery("select * from TEST" ,null);
                    csvWrite.writeNext(curCSV.getColumnNames());
                    Log.d("getting columns",""+curCSV.getColumnNames());
                    while(curCSV.moveToNext()){
                        String arrStr[] = new String[152];
                        arrStr[0] = curCSV.getString(0);
                        for(int i=1;i<151;i++){
                            arrStr[i] = curCSV.getString(i);
                        }
                        arrStr[arrStr.length-1] = curCSV.getString(arrStr.length-1);
                        //String arrStr[] ={curCSV.getString(0),curCSV.getString(1)};
                        Log.d("array string",""+arrStr);
                        /*curCSV.getString(2),curCSV.getString(3),curCSV.getString(4)*/
                        csvWrite.writeNext(arrStr);
                    }

                    csvWrite.close();
                    curCSV.close();
                    return true;

                }
                catch(SQLiteException e){
                    e.printStackTrace();
                    Log.d("ashni, exception","ashni exception here");
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("ashni, exception","ashni exception here");
                return false;
            }

        }

//                @Override
//                protected void onPostExecute(final Boolean success) {
//                    if (success) {
//                        Toast.makeText(getApplicationContext(), "Test Accuracy = "+, Toast.LENGTH_SHORT).show();
//                    }  else {
//                        Toast.makeText(getApplicationContext(), "Export failed", Toast.LENGTH_SHORT).show();
//                    }
//                }

    }

    class Createdataset_2Darr extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

            try {

                SQLiteDatabase database = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getPath() + "/CSE535_ASSIGNMENT3/activityDB.db", null, 0);
                Log.d("no exception", "no exception");
                String query = "Select * from TEST";
                try{
                    Cursor cursor = database.rawQuery(query,null);
                    Log.d("KUSHAL","READ------------------  "+cursor.getCount());
                    myValues = new float[cursor.getCount()][152];
                    if(cursor.moveToFirst()){
                        for(int i=1;i<cursor.getCount();i++){
                            for(int j=1;j<151;j++){
                                Float val = cursor.getFloat(j);
                                myValues[i-1][j-1]=val;
                               // Log.d("val",""+val);
                            }
                            String activity = cursor.getString(151);
                           // Log.d("KUSHAL","activity = "+activity);
                            if(activity.equals("Walk")){
                                myValues[i-1][151] = 0;
                            }
                            else if(activity.equals("Run")){
                                myValues[i-1][151] =1;
                            }
                            else{
                                myValues[i-1][151]  = -1;
                            }
                            //Log.d("label",myValues[i-1][151]+"");
                            //Log.d("new row","---------");
                            cursor.moveToNext();
                        }
                    }
                    MainActivity.dataset_2Darr = myValues;
                    Log.d("main actvity array",MainActivity.dataset_2Darr[0][0]+"");
                    Log.d("float array",myValues[0][0]+"");

                    // JOSH - - - - - - - - - - - - Inside the fetchData Async Task
                    // JOSH - - - - - - - - - - - - Inside the fetchData Async Task
                    // JOSH - - - - - - - - - - - - Inside the fetchData Async Task
                    // JOSH - - - - - - - - - - - - Inside the fetchData Async Task
                    // JOSH - - - - - - - - - - - - Inside the fetchData Async Task

                    // Training is done here.
                    // First copy the 2D float array into data matrix X and target vector Y
                    // Then construct model via train(X,Y)

                    // Copy train data into X and Y
                    for (int i=0; i < 60; i++) {
                        for(int j=0; j < 150; j++) {
                            MainActivity.X.put(i, j, MainActivity.dataset_2Darr[i][j]); // Copy 2D array into mat object
                        }
                    }
                    // Itterate down rows of right-most column
                    double[] temp00;
                    float y_debug = 0;
                    for (int i = 0; i < 60; ++i){
                        MainActivity.Y.put(i,0, (int)MainActivity.dataset_2Darr[i][151]); // Labels
                        y_debug = (MainActivity.dataset_2Darr[i][151]);
                        temp00 = MainActivity.Y.get(i, 0);

                    }



                    // Train the model using X and Y
                    boolean isTrained = MainActivity.classifier.isTrained();

                    isTrained = MainActivity.classifier.isTrained();
                    //train only if it is not already trained
                    Log.d("KUSHAL","Is model trained?: "+isTrained);
                    if(!isTrained)
                        MainActivity.classifier.train(MainActivity.X, Ml.ROW_SAMPLE, MainActivity.Y);
                    else
                        Log.d("KUSHAL","Skipped Trained....as already trained! "+isTrained);



                    int getType = MainActivity.classifier.getType();
                    Mat getClassWeights = MainActivity.classifier.getClassWeights();
                    TermCriteria getTermCriteria = MainActivity.classifier.getTermCriteria();

                    // ----------------------------
                    //  -Compute training accuracy:
                    // ----------------------------

                    // Count successful classifications
                    int count = 0;
                    //Testing data points and labels -- 6 vectors
                    Mat test_x = Mat.zeros(6, 150, CvType.CV_32FC1);
                    Mat test_y = Mat.zeros(6, 1, CvType.CV_32FC1);;
                    for (int i=60; i < 66; i++) {
                        for(int j=0; j < 150; j++) {
                            test_x.put(i-60, j, MainActivity.dataset_2Darr[i][j]); // Copy 2D array into mat object
                            test_y.put(i-60,0,MainActivity.dataset_2Darr[i][151]);
                        }
                    }

                    // Iterate over the examples
                    //for (int i = 0; i < 60; ++i) {
                    for (int i = 0; i < 6; i++) {
                        /*
                        // Extract row of X
                        Mat x = Mat.zeros(1, 150, CvType.CV_32FC1);
                        for (int j = 0; j < 150; ++j) {
                            x.put(0, j, MainActivity.X.get(i, j)); // Individual feature vector for ith example
                        }
                        */
                        // Pass in training example and compute prediction
                        Mat outMat = new Mat();
                        float _res = MainActivity.classifier.predict(test_x, outMat, 0);

                        double predictions[] = outMat.get(0,0);
                        double p = predictions[0];
                        //double[] tempArr = new float[outMat.rows() * outMat.cols()];
                        //int tempRows = outMat.rows();
                        //int tempCols = outMat.cols();
                        //Log.d("KUSHAL","outmatrix size == "+tempRows+"x"+tempCols+"  pred"+pred[0]);
                        /*for (int ii = 0; ii < outMat.rows(); ii++){
                            for (int jj = 0; jj < outMat.cols(); jj++)
                                double[ii * outMat.cols() + jj] = outMat.get(ii,jj);
                        }
                           */

                        // Get actual target value
                        double[] T = test_y.get(i, 0); // Y is 60x1

                        double t = T[0]; // Grab value

                        // There is a ternary split in the real line for our classification:
                        // (-infinity)---------(-1)---------(0)---------(+1)---------(+infinity)
                        //                   Class 1  |   Class 2   |  Class 3
                        // Activity 1: -infinity < prediction < -0.5
                        // Activity 2: -0.5 < prediction < +0.5
                        // Activity 3: +0.5 < prediction < infinity
                        Log.d("KUSHAL","Act Label==="+T[0]+" Prediction Label = "+p);
                        if ( ( p < -0.5  &&  t < -0.5 ) ||
                           ( (-0.5 <= p  &&  p < 0.5) && (-0.5 <= t  &&  t < 0.5) ) ||
                           ( ( 0.5 <= p  &&  0.5 <= t ) ) ) {
                            count++;
                        }

                    }

                    accuracy = (float)count / 6.0f;
                    Log.d("KUSHAL","Training Accuracy = "+accuracy);
                    // End training of model
                    // End training of model
                    // End training of model

                    cursor.close();
                    database.close();
                    return true;

            }catch(SQLiteException e){
                    e.printStackTrace();
                    Log.d("ashni, exception","ashni exception here");
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("ashni, exception","ashni exception here");
                return false;
            }
        }
                @Override
                protected void onPostExecute(final Boolean success) {
                    if (success) {
                        Toast.makeText(c, "Test Accuracy = "+(accuracy*100), Toast.LENGTH_SHORT).show();
                    }
                }
    }
}
