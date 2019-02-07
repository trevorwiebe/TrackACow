package com.trevorwiebe.trackacow.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "Cow")
public class CowEntity implements Parcelable {

    public static final String COW = "cows";
    public static final String COW_NUMBER = "cowNumber";
    public static final String COW_ID = "cowId";
    public static final String PEN_ID = "penId";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    private int primaryKey;

    @ColumnInfo(name = "isAlive")
    private int isAlive;

    @ColumnInfo(name = "cowId")
    private String cowId;

    @ColumnInfo(name = "tagNumber")
    private int tagNumber;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "penId")
    private String penId;

    public CowEntity(int isAlive, String cowId, int tagNumber, long date, String notes, String penId) {
        this.isAlive = isAlive;
        this.cowId = cowId;
        this.tagNumber = tagNumber;
        this.date = date;
        this.notes = notes;
        this.penId = penId;
    }

    @Ignore
    public CowEntity(){}

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int isAlive() {
        return isAlive;
    }

    public void setAlive(int alive) {
        isAlive = alive;
    }

    public void setCowId(String cowId) {
        this.cowId = cowId;
    }

    public String getCowId() {
        return cowId;
    }

    public int getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(int tagNumber) {
        this.tagNumber = tagNumber;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPenId() {
        return penId;
    }

    public void setPenId(String penId) {
        this.penId = penId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.primaryKey);
        dest.writeInt(this.isAlive);
        dest.writeString(this.cowId);
        dest.writeInt(this.tagNumber);
        dest.writeLong(this.date);
        dest.writeString(this.notes);
        dest.writeString(this.penId);
    }

    protected CowEntity(Parcel in) {
        this.primaryKey = in.readInt();
        this.isAlive = in.readInt();
        this.cowId = in.readString();
        this.tagNumber = in.readInt();
        this.date = in.readLong();
        this.notes = in.readString();
        this.penId = in.readString();
    }

    public static final Creator<CowEntity> CREATOR = new Creator<CowEntity>() {
        @Override
        public CowEntity createFromParcel(Parcel source) {
            return new CowEntity(source);
        }

        @Override
        public CowEntity[] newArray(int size) {
            return new CowEntity[size];
        }
    };
}
