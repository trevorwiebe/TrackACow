package com.trevorwiebe.trackacow.objects;

import android.support.annotation.Keep;

@Keep
public class DeadCowObject {

    public static final String DEAD_COWS = "deadCows";

    private String penId;
    private String deadCowId;
    private String tagNumber;
    private long dateDied;
    private String notes;

    public DeadCowObject(String penId, String deadCowId, String tagNumber, long dateDied, String notes) {
        this.penId = penId;
        this.deadCowId = deadCowId;
        this.tagNumber = tagNumber;
        this.dateDied = dateDied;
        this.notes = notes;
    }

    public DeadCowObject(){}

    public String getPenId() {
        return penId;
    }

    public void setPenId(String penId) {
        this.penId = penId;
    }

    public String getDeadCowId() {
        return deadCowId;
    }

    public void setDeadCowId(String deadCowId) {
        this.deadCowId = deadCowId;
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(String tagNumber) {
        this.tagNumber = tagNumber;
    }

    public long getDateDied() {
        return dateDied;
    }

    public void setDateDied(long dateDied) {
        this.dateDied = dateDied;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
