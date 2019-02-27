package com.trevorwiebe.trackacow.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

@Keep
@Entity(tableName = "DrugsGiven")
public class DrugsGivenEntity implements Parcelable {

    public static final String DRUGS_GIVEN = "drugsGiven";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryKey")
    public int primaryKey;

    @ColumnInfo(name = "drugGiveId")
    public String drugGivenId;

    @ColumnInfo(name = "drugId")
    public String drugId;

    @ColumnInfo(name = "amountGiven")
    public int amountGiven;

    @ColumnInfo(name = "cowId")
    public String cowId;

    @ColumnInfo(name = "penId")
    public String penId;

    public DrugsGivenEntity(String drugGivenId, String drugId, int amountGiven, String cowId, String penId) {
        this.drugGivenId = drugGivenId;
        this.drugId = drugId;
        this.amountGiven = amountGiven;
        this.cowId = cowId;
        this.penId = penId;
    }

    @Ignore
    public DrugsGivenEntity(){}

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getDrugGivenId() {
        return drugGivenId;
    }

    public void setDrugGivenId(String drugGivenId) {
        this.drugGivenId = drugGivenId;
    }

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.primaryKey);
        dest.writeString(this.drugGivenId);
        dest.writeString(this.drugId);
        dest.writeInt(this.amountGiven);
        dest.writeString(this.cowId);
        dest.writeString(this.penId);
    }

    protected DrugsGivenEntity(Parcel in) {
        this.primaryKey = in.readInt();
        this.drugGivenId = in.readString();
        this.drugId = in.readString();
        this.cowId = in.readString();
        this.penId = in.readString();
    }

    public static final Creator<DrugsGivenEntity> CREATOR = new Creator<DrugsGivenEntity>() {
        @Override
        public DrugsGivenEntity createFromParcel(Parcel source) {
            return new DrugsGivenEntity(source);
        }

        @Override
        public DrugsGivenEntity[] newArray(int size) {
            return new DrugsGivenEntity[size];
        }
    };
}
