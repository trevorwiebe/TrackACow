package com.trevorwiebe.trackacow.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

@Keep
@Entity(tableName = "Drug")
public class DrugEntity implements Parcelable {

    public static final String DRUG_OBJECT = "drugs";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    public int primaryKey;

    @ColumnInfo(name = "defaultAmount")
    public int defaultAmount;

    @ColumnInfo(name = "drugId")
    public String drugId;

    @ColumnInfo(name = "drugName")
    public String drugName;

    public DrugEntity(int defaultAmount, String drugId, String drugName){
        this.defaultAmount = defaultAmount;
        this.drugId = drugId;
        this.drugName = drugName;
    }

    @Ignore
    public DrugEntity() {}

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int getDefaultAmount() {
        return defaultAmount;
    }

    public void setDefaultAmount(int defaultAmount) {
        this.defaultAmount = defaultAmount;
    }

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.primaryKey);
        dest.writeInt(this.defaultAmount);
        dest.writeString(this.drugId);
        dest.writeString(this.drugName);
    }

    protected DrugEntity(Parcel in) {
        this.primaryKey = in.readInt();
        this.defaultAmount = in.readInt();
        this.drugId = in.readString();
        this.drugName = in.readString();
    }

    public static final Creator<DrugEntity> CREATOR = new Creator<DrugEntity>() {
        @Override
        public DrugEntity createFromParcel(Parcel source) {
            return new DrugEntity(source);
        }

        @Override
        public DrugEntity[] newArray(int size) {
            return new DrugEntity[size];
        }
    };
}
