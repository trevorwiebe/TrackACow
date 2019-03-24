package com.trevorwiebe.trackacow.db.holdingUpdateEntities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Keep;

import com.trevorwiebe.trackacow.db.entities.LotEntity;

@Keep
@Entity(tableName = "holdingLot")
public class HoldingLotEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    private int primaryKey;

    @ColumnInfo(name = "lotName")
    private String lotName;

    @ColumnInfo(name = "lotId")
    private String lotId;

    @ColumnInfo(name = "customerName")
    private String customerName;

    @ColumnInfo(name = "totalHead")
    private int totalHead;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "penId")
    private String penId;

    @ColumnInfo(name = "whatHappened")
    private int whatHappened;

    public HoldingLotEntity(String lotName, String lotId, String customerName, int totalHead, String notes, long date, String penId, int whatHappened) {
        this.lotName = lotName;
        this.lotId = lotId;
        this.customerName = customerName;
        this.totalHead = totalHead;
        this.notes = notes;
        this.date = date;
        this.penId = penId;
        this.whatHappened = whatHappened;
    }

    public HoldingLotEntity(LotEntity lotEntity, int whatHappened) {
        this.lotName = lotEntity.getLotName();
        this.lotId = lotEntity.getLotId();
        this.customerName = lotEntity.getCustomerName();
        this.totalHead = lotEntity.getTotalHead();
        this.notes = lotEntity.getNotes();
        this.date = lotEntity.getDate();
        this.penId = lotEntity.getPenId();
        this.whatHappened = whatHappened;
    }

    @Ignore
    public HoldingLotEntity() {
    }

    public String getLotName() {
        return lotName;
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public void setLotName(String lotName) {
        this.lotName = lotName;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getTotalHead() {
        return totalHead;
    }

    public void setTotalHead(int totalHead) {
        this.totalHead = totalHead;
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
