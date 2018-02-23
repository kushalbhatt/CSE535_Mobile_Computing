package com.example.kushal.mc535;

/**
 * Created by RachelDedinsky on 2/23/18.
 */

public class Patient {
    //fields
    private int patientID;
    private int patientAge;
    private String patientName;
    private String patientSex;
    // constructors
    public Patient() {}
    public Patient(int id, int age, String name, String gender) {
        this.patientID = id;
        this.patientAge = age;
        this.patientName = name;
        this.patientSex = gender;

    }
    // properties
    public void setID(int id) {
        this.patientID = id;
    }
    public int getID() {
        return this.patientID;
    }
    public void setPatientAge(int age) {
        this.patientAge = age;
    }
    public int getPatientAge() {
        return this.patientAge;
    }
    public void setPatientName(String patientname) {
        this.patientName = patientname;
    }
    public String getPatientName() {
        return this.patientName;
    }
    public void setPatientSex(String sex) {
        this.patientSex = sex;
    }
    public String getPatientSex() {
        return this.patientSex;
    }
}

