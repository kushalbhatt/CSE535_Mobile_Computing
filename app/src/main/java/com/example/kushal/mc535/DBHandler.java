package com.example.kushal.mc535;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by RachelDedinsky on 2/23/18.
 */

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "patientDB.db";
    public static final String TABLE_NAME = "Name_ID_Age_Sex";
    public static final String COLUMN_TIMESTAMP = "Timestamp";
    public static final String COLUMN_X = "XValues";
    public static final String COLUMN_Y = "YValues";
    public static final String COLUMN_Z = "ZValues";
    //initialize the database
    public DBHandler(Context context, String name,
                     SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = " CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_TIMESTAMP + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_X + " INTEGER NOT NULL, " +
                COLUMN_Y + " INTEGER NOT NULL, " +
                COLUMN_Z + " INTEGER NOT NULL);";
                //"CREATE TABLE" + TABLE_NAME + "(" + COLUMN_ID +
                //"INTEGER PRIMARYKEY," + COLUMN_AGE +
                //"INTEGER," + COLUMN_NAME + "TEXT," + COLUMN_SEX + "TEXT )";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}
    public String loadHandler() {
        String result = "";
        String query = "Select*FROM" + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int result_0 = cursor.getInt(0);
            String result_1 = cursor.getString(1);
            result += String.valueOf(result_0) + " " + result_1 +
                    System.getProperty("line.separator");
        }
        cursor.close();
        db.close();
        return result;
    }
    public void addHandler(Patient patient) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP, patient.getTimestamp());
        values.put(COLUMN_X, patient.getXValues());
        values.put(COLUMN_Y, patient.getYValues());
        values.put(COLUMN_Z, patient.getZValues());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    public Patient findHandler(int timestamp) {
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_TIMESTAMP + " = " + timestamp;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery(query, null);
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
        db.close();
        return patient;
    }
    public boolean deleteHandler(int iD) {
        boolean result = false;
        String query = "Select*FROM" + TABLE_NAME + "WHERE" + COLUMN_TIMESTAMP + "= '" + String.valueOf(iD) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Patient patient = new Patient();
        if (cursor.moveToFirst()) {
            patient.setTimestamp(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_NAME, COLUMN_TIMESTAMP + "=?",
                    new String[] {
                String.valueOf(patient.getTimestamp())
            });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }
    public boolean updateHandler(int iD, int age, String name, String sex) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_TIMESTAMP, iD);
        args.put(COLUMN_X, age);
        args.put(COLUMN_Y, name);
        args.put(COLUMN_Z, sex);
        return db.update(TABLE_NAME, args, COLUMN_TIMESTAMP + "=" + iD, null) > 0;
    }

}
