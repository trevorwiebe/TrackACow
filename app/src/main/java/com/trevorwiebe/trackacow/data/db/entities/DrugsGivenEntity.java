package com.trevorwiebe.trackacow.data.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.Keep;

@Keep
@Entity(tableName = "DrugsGiven")
public class DrugsGivenEntity {

    public static final String DRUGS_GIVEN = "drugsGiven";
    public static final String LOT_ID = "lotId";
    public static final String COW_ID = "cowId";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    public int primaryKey;

    @ColumnInfo(name = "drugGivenId")
    public String drugGivenId;

    @ColumnInfo(name = "drugId")
    public String drugId;

    @ColumnInfo(name = "amountGiven")
    public int amountGiven;

    @ColumnInfo(name = "cowId")
    public String cowId;

    @ColumnInfo(name = "lotId")
    public String lotId;

    @ColumnInfo(name = "date")
    public long date;

    public DrugsGivenEntity(String drugGivenId, String drugId, int amountGiven, String cowId, String lotId, long date) {
        this.drugGivenId = drugGivenId;
        this.drugId = drugId;
        this.amountGiven = amountGiven;
        this.cowId = cowId;
        this.lotId = lotId;
        this.date = date;
    }

    @Ignore
    public DrugsGivenEntity(){}

    protected DrugsGivenEntity(Parcel in) {
        primaryKey = in.readInt();
        drugGivenId = in.readString();
        drugId = in.readString();
        amountGiven = in.readInt();
        cowId = in.readString();
        lotId = in.readString();
        date = in.readLong();
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getDrugGivenId() {
        return drugGivenId;
    }

    public void setDrugGivenId(String drugGivenId) {
        this.drugGivenId = drugGivenId;
    }

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public int getAmountGiven() {
        return amountGiven;
    }

    public void setAmountGiven(int amountGiven) {
        this.amountGiven = amountGiven;
    }

    public String getCowId() {
        return cowId;
    }

    public void setCowId(String cowId) {
        this.cowId = cowId;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
