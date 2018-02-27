package com.example.kushal.mc535;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by RachelDedinsky on 2/23/18.
 */

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "patientDB.db";
    public static String TABLE_NAME = "Name_ID_Age_Sex";
    public static final String COLUMN_TIMESTAMP = "Timestamp";
    public static final String COLUMN_X = "XValues";
    public static final String COLUMN_Y = "YValues";
    public static final String COLUMN_Z = "ZValues";
    //initialize the database
    private SQLiteDatabase database = null;
    public DBHandler(Context context, String name,
                     SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //called only after getReadable or getWritableDatabase() is invoked
        database = db;
        //if required add code here when db is first created
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}
    public  void createPatientTable(String table)
    {
        database = getWritableDatabase();
        //Intger is 8 bytes long, as is a long, so timestamp can be stored in an integer
        String create_table = "CREATE TABLE IF NOT EXISTS "+table+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COLUMN_TIMESTAMP+" INTEGER,"+
                COLUMN_X+" REAL,"+COLUMN_Y+" REAL,"+COLUMN_Z+" REAL);";
        TABLE_NAME=table;
        database.execSQL(create_table);
    }
    //Use this to add entries to the database
    public void addHandler(Patient patient) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP, patient.getTimestamp());
        values.put(COLUMN_X, patient.getXValues());
        values.put(COLUMN_Y, patient.getYValues());
        values.put(COLUMN_Z, patient.getZValues());
        database.insert(TABLE_NAME, null, values);
    }
    //Use this to check if a timestamp already exists in the database.
    public Patient findHandler(long timestamp) {
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_TIMESTAMP + "=" + timestamp;
        Cursor cursor = database.rawQuery(query, null);
        Patient patient = new Patient();
        if (cursor!=null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            patient.setTimestamp(Integer.parseInt(cursor.getString(0)));
            patient.setXValues(Integer.parseInt(cursor.getString(1)));
            patient.setYValues(Integer.parseInt(cursor.getString(2)));
            patient.setZValues(Integer.parseInt(cursor.getString(3)));
            cursor.close();
        } else {
            patient = null;
        }
        return patient;
    }
}
