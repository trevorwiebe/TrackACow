package com.trevorwiebe.trackacow.data.local.holdingUpdateEntities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.Keep;

import com.trevorwiebe.trackacow.data.local.entities.ArchivedLotEntity;

@Keep
@Entity(tableName = "holdingArchivedLot")
public class HoldingArchivedLotEntity {

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

    @ColumnInfo(name = "dateStarted")
    private long dateStarted;

    @ColumnInfo(name = "dateEnded")
    private long dateEnded;

    @ColumnInfo(name = "whatHappened")
    private int whatHappened;


    public HoldingArchivedLotEntity(String lotName, String lotId, String customerName, String notes, long dateStarted, long dateEnded, int whatHappened) {
        this.lotName = lotName;
        this.lotId = lotId;
        this.customerName = customerName;
        this.notes = notes;
        this.dateStarted = dateStarted;
        this.dateEnded = dateEnded;
        this.whatHappened = whatHappened;
    }

    @Ignore
    public HoldingArchivedLotEntity(ArchivedLotEntity archivedLotEntity, int whatHappened) {
        this.lotName = archivedLotEntity.getLotName();
        this.lotId = archivedLotEntity.getLotId();
        this.customerName = archivedLotEntity.getCustomerName();
        this.notes = archivedLotEntity.getNotes();
        this.dateStarted = archivedLotEntity.getDateStarted();
        this.dateEnded = archivedLotEntity.getDateEnded();
        this.whatHappened = whatHappened;
    }

    @Ignore
    public HoldingArchivedLotEntity() {
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

    public long getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(long dateStarted) {
        this.dateStarted = dateStarted;
    }

    public long getDateEnded() {
        return dateEnded;
    }

    public void setDateEnded(long dateEnded) {
        this.dateEnded = dateEnded;
    }

    public int getWhatHappened() {
        return whatHappened;
    }

    public void setWhatHappened(int whatHappened) {
        this.whatHappened = whatHappened;
    }
}
