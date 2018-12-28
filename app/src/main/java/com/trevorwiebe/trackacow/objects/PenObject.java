package com.trevorwiebe.trackacow.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

@Keep
public class PenObject implements Parcelable {

    public static final String PEN_OBJECT = "pens";
    public static final String PEN_PEN_ID = "penId";
    public static final String PEN_PEN_NAME = "penName";
    public static final String PEN_CUSTOMER_ID = "customerId";

    private String penId;
    private String penName;
    private String customerId;

    public PenObject(String penId, String penName, String customerId) {
        this.penId = penId;
        this.penName = penName;
        this.customerId = customerId;
    }

    public PenObject(){}

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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.penId);
        dest.writeString(this.penName);
        dest.writeString(this.customerId);
    }

    protected PenObject(Parcel in) {
        this.penId = in.readString();
        this.penName = in.readString();
        this.customerId = in.readString();
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
