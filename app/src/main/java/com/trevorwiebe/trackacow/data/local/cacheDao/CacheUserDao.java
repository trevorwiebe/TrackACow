package com.trevorwiebe.trackacow.data.local.cacheDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.cacheEntities.CacheUserEntity;

import java.util.List;


@Dao
public interface CacheUserDao {

    @Insert
    void insertHoldingUser(CacheUserEntity cacheUserEntity);

    @Query("SELECT * FROM holdingUser WHERE uid = :uid")
    CacheUserEntity getHoldingUserByUid(String uid);

    @Query("SELECT * FROM holdingUser")
    List<CacheUserEntity> getHoldingUserList();

    @Update
    void updateHoldingUser(CacheUserEntity cacheUserEntity);

    @Delete
    void deleteHoldingUser(CacheUserEntity cacheUserEntity);

    @Query("DELETE FROM holdingUser")
    void deleteHoldingUserTable();

}
