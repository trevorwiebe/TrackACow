package com.trevorwiebe.trackacow.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Keep;

@Keep
@Entity(tableName = "call")
public class CallEntity {

    public static final String CALL = "call";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    private int primaryKey;

    @ColumnInfo(name = "amountFed")
    private int amountFed;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "lotId")
    private String lotId;

    @ColumnInfo(name = "id")
    private String id;

    public CallEntity(int amountFed, long date, String lotId, String id) {
        this.amountFed = amountFed;
        this.date = date;
        this.lotId = lotId;
        this.id = id;
    }

    // TODO: 4/3/2019 add constructor for HoldingCallEntity

    @Ignore
    public CallEntity() {
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int getAmountFed() {
        return amountFed;
    }

    public void setAmountFed(int amountFed) {
        this.amountFed = amountFed;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
