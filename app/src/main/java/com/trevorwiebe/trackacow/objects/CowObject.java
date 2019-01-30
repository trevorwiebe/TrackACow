package com.trevorwiebe.trackacow.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import java.util.ArrayList;

@Keep
public class CowObject implements Parcelable {

    public static final String COW = "cows";
    public static final String COW_NUMBER = "cowNumber";
    public static final String COW_ID = "cowId";
    public static final String PEN_ID = "penId";

    private int cowNumber;
    private String cowId;
    private String penId;
    private String notes;
    private boolean isAlive;
    private long date;
    private ArrayList<DrugsGivenObject> mDrugList = new ArrayList<>();

    public CowObject (){}

    public CowObject(int cowNumber, String cowId, String penId, String notes, boolean isAlive, long date, ArrayList<DrugsGivenObject> mDrugList) {
        this.cowNumber = cowNumber;
        this.cowId = cowId;
        this.penId = penId;
        this.notes = notes;
        this.isAlive = isAlive;
        this.date = date;
        this.mDrugList = mDrugList;
    }

    public int getCowNumber() {
        return cowNumber;
    }

    public void setCowNumber(int cowNumber) {
        this.cowNumber = cowNumber;
    }

    public String getCowId() {
        return cowId;
    }

    public void setCowId(String cowId) {
        this.cowId = cowId;
    }

    public String getPenId() {
        return penId;
    }

    public void setPenId(String penId) {
        this.penId = penId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public ArrayList<DrugsGivenObject> getmDrugList() {
        return mDrugList;
    }

    public void setmDrugList(ArrayList<DrugsGivenObject> mDrugList) {
        this.mDrugList = mDrugList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.cowNumber);
        dest.writeString(this.cowId);
        dest.writeString(this.penId);
        dest.writeString(this.notes);
        dest.writeByte(this.isAlive ? (byte) 1 : (byte) 0);
        dest.writeLong(this.date);
        dest.writeList(this.mDrugList);
    }

    protected CowObject(Parcel in) {
        this.cowNumber = in.readInt();
        this.cowId = in.readString();
        this.penId = in.readString();
        this.notes = in.readString();
        this.isAlive = in.readByte() != 0;
        this.date = in.readLong();
        this.mDrugList = new ArrayList<DrugsGivenObject>();
        in.readList(this.mDrugList, DrugsGivenObject.class.getClassLoader());
    }

    public static final Creator<CowObject> CREATOR = new Creator<CowObject>() {
        @Override
        public CowObject createFromParcel(Parcel source) {
            return new CowObject(source);
        }

        @Override
        public CowObject[] newArray(int size) {
            return new CowObject[size];
        }
    };
}
