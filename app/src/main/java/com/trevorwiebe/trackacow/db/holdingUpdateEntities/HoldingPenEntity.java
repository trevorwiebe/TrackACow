package com.trevorwiebe.trackacow.db.holdingUpdateEntities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "HoldingPen")
public class HoldingPenEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    private int primaryKey;

    @ColumnInfo(name = "customerName")
    private String customerName;

    @ColumnInfo(name = "isActive")
    private boolean isActive;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "penId")
    private String penDatabaseId;

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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPenDatabaseId() {
        return penDatabaseId;
    }

    public void setPenDatabaseId(String penDatabaseId) {
        this.penDatabaseId = penDatabaseId;
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
