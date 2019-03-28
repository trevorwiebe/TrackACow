package com.trevorwiebe.trackacow.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Keep;

import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingArchivedLotEntity;

@Keep
@Entity(tableName = "archivedLot")
public class ArchivedLotEntity {

    public static final String ARCHIVED_LOT = "archivedLot";

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

    @ColumnInfo(name = "dateStarted")
    private long dateStarted;

    @ColumnInfo(name = "dateEnded")
    private long dateEnded;

    public ArchivedLotEntity(String lotName, String lotId, String customerName, int totalHead, String notes, long dateStarted, long dateEnded) {
        this.lotName = lotName;
        this.lotId = lotId;
        this.customerName = customerName;
        this.totalHead = totalHead;
        this.notes = notes;
        this.dateStarted = dateStarted;
        this.dateEnded = dateEnded;
    }

    @Ignore
    public ArchivedLotEntity(HoldingArchivedLotEntity holdingArchivedLotEntity) {
        this.lotName = holdingArchivedLotEntity.getLotName();
        this.lotId = holdingArchivedLotEntity.getLotId();
        this.customerName = holdingArchivedLotEntity.getCustomerName();
        this.totalHead = holdingArchivedLotEntity.getTotalHead();
        this.notes = holdingArchivedLotEntity.getNotes();
        this.dateStarted = holdingArchivedLotEntity.getDateStarted();
        this.dateEnded = holdingArchivedLotEntity.getDateEnded();
    }

    @Ignore
    public ArchivedLotEntity(LotEntity lotEntity, long dateEnded) {
        this.lotName = lotEntity.getLotName();
        this.lotId = lotEntity.getLotId();
        this.customerName = lotEntity.getCustomerName();
        this.totalHead = lotEntity.getTotalHead();
        this.notes = lotEntity.getNotes();
        this.dateStarted = lotEntity.getDate();
        this.dateEnded = dateEnded;
    }

    @Ignore
    public ArchivedLotEntity() {
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
}
