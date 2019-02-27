package com.trevorwiebe.trackacow.db.holdingUpdateEntities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Keep;

@Keep
@Entity(tableName = "HoldingDrug")
public class HoldingDrugEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    public int primaryKey;

    @ColumnInfo(name = "defaultAmount")
    public int defaultAmount;

    @ColumnInfo(name = "drugId")
    public String drugId;

    @ColumnInfo(name = "drugName")
    public String drugName;

    @ColumnInfo(name = "whatHappened")
    public int whatHappened;

    public HoldingDrugEntity() {}

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int getDefaultAmount() {
        return defaultAmount;
    }

    public void setDefaultAmount(int defaultAmount) {
        this.defaultAmount = defaultAmount;
    }

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public int getWhatHappened() {
        return whatHappened;
    }

    public void setWhatHappened(int whatHappened) {
        this.whatHappened = whatHappened;
    }
}
