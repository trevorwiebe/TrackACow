package com.trevorwiebe.trackacow.db.holdingUpdateEntities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Keep;

@Keep
@Entity(tableName = "HoldingDrugsGiven")
public class HoldingDrugsGivenEntity {

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

    @ColumnInfo(name = "penId")
    public String penId;

    @ColumnInfo(name = "whatHappened")
    public int whatHappened;

    public HoldingDrugsGivenEntity(){}

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
