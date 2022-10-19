package com.trevorwiebe.trackacow.data.cacheEntities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.Keep;

import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity;

@Keep
@Entity(tableName = "HoldingDrugsGiven")
public class CacheDrugsGivenEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    private int primaryKey;

    @ColumnInfo(name = "drugGivenId")
    private String drugGivenId;

    @ColumnInfo(name = "drugId")
    private String drugId;

    @ColumnInfo(name = "amountGiven")
    private int amountGiven;

    @ColumnInfo(name = "cowId")
    private String cowId;

    @ColumnInfo(name = "lotId")
    private String lotId;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "whatHappened")
    private int whatHappened;

    public CacheDrugsGivenEntity(){}

    @Ignore
    public CacheDrugsGivenEntity(DrugsGivenEntity drugsGivenEntity, int whatHappened) {
        this.drugGivenId = drugsGivenEntity.getDrugGivenId();
        this.drugId = drugsGivenEntity.getDrugId();
        this.amountGiven = drugsGivenEntity.getAmountGiven();
        this.cowId = drugsGivenEntity.getCowId();
        this.lotId = drugsGivenEntity.getLotId();
        this.date = drugsGivenEntity.getDate();
        this.whatHappened = whatHappened;
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

    public int getWhatHappened() {
        return whatHappened;
    }

    public void setWhatHappened(int whatHappened) {
        this.whatHappened = whatHappened;
    }
}
