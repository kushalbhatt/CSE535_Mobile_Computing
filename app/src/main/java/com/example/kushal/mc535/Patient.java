package com.example.kushal.mc535;

/**
 * Created by RachelDedinsky on 2/23/18.
 */

//Patient objects are stored in the database.
public class Patient {
    //fields
    private long patienttimestamp;
    private int patientxvalues;
    private int patientyvalues;
    private int patientzvalues;
    // constructors
    public Patient() {}
    public Patient(long timestamp, int xvalues, int yvalues, int zvalues) {
        this.patienttimestamp = timestamp;
        this.patientxvalues = xvalues;
        this.patientyvalues = yvalues;
        this.patientzvalues = zvalues;

    }
    // properties
    public void setTimestamp(long timeStamp) {
        this.patienttimestamp = timeStamp;
    }
    public long getTimestamp() {
        return this.patienttimestamp;
    }
    public void setXValues(int x) {
        this.patientxvalues = x;
    }
    public int getXValues() {
        return this.patientxvalues;
    }
    public void setYValues(int y) {
        this.patientyvalues = y;
    }
    public int getYValues() {
        return this.patientyvalues;
    }
    public void setZValues(int z) {
        this.patientzvalues = z;
    }
    public int getZValues() {
        return this.patientzvalues;
    }
}

