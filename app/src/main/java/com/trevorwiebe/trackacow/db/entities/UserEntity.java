package com.trevorwiebe.trackacow.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Keep;

import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingUserEntity;

@Keep
@Entity(tableName = "user")
public class UserEntity {

    public static final String USER = "user";

    public static final int FREE_TRIAL = 0;
    public static final int MONTHLY_SUBSCRIPTION = 1;
    public static final int ANNUAL_SUBSCRIPTION = 2;
    public static final int CANCELED = 6;
    public static final int FOREVER_FREE_USER = 7;

    @ColumnInfo(name = "primaryKey")
    @PrimaryKey(autoGenerate = true)
    private int primaryKey;

    @ColumnInfo(name = "accountType")
    private int accountType;

    @ColumnInfo(name = "dateCreated")
    private long dateCreated;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "renewalDate")
    private long renewalDate;

    @ColumnInfo(name = "uid")
    private String uid;

    public UserEntity(long dateCreated, int accountType, String name, String email, long renewalDate, String uid) {
        this.dateCreated = dateCreated;
        this.accountType = accountType;
        this.name = name;
        this.email = email;
        this.renewalDate = renewalDate;
        this.uid = uid;
    }

    @Ignore
    public UserEntity(HoldingUserEntity holdingUserEntity) {
        this.dateCreated = holdingUserEntity.getDateCreated();
        this.accountType = holdingUserEntity.getAccountType();
        this.name = holdingUserEntity.getName();
        this.email = holdingUserEntity.getEmail();
        this.renewalDate = holdingUserEntity.getRenewalDate();
        this.uid = holdingUserEntity.getUid();
    }

    @Ignore
    public UserEntity() {
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(long renewalDate) {
        this.renewalDate = renewalDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
