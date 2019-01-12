package com.trevorwiebe.trackacow.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

@Keep
public class DrugObject implements Parcelable {

    public static final String DRUG_OBJECT = "drugs";

    private String drugId;
    private String drugName;
    private int defaultAmount;

    public DrugObject(String drugId, String drugName, int defaultAmount) {
        this.drugId = drugId;
        this.drugName = drugName;
        this.defaultAmount = defaultAmount;
    }

    public DrugObject(){}

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public int getDefaultAmount() {
        return defaultAmount;
    }

    public void setDefaultAmount(int defaultAmount) {
        this.defaultAmount = defaultAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.drugId);
        dest.writeString(this.drugName);
        dest.writeInt(this.defaultAmount);
    }

    protected DrugObject(Parcel in) {
        this.drugId = in.readString();
        this.drugName = in.readString();
        this.defaultAmount = in.readInt();
    }

    public static final Creator<DrugObject> CREATOR = new Creator<DrugObject>() {
        @Override
        public DrugObject createFromParcel(Parcel source) {
            return new DrugObject(source);
        }

        @Override
        public DrugObject[] newArray(int size) {
            return new DrugObject[size];
        }
    };
}
