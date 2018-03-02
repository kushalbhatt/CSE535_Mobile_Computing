package com.example.kushal.mc535;

/**
 * Created by RachelDedinsky on 2/23/18.
 */

//Patient objects are stored in the database.
public class Patient {
    //fields
    private long patienttimestamp;
    private float patientxvalues;
    private float patientyvalues;
    private float patientzvalues;
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
    public void setXValues(float x) {
        this.patientxvalues = x;
    }
    public float getXValues() {
        return this.patientxvalues;
    }
    public void setYValues(float y) {
        this.patientyvalues = y;
    }
    public float getYValues() {
        return this.patientyvalues;
    }
    public void setZValues(float z) {
        this.patientzvalues = z;
    }
    public float getZValues() {
        return this.patientzvalues;
    }
}

