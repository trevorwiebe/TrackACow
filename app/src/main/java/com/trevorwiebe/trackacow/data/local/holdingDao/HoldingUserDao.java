package com.trevorwiebe.trackacow.data.local.holdingDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingUserEntity;

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
