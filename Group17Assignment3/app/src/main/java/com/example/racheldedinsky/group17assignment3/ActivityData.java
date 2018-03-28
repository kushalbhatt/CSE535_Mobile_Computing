package com.example.racheldedinsky.group17assignment3;

import java.util.ArrayList;

/**
 * Created by RachelDedinsky on 3/28/18.
 */

public class ActivityData {
    //fields
    private int activity_iD;
    private ArrayList<Integer[]> activity_data = new ArrayList<Integer[]>();
    private String activity_label;
    //constructors
    public ActivityData(){}
    public ActivityData(ArrayList<Integer[]> activityData,int id, String activityLabel){
        this.activity_iD = id;
        this.activity_data = activityData;
        this.activity_label = activityLabel;
    }

    //properties
    public void setiD(int id) {

        this.activity_iD = id;
    }
    public int getiD() {
        return this.activity_iD;
    }
    public void setActivityData(ArrayList<Integer[]> activityData) {
        this.activity_data = activityData;
    }
    public ArrayList<Integer[]> getActivityData() {

        return this.activity_data;
    }
    public void setActivityLabel(String activityLabel) {

        this.activity_label = activityLabel;
    }
    public String getActivityLabel() {

        return this.activity_label;
    }
}
