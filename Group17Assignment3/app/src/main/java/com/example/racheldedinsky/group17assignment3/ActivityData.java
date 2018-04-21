package com.example.racheldedinsky.group17assignment3;

/**
 * Created by RachelDedinsky on 3/28/18.
 */

public class ActivityData {
    //fields
    private String activity_iD;
    private String activity_data;
    private String activity_label;
    //constructors
    public ActivityData(){}
    public ActivityData(String activityData, String id, String activityLabel){
        this.activity_iD = id;
        this.activity_data = activityData;
        this.activity_label = activityLabel;
    }

    //properties
    public void setiD(String id) {

        this.activity_iD = id;
    }
    public String getiD() {
        return this.activity_iD;
    }
    public void setActivityData(String activityData) {
        this.activity_data = activityData;
    }
    public String getActivityData() {

        return this.activity_data;
    }
    public void setActivityLabel(String activityLabel) {

        this.activity_label = activityLabel;
    }
    public String getActivityLabel() {

        return this.activity_label;
    }
}
