package com.example.racheldedinsky.group17assignment3;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by asmodi on 4/12/18.
 */





public  class fetchData extends AppCompatActivity {

    fetchData(){

    }


    public void executeTask(){
        Log.d(" fetch data","Hi");
        new CreateCSV().execute();
        new CreateFloatArray().execute();
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

    class CreateFloatArray extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

        float[][] myValues;
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
                            for(int j=1;j<152;j++){
                                Float val = cursor.getFloat(j);
                                myValues[i-1][j-1]=val;
                                Log.d("val",""+val);


                            }
                            Log.d("new row","---------");
                            cursor.moveToNext();

                        }

                    }
                    Log.d("float array",myValues+"");
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
