package com.trevorwiebe.trackacow.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import com.google.firebase.database.annotations.NotNull;

@Keep
@Entity(tableName = "Pen")
public class PenEntity {

    public static final String PEN_OBJECT = "pens";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    public int primaryKey;

    @ColumnInfo(name = "penId")
    public String penId;

    @ColumnInfo(name = "penName")
    public String penName;

    public PenEntity(String penId, String penName) {
        this.penId = penId;
        this.penName = penName;
    }

    @Ignore
    public PenEntity(){}

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getPenId() {
        return penId;
    }

    public void setPenId(String penId) {
        this.penId = penId;
    }

    public String getPenName() {
        return penName;
    }

    public void setPenName(String penName) {
        this.penName = penName;
    }

}
