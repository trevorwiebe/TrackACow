package com.trevorwiebe.trackacow.db.holdingDao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingUserEntity;

import java.util.List;


@Dao
public interface HoldingUserDao {

    @Insert
    void insertHoldingUser(HoldingUserEntity holdingUserEntity);

    @Query("SELECT * FROM holdingUser WHERE uid = :uid")
    HoldingUserEntity getHoldingUserByUid(String uid);

    @Query("SELECT * FROM holdingUser")
    List<HoldingUserEntity> getHoldingUserList();

    @Update
    void updateHoldingUser(HoldingUserEntity holdingUserEntity);

    @Delete
    void deleteHoldingUser(HoldingUserEntity holdingUserEntity);

    @Query("DELETE FROM holdingUser")
    void deleteHoldingUserTable();

}
