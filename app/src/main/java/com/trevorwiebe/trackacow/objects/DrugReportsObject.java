package com.trevorwiebe.trackacow.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class DrugReportsObject implements Parcelable {

    private String drugId;
    private int drugAmount;

    public DrugReportsObject(String drugId, int drugAmount) {
        this.drugAmount = drugAmount;
        this.drugId = drugId;
    }

    protected DrugReportsObject(Parcel in) {
        drugId = in.readString();
        drugAmount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(drugId);
        dest.writeInt(drugAmount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DrugReportsObject> CREATOR = new Creator<DrugReportsObject>() {
        @Override
        public DrugReportsObject createFromParcel(Parcel in) {
            return new DrugReportsObject(in);
        }

        @Override
        public DrugReportsObject[] newArray(int size) {
            return new DrugReportsObject[size];
        }
    };

    public int getDrugAmount() {
        return drugAmount;
    }

    public void setDrugAmount(int drugAmount) {
        this.drugAmount = drugAmount;
    }

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

}
