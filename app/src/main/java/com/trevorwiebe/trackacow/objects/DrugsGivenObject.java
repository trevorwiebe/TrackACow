package com.trevorwiebe.trackacow.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

@Keep
public class DrugsGivenObject implements Parcelable {

    private String drugId;
    private int amountGiven;
    private long date;

    public DrugsGivenObject(String drugId, int amountGiven, long date) {
        this.drugId = drugId;
        this.amountGiven = amountGiven;
        this.date = date;
    }

    public DrugsGivenObject(){}

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public int getAmountGiven() {
        return amountGiven;
    }

    public void setAmountGiven(int amountGiven) {
        this.amountGiven = amountGiven;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.drugId);
        dest.writeInt(this.amountGiven);
        dest.writeLong(this.date);
    }

    protected DrugsGivenObject(Parcel in) {
        this.drugId = in.readString();
        this.amountGiven = in.readInt();
        this.date = in.readLong();
    }

    public static final Creator<DrugsGivenObject> CREATOR = new Creator<DrugsGivenObject>() {
        @Override
        public DrugsGivenObject createFromParcel(Parcel source) {
            return new DrugsGivenObject(source);
        }

        @Override
        public DrugsGivenObject[] newArray(int size) {
            return new DrugsGivenObject[size];
        }
    };
}
