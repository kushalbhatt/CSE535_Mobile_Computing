package com.example.racheldedinsky.group17assignment3;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.ml.Ml;

import java.io.File;
import java.io.FileWriter;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by asmodi on 4/12/18.
 */

public  class fetchData extends AppCompatActivity {

    public static float myValues[][];
    fetchData(){}

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

        //        @Override
        //        protected void onPostExecute(final Boolean success) {
        //            if (success) {
        //                Toast.makeText(getApplicationContext(), "Export successful!", Toast.LENGTH_SHORT).show();
        //            }  else {
        //                Toast.makeText(getApplicationContext(), "Export failed", Toast.LENGTH_SHORT).show();
        //            }
        //        }

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
                    Log.d("cursor rows",""+cursor.getCount());
                    myValues = new float[cursor.getCount()][152];
                    if(cursor.moveToFirst()){
                        for(int i=1;i<cursor.getCount();i++){
                            for(int j=1;j<151;j++){
                                Float val = cursor.getFloat(j);
                                myValues[i-1][j-1]=val;
                                Log.d("val",""+val);
                            }
                            String activity = cursor.getString(151);
                            if(activity.equals("Walk")){
                                myValues[i-1][151] = 0;
                            }
                            else if(activity.equals("Run")){
                                myValues[i-1][151] =1;
                            }
                            else{
                                myValues[i-1][151]  = -1;
                            }
                            Log.d("label",myValues[i-1][151]+"");
                            Log.d("new row","---------");
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

                    // Copy data into X and Y
                    for (int i=0; i < 60; i++) {
                        for(int j=0; j < 150; j++) {
                            MainActivity.X.put(i, j, MainActivity.dataset_2Darr[i][j]); // Copy 2D array into mat object
                        }
                    }
                    // Itterate down rows of right-most column
                    for (int i = 0; i < 60; ++i)
                        MainActivity.Y.put(i,0, (int)MainActivity.dataset_2Darr[i][150]); // Labels

                    // Train the model using X and Y
                    boolean isTrained = MainActivity.classifier.isTrained();
                    MainActivity.classifier.train(MainActivity.X, Ml.ROW_SAMPLE, MainActivity.Y);
                    isTrained = MainActivity.classifier.isTrained();

                    int getType = MainActivity.classifier.getType();
                    Mat getClassWeights = MainActivity.classifier.getClassWeights();
                    TermCriteria getTermCriteria = MainActivity.classifier.getTermCriteria();

                    // ----------------------------
                    //  -Compute training accuracy:
                    // ----------------------------

                    // Count successful classifications
                    int count = 0;


                    // Iterate over the examples
                    for (int i = 0; i < 60; ++i) {

                        // Extract row of X
                        Mat x = Mat.zeros(1, 150, CvType.CV_32FC1);
                        for (int j = 0; j < 150; ++j) {
                            x.put(0, j, MainActivity.X.get(i, j)); // Individual feature vector for ith example
                        }

                        // Pass in training example and compute prediction
                        Mat outMat = new Mat();
                        float p = MainActivity.classifier.predict(x, outMat, 0);

                        // Get actual target value
                        double[] T = MainActivity.Y.get(i, 0); // Y is 60x1
                        double t = T[0]; // Grab value

                        // There is a ternary split in the real line for our classification:
                        // (-infinity)---------(-1)---------(0)---------(+1)---------(+infinity)
                        //                   Class 1  |   Class 2   |  Class 3
                        // Activity 1: -infinity < prediction < -0.5
                        // Activity 2: -0.5 < prediction < +0.5
                        // Activity 3: +0.5 < prediction < infinity
                        if ( ( p < -0.5  &&  t < -0.5 ) ||
                           ( (-0.5 <= p  &&  p < 0.5) && (-0.5 <= t  &&  t < 0.5) ) ||
                           ( ( 0.5 <= p  &&  0.5 <= t ) ) ) {
                            count++;
                        }


                    }

                    float accuracy = (float)count / 60.0f;

                    // End training of model
                    // End training of model
                    // End training of model

                    cursor.close();
                    database.close();
                    return null;

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
        //        @Override
        //        protected void onPostExecute(final Boolean success) {
        //            if (success) {
        //                Toast.makeText(getApplicationContext(), "Export successful!", Toast.LENGTH_SHORT).show();
        //            }  else {
        //                Toast.makeText(getApplicationContext(), "Export failed", Toast.LENGTH_SHORT).show();
        //            }
        //        }
    }
}
