package com.trevorwiebe.trackacow.data.db.holdingUpdateEntities;

import androidx.annotation.Keep;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.trevorwiebe.trackacow.data.db.entities.CallEntity;

@Keep
@Entity(tableName = "holdingCall")
public class HoldingCallEntity {

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

    @ColumnInfo(name = "whatHappened")
    private int whatHappened;

    public HoldingCallEntity(int callAmount, long date, String lotId, String id, int whatHappened) {
        this.callAmount = callAmount;
        this.date = date;
        this.lotId = lotId;
        this.id = id;
        this.whatHappened = whatHappened;
    }

    @Ignore
    public HoldingCallEntity(CallEntity callEntity, int whatHappened){
        this.primaryKey = callEntity.getPrimaryKey();
        this.callAmount = callEntity.getCallAmount();
        this.date = callEntity.getDate();
        this.lotId = callEntity.getLotId();
        this.id = callEntity.getId();
        this.whatHappened = whatHappened;
    }

    @Ignore
    public HoldingCallEntity() {
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

    public int getWhatHappened() {
        return whatHappened;
    }

    public void setWhatHappened(int whatHappened) {
        this.whatHappened = whatHappened;
    }
}
