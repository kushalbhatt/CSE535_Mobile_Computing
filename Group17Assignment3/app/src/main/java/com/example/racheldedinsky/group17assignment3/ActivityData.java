package com.example.racheldedinsky.group17assignment3;

/**
 * Created by RachelDedinsky on 3/26/18.
 */

public class ActivityData {
    //fields
    private long patienttimestamp;
    private float patientxvalues;
    private float patientyvalues;
    private float patientzvalues;
    // constructors
    public ActivityData() {}
    public ActivityData(long timestamp, float xvalues, float yvalues, float zvalues) {
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
