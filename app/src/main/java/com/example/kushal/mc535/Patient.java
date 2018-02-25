package com.example.kushal.mc535;

/**
 * Created by RachelDedinsky on 2/23/18.
 */

public class Patient {
    //fields
    private int patienttimestamp;
    private int patientxvalues;
    private int patientyvalues;
    private int patientzvalues;
    // constructors
    public Patient() {}
    public Patient(int timestamp, int xvalues, int yvalues, int zvalues) {
        this.patienttimestamp = timestamp;
        this.patientxvalues = xvalues;
        this.patientyvalues = yvalues;
        this.patientzvalues = zvalues;

    }
    // properties
    public void setTimestamp(int timeStamp) {
        this.patienttimestamp = timeStamp;
    }
    public int getTimestamp() {
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

