package com.trevorwiebe.trackacow.data.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.Keep;

import com.trevorwiebe.trackacow.data.db.holdingUpdateEntities.HoldingLoadEntity;

@Keep
@Entity(tableName = "load")
public class LoadEntity {

    public static final String LOAD = "loads";

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

    public LoadEntity(int numberOfHead, long date, String description, String lotId, String loadId) {
        this.numberOfHead = numberOfHead;
        this.date = date;
        this.description = description;
        this.lotId = lotId;
        this.loadId = loadId;
    }

    @Ignore
    public LoadEntity(HoldingLoadEntity holdingLoadEntity) {
        this.numberOfHead = holdingLoadEntity.getNumberOfHead();
        this.date = holdingLoadEntity.getDate();
        this.description = holdingLoadEntity.getDescription();
        this.lotId = holdingLoadEntity.getLotId();
        this.loadId = holdingLoadEntity.getLoadId();
    }

    @Ignore
    public LoadEntity() {
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
}
