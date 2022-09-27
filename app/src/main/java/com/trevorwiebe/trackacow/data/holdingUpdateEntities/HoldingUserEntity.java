package com.trevorwiebe.trackacow.data.holdingUpdateEntities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.Keep;

import com.trevorwiebe.trackacow.data.entities.UserEntity;

@Keep
@Entity(tableName = "holdingUser")
public class HoldingUserEntity {

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

    @ColumnInfo(name = "whatHappened")
    private int whatHappened;

    public HoldingUserEntity(long dateCreated, int accountType, String name, String email, long renewalDate, String uid, int whatHappened) {
        this.dateCreated = dateCreated;
        this.accountType = accountType;
        this.name = name;
        this.email = email;
        this.renewalDate = renewalDate;
        this.uid = uid;
        this.whatHappened = whatHappened;
    }

    @Ignore
    public HoldingUserEntity(UserEntity userEntity, int whatHappened) {
        this.dateCreated = userEntity.getDateCreated();
        this.accountType = userEntity.getAccountType();
        this.name = userEntity.getName();
        this.email = userEntity.getEmail();
        this.renewalDate = userEntity.getRenewalDate();
        this.uid = userEntity.getUid();
        this.whatHappened = whatHappened;
    }

    @Ignore
    public HoldingUserEntity() {
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

    public int getWhatHappened() {
        return whatHappened;
    }

    public void setWhatHappened(int whatHappened) {
        this.whatHappened = whatHappened;
    }
}
