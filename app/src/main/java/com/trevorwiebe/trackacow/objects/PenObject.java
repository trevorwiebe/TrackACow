package com.trevorwiebe.trackacow.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

@Keep
public class PenObject implements Parcelable {

    public static final String PEN_OBJECT = "pens";
    public static final String PEN_PEN_ID = "penId";
    public static final String PEN_PEN_NAME = "penName";
    public static final String PEN_CUSTOMER_NAME = "customerName";
    public static final String PEN_TOTAL_HEAD = "totalHead";
    public static final String PEN_IS_ACTIVE = "active";

    private String penId;
    private String penName;
    private String customerName;
    private int totalHead;
    private String notes;
    private boolean isActive;

    public PenObject(){}

    public PenObject(String penId, String penName, String customerName, int totalHead, String notes, boolean isActive) {
        this.penId = penId;
        this.penName = penName;
        this.customerName = customerName;
        this.totalHead = totalHead;
        this.notes = notes;
        this.isActive = isActive;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getTotalHead() {
        return totalHead;
    }

    public void setTotalHead(int totalHead) {
        this.totalHead = totalHead;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.penId);
        dest.writeString(this.penName);
        dest.writeString(this.customerName);
        dest.writeInt(this.totalHead);
        dest.writeString(this.notes);
        dest.writeByte(this.isActive ? (byte) 1 : (byte) 0);
    }

    protected PenObject(Parcel in) {
        this.penId = in.readString();
        this.penName = in.readString();
        this.customerName = in.readString();
        this.totalHead = in.readInt();
        this.notes = in.readString();
        this.isActive = in.readByte() != 0;
    }

    public static final Creator<PenObject> CREATOR = new Creator<PenObject>() {
        @Override
        public PenObject createFromParcel(Parcel source) {
            return new PenObject(source);
        }

        @Override
        public PenObject[] newArray(int size) {
            return new PenObject[size];
        }
    };
}
