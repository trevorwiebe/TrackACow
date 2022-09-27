package com.trevorwiebe.trackacow.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.Keep;

import com.trevorwiebe.trackacow.data.cacheEntities.CacheLotEntity;

@Keep
@Entity(tableName = "lot")
public class LotEntity {

    public static final String LOT = "cattleLot";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    private int primaryKey;

    @ColumnInfo(name = "lotName")
    private String lotName;

    @ColumnInfo(name = "lotId")
    private String lotId;

    @ColumnInfo(name = "customerName")
    private String customerName;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "penId")
    private String penId;

    public LotEntity(String lotName, String lotId, String customerName, String notes, long date, String penId) {
        this.lotName = lotName;
        this.lotId = lotId;
        this.customerName = customerName;
        this.notes = notes;
        this.date = date;
        this.penId = penId;
    }

    public LotEntity(CacheLotEntity cacheLotEntity) {
        this.lotName = cacheLotEntity.getLotName();
        this.lotId = cacheLotEntity.getLotId();
        this.customerName = cacheLotEntity.getCustomerName();
        this.notes = cacheLotEntity.getNotes();
        this.date = cacheLotEntity.getDate();
        this.penId = cacheLotEntity.getPenId();
    }

    public LotEntity(ArchivedLotEntity archivedLotEntity) {
        this.lotName = archivedLotEntity.getLotName();
        this.lotId = archivedLotEntity.getLotId();
        this.customerName = archivedLotEntity.getCustomerName();
        this.notes = archivedLotEntity.getNotes();
        this.date = archivedLotEntity.getDateStarted();
        this.penId = "";
    }

    @Ignore
    public LotEntity() {
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getLotName() {
        return lotName;
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
}
