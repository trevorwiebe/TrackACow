package com.trevorwiebe.trackacow.db.holdingUpdateEntities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Keep;

@Keep
@Entity(tableName = "HoldingPen")
public class HoldingPenEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    public int primaryKey;

    @ColumnInfo(name = "penId")
    public String penId;

    @ColumnInfo(name = "customerName")
    public String customerName;

    @ColumnInfo(name = "isActive")
    public int isActive;

    @ColumnInfo(name = "notes")
    public String notes;

    @ColumnInfo(name = "penName")
    public String penName;

    @ColumnInfo(name = "totalHead")
    public int totalHead;

    @ColumnInfo(name = "whatHappened")
    public int whatHappened;

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
