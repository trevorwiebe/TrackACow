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
public class PenEntity implements Parcelable {

    public static final String PEN_OBJECT = "pens";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    public int primaryKey;

    @ColumnInfo(name = "penId")
    public String penId;

    @ColumnInfo(name = "customerName")
    public String customerName;

    @ColumnInfo(name = "isActive")
    public int isActive;

    @ColumnInfo(name = "notes")
    public String notes;

    @ColumnInfo(name = "penName")
    public String penName;

    @ColumnInfo(name = "totalHead")
    public int totalHead;

    public PenEntity(String penId, String customerName, int isActive, String notes, String penName, int totalHead) {
        this.penId = penId;
        this.customerName = customerName;
        this.isActive = isActive;
        this.notes = notes;
        this.penName = penName;
        this.totalHead = totalHead;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPenName() {
        return penName;
    }

    public void setPenName(String penName) {
        this.penName = penName;
    }

    public int getTotalHead() {
        return totalHead;
    }

    public void setTotalHead(int totalHead) {
        this.totalHead = totalHead;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.primaryKey);
        dest.writeString(this.penId);
        dest.writeString(this.customerName);
        dest.writeInt(this.isActive);
        dest.writeString(this.notes);
        dest.writeString(this.penName);
        dest.writeInt(this.totalHead);
    }

    protected PenEntity(Parcel in) {
        this.primaryKey = in.readInt();
        this.penId = in.readString();
        this.customerName = in.readString();
        this.isActive = in.readInt();
        this.notes = in.readString();
        this.penName = in.readString();
        this.totalHead = in.readInt();
    }

    public static final Creator<PenEntity> CREATOR = new Creator<PenEntity>() {
        @Override
        public PenEntity createFromParcel(Parcel source) {
            return new PenEntity(source);
        }

        @Override
        public PenEntity[] newArray(int size) {
            return new PenEntity[size];
        }
    };
}
