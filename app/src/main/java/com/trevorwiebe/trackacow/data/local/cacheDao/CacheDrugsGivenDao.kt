package com.trevorwiebe.trackacow.data.local.cacheDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugsGivenEntity;

import java.util.List;

@Dao
public interface CacheDrugsGivenDao {

    @Insert
    void insertHoldingDrugsGiven(CacheDrugsGivenEntity cacheDrugsGivenEntity);

    @Insert
    void insertHoldingDrugsGivenList(List<CacheDrugsGivenEntity> holdingDrugsGivenEntities);

    @Query("SELECT * FROM HoldingDrugsGiven WHERE cowId = :cowId")
    List<CacheDrugsGivenEntity> getHoldingDrugsGivenByCowId(String cowId);

    @Query("SELECT * FROM HoldingDrugsGiven")
    List<CacheDrugsGivenEntity> getHoldingDrugsGivenList();

    @Query("DELETE FROM HoldingDrugsGiven")
    void deleteHoldingDrugsGivenTable();

    @Update
    void updateHoldingDrugsGiven(CacheDrugsGivenEntity cacheDrugsGivenEntity);

    @Delete
    void deleteHoldingDrugsGiven(CacheDrugsGivenEntity cacheDrugsGivenEntity);

}
