package com.trevorwiebe.trackacow.db.holdingUpdateEntities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "HoldingPen")
public class HoldingPenEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    private int primaryKey;

    @ColumnInfo(name = "penId")
    private String penId;

    @ColumnInfo(name = "customerName")
    private String customerName;

    @ColumnInfo(name = "isActive")
    private int isActive;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "penName")
    private String penName;

    @ColumnInfo(name = "totalHead")
    private int totalHead;

    @ColumnInfo(name = "whatHappened")
    private int whatHappened;

    public HoldingPenEntity(){}

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getPenId() {
        return penId;
    }

    public void setPenId(String penId) {
        this.penId = penId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPenName() {
        return penName;
    }

    public void setPenName(String penName) {
        this.penName = penName;
    }

    public int getTotalHead() {
        return totalHead;
    }

    public void setTotalHead(int totalHead) {
        this.totalHead = totalHead;
    }

    public int getWhatHappened() {
        return whatHappened;
    }

    public void setWhatHappened(int whatHappened) {
        this.whatHappened = whatHappened;
    }
}
