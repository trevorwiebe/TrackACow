package com.trevorwiebe.trackacow.db.holdingUpdateEntities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "HoldingCow")
public class HoldingCowEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    private int primaryKey;

    @ColumnInfo(name = "isAlive")
    private int isAlive;

    @ColumnInfo(name = "cowId")
    private String cowId;

    @ColumnInfo(name = "tagNumber")
    private int tagNumber;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "penId")
    private String penId;

    @ColumnInfo(name = "whatHappened")
    private int whatHappened;

    public HoldingCowEntity(){}

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int isAlive() {
        return isAlive;
    }

    public void setIsAlive(int isAlive) {
        this.isAlive = isAlive;
    }

    public void setCowId(String cowId) {
        this.cowId = cowId;
    }

    public String getCowId() {
        return cowId;
    }

    public int getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(int tagNumber) {
        this.tagNumber = tagNumber;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPenId() {
        return penId;
    }

    public void setPenId(String penId) {
        this.penId = penId;
    }

    public int getWhatHappened() {
        return whatHappened;
    }

    public void setWhatHappened(int whatHappened) {
        this.whatHappened = whatHappened;
    }
}
