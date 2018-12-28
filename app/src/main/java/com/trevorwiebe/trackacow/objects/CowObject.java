package com.trevorwiebe.trackacow.objects;

import java.util.ArrayList;

public class CowObject {

    public static final String COW = "cows";
    public static final String COW_NUMBER = "cowNumber";
    public static final String COW_ID = "cowId";
    public static final String PEN_ID = "penId";

    private int cowNumber;
    private String cowId;
    private String penId;
    private String notes;
    // TODO: 12/27/2018 change date to last treated date
    private long date;
    private ArrayList<CowDrugObject> mDrugList = new ArrayList<>();

    public CowObject(int cowNumber, String cowId, String penId, String notes, long date, ArrayList<CowDrugObject> mDrugList) {
        this.cowNumber = cowNumber;
        this.cowId = cowId;
        this.penId = penId;
        this.notes = notes;
        this.date = date;
        this.mDrugList = mDrugList;
    }

    public CowObject(){}

    public int getCowNumber() {
        return cowNumber;
    }

    public void setCowNumber(int cowNumber) {
        this.cowNumber = cowNumber;
    }

    public String getCowId() {
        return cowId;
    }

    public void setCowId(String cowId) {
        this.cowId = cowId;
    }

    public String getPenId() {
        return penId;
    }

    public void setPenId(String penId) {
        this.penId = penId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public ArrayList<CowDrugObject> getmDrugList() {
        return mDrugList;
    }

    public void setmDrugList(ArrayList<CowDrugObject> mDrugList) {
        this.mDrugList = mDrugList;
    }
}
