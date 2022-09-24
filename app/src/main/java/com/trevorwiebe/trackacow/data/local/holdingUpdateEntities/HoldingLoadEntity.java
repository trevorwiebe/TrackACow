package com.trevorwiebe.trackacow.data.local.holdingUpdateEntities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.Keep;

import com.trevorwiebe.trackacow.data.local.entities.LoadEntity;

@Keep
@Entity(tableName = "holdingLoad")
public class HoldingLoadEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    private int primaryKey;

    @ColumnInfo(name = "numberOfHead")
    private int numberOfHead;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "lotId")
    private String lotId;

    @ColumnInfo(name = "loadId")
    private String loadId;

    @ColumnInfo(name = "whatHappened")
    private int whatHappened;

    public HoldingLoadEntity(int numberOfHead, long date, String description, String lotId, String loadId, int whatHappened) {
        this.numberOfHead = numberOfHead;
        this.date = date;
        this.description = description;
        this.lotId = lotId;
        this.loadId = loadId;
        this.whatHappened = whatHappened;
    }

    @Ignore
    public HoldingLoadEntity(LoadEntity loadEntity, int whatHappened) {
        this.numberOfHead = loadEntity.getNumberOfHead();
        this.date = loadEntity.getDate();
        this.description = loadEntity.getDescription();
        this.lotId = loadEntity.getLotId();
        this.loadId = loadEntity.getLoadId();
        this.whatHappened = whatHappened;
    }

    @Ignore
    public HoldingLoadEntity() {
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int getNumberOfHead() {
        return numberOfHead;
    }

    public void setNumberOfHead(int numberOfHead) {
        this.numberOfHead = numberOfHead;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public String getLoadId() {
        return loadId;
    }

    public void setLoadId(String loadId) {
        this.loadId = loadId;
    }

    public int getWhatHappened() {
        return whatHappened;
    }

    public void setWhatHappened(int whatHappened) {
        this.whatHappened = whatHappened;
    }
}
