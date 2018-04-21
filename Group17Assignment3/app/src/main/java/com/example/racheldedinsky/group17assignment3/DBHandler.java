package com.example.racheldedinsky.group17assignment3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by RachelDedinsky on 3/28/18.
 */

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    //WE need to store db to Eternal SD CARD
    public static final String DATABASE_PATH = Environment.getExternalStorageDirectory()+"/CSE535_ASSIGNMENT3/";
    public static final String DATABASE_NAME = "activityDB.db";
    public static String TABLE_NAME = "ID";
    public static final String COLUMN_ID = "ActivityID";
    public static final String COLUMN_DATA = "ActivityData";
    public static final String COLUMN_LABEL = "ActivityLabel";

    String columnsString = "";
    //initialize the database
    private SQLiteDatabase database = null;
    public DBHandler(Context context)
    {
        super(context, DATABASE_PATH+DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //called only after getReadable or getWritableDatabase() is invoked
        Log.d("RAHEL","once on create db"+DATABASE_PATH);
        database = db;
        StringBuilder sqlStr = new StringBuilder();
        for(int i=0;i<50;i++)
        {
            sqlStr.append("x"+i+" FLOAT, "+"y"+i+" FLOAT, "+"z"+i+" FLOAT, ");
        }
        columnsString=sqlStr.toString();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}
    public  void createTable(String table)
    {
        if(database==null)
            database = getWritableDatabase();
        //kushal:  To avoid unnecessary db interactions as much as we can
        if(!TABLE_NAME.equals(table)) {
            //Integer is 8 bytes long, as is a long, so timestamp can be stored in an integer
            String create_table = "CREATE TABLE IF NOT EXISTS " + table + "(" +
                    COLUMN_ID + " TEXT," +
                    columnsString + COLUMN_LABEL + " TEXT);";
            //create column_data "X"+i+ " FLOAT," in loop
            TABLE_NAME = table;
            database.execSQL(create_table);
            Log.d("KUSHAL", "Tabel created : " + table);
        }
    }

    //Use this to add entries to the database
    public void addHandler(String id, ArrayList<Float[]> data, String label) {
        ContentValues values = new ContentValues();
        //Integer is 8 bytes long, as is a long, so timestamp can be stored in an integer
        values.put(COLUMN_ID, id);
        Log.d("RACHEL",data.size()+"");
        for(int i=0;i<50;i++)
        {
            Float[] vals= data.get(i);
            values.put("x"+i,vals[0]);
            values.put("y"+i,vals[1]);
            values.put("z"+i,vals[2]);
        }
        values.put(COLUMN_LABEL, label);
        database.insert(TABLE_NAME, null, values);
    }
    //Use this to check if a timestamp already exists in the database.
    public ActivityData findHandler(int id) {
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + id;
        Cursor cursor = database.rawQuery(query, null);
        ActivityData activityData = new ActivityData();
        if (cursor!=null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            activityData.setiD(cursor.getString(0));
            activityData.setActivityData(cursor.getString(1));
            activityData.setActivityLabel(cursor.getString(2));
            cursor.close();
        } else {
            activityData = null;
        }
        return activityData;
    }

}
