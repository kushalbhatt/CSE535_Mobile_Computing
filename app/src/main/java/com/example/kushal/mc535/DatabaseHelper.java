package com.example.kushal.mc535;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kushal on 2/17/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    //schema change will require version change
    private final String LOG_LABEL = "DBHELPER";
    static final int DB_VERSION = 1;
    //notice that this is database name not table name
    static final String DATABASE_NAME = "CSE535_ASSIGNMENT2";

    /*
        Table Columns
        Table name will change for each patient
     */
    static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    static final String COLUMN_NAME_X = "x";
    static final String COLUMN_NAME_Y = "y";
    static final String COLUMN_NAME_Z = "z";

    private SQLiteDatabase databse = null;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        databse = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //called only after getReadable or getWritableDatabase() is invoked
        Log.d(LOG_LABEL,"onCreate Called!"+sqLiteDatabase);
        databse = sqLiteDatabase;
        //if required add code here when db is first created
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Not required for now
    }

    public  void createPatientTable(String table)
    {
        String create_table = "CREATE TABLE IF NOT EXISTS "+table+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                COLUMN_NAME_TIMESTAMP+" DATETIME,"+
                                COLUMN_NAME_X+" REAL,"+COLUMN_NAME_Y+" REAL,"+COLUMN_NAME_Z+" REAL)";

        databse.execSQL(create_table);
        Log.d(LOG_LABEL,"Table created: "+table);
    }

    public  void insertValues(String table,String time,float x, float y, float z)
    {

    }

}
