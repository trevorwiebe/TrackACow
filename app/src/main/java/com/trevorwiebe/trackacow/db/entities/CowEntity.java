package com.trevorwiebe.trackacow.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

@Keep
@Entity(tableName = "Cow")
public class CowEntity {

    public static final String COW = "cows";
    public static final String COW_NUMBER = "cowNumber";
    public static final String COW_ID = "cowId";
    public static final String LOT_ID = "lotId";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    public int primaryKey;

    @ColumnInfo(name = "isAlive")
    public int isAlive;

    @ColumnInfo(name = "cowId")
    public String cowId;

    @ColumnInfo(name = "tagNumber")
    public int tagNumber;

    @ColumnInfo(name = "date")
    public long date;

    @ColumnInfo(name = "notes")
    public String notes;

    @ColumnInfo(name = "lotId")
    public String lotId;

    public CowEntity(int isAlive, String cowId, int tagNumber, long date, String notes, String lotId) {
        this.isAlive = isAlive;
        this.cowId = cowId;
        this.tagNumber = tagNumber;
        this.date = date;
        this.notes = notes;
        this.lotId = lotId;
    }

    @Ignore
    public CowEntity(){}

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int isAlive() {
        return isAlive;
    }

    public void setAlive(int alive) {
        isAlive = alive;
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
}
