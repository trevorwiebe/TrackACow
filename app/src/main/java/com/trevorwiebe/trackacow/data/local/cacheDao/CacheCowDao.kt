package com.trevorwiebe.trackacow.data.local.cacheDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.cacheEntities.CacheCowEntity;

import java.util.List;

@Dao
public interface CacheCowDao {

    @Insert
    void insertHoldingCow(CacheCowEntity cacheCowEntity);

    @Insert
    void insertHoldingCowList(List<CacheCowEntity> cacheCowEntityList);

    @Query("SELECT * FROM HoldingCow WHERE cowId = :id")
    CacheCowEntity getHoldingCowById(String id);

    @Query("SELECT * FROM HoldingCow")
    List<CacheCowEntity> getHoldingCowEntityList();

    @Query("DELETE FROM HoldingCow")
    void deleteHoldingCowTable();

    @Update
    void updateHoldingCow(CacheCowEntity cacheCowEntity);

    @Delete
    void deleteCow(CacheCowEntity cacheCowEntity);

}
