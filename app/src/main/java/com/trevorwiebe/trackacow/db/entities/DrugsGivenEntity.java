package com.trevorwiebe.trackacow.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

@Keep
@Entity(tableName = "DrugsGiven")
public class DrugsGivenEntity {

    public static final String DRUGS_GIVEN = "drugsGiven";
    public static final String LOT_ID = "lotId";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    public int primaryKey;

    @ColumnInfo(name = "drugGiveId")
    public String drugGivenId;

    @ColumnInfo(name = "drugId")
    public String drugId;

    @ColumnInfo(name = "amountGiven")
    public int amountGiven;

    @ColumnInfo(name = "cowId")
    public String cowId;

    @ColumnInfo(name = "lotId")
    public String lotId;

    public DrugsGivenEntity(String drugGivenId, String drugId, int amountGiven, String cowId, String lotId) {
        this.drugGivenId = drugGivenId;
        this.drugId = drugId;
        this.amountGiven = amountGiven;
        this.cowId = cowId;
        this.lotId = lotId;
    }

    @Ignore
    public DrugsGivenEntity(){}

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
}
