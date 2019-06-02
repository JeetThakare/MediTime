package com.meditime.meditime;

import java.io.DataInputStream;
import java.util.Date;

public class Medicine {
    private String doctorID;
    private String name;
    private String patientID;
    private String dayFreq;
    private String endDate;
    private String photoUrl;
    private String startDate;
    private String weekFreq;
    private String medicineID;

    public Medicine(String doctorID, String name, String patientID, String dayFreq, String endDate, String photoUrl, String startDate, String weekFreq, String medicineID){
        this.doctorID=doctorID;
        this.name=name;
        this.patientID=patientID;
        this.dayFreq=dayFreq;
        this.photoUrl=photoUrl;
        this.weekFreq=weekFreq;
        this.endDate=endDate;
        this.startDate=startDate;
        this.medicineID=medicineID;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getDoctorID() {
        return doctorID;
    }
    public String getName() {
        return name;
    }
    public String getPatientID() {
        return patientID;
    }
    public String getDayFreq() {
        return dayFreq;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public String getWeekFreq() {
        return weekFreq;
    }
    public String getEndDate() {
        return endDate;
    }
    public String getStartDate() {
        return startDate;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }
    public void setDayFreq(String dayFreq) {
        this.dayFreq = dayFreq;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    public void setWeekFreq(String weekFreq) {
        this.weekFreq = weekFreq;
    }

    public void setEndDate(String endDate){
        this.endDate=endDate;
    }

    public String getMedicineID() {
        return medicineID;
    }

    public void setMedicineID(String medicineID) {
        this.medicineID = medicineID;
    }

    public void setStartDate(String startDate){
        this.startDate=startDate;
    }
}
