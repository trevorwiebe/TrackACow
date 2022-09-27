package com.trevorwiebe.trackacow.data.local.cacheDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.cacheEntities.CacheCallEntity;
import com.trevorwiebe.trackacow.data.entities.CallEntity;

import java.util.List;

@Dao
public interface CacheCallDao {

    @Insert
    void insertHoldingCall(CacheCallEntity cacheCallEntity);

    @Insert
    void insertHoldingCallList(List<CacheCallEntity> holdingCallEntities);

    @Query("SELECT * FROM holdingCall")
    List<CacheCallEntity> getHoldingCallEntities();

    @Query("DELETE FROM holdingCall")
    void deleteCallTable();

    @Update
    void updateCallEntity(CallEntity callEntity);

    @Delete
    void deleteCallEntity(CallEntity callEntity);

}
