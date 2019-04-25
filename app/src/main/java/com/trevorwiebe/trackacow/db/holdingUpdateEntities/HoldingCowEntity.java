package com.trevorwiebe.trackacow.db.holdingUpdateEntities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Keep;

import com.trevorwiebe.trackacow.db.entities.CowEntity;

@Keep
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

    @ColumnInfo(name = "lotId")
    private String lotId;

    @ColumnInfo(name = "whatHappened")
    private int whatHappened;

    public HoldingCowEntity(){}

    @Ignore
    public HoldingCowEntity(CowEntity cowEntity, int whatHappened) {
        this.isAlive = cowEntity.getIsAlive();
        this.cowId = cowEntity.getCowId();
        this.tagNumber = cowEntity.getTagNumber();
        this.date = cowEntity.getDate();
        this.notes = cowEntity.getNotes();
        this.lotId = cowEntity.getLotId();
        this.whatHappened = whatHappened;
    }

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

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public int getWhatHappened() {
        return whatHappened;
    }

    public void setWhatHappened(int whatHappened) {
        this.whatHappened = whatHappened;
    }
}
