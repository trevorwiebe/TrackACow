package com.trevorwiebe.trackacow.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.Keep;

@Keep
@Entity(tableName = "call")
public class CallEntity {

    public static final String CALL = "call";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    private int primaryKey;

    @ColumnInfo(name = "callAmount")
    private int callAmount;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "lotId")
    private String lotId;

    @ColumnInfo(name = "id")
    private String id;

    public CallEntity(int callAmount, long date, String lotId, String id) {
        this.callAmount = callAmount;
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

    public int getCallAmount() {
        return callAmount;
    }

    public void setCallAmount(int callAmount) {
        this.callAmount = callAmount;
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
