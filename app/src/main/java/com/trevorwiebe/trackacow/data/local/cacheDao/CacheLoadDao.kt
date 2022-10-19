package com.trevorwiebe.trackacow.data.local.cacheDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.cacheEntities.CacheLoadEntity;

import java.util.List;

@Dao
public interface CacheLoadDao {

    @Insert
    void insertHoldingLoad(CacheLoadEntity cacheLoadEntity);

    @Query("SELECT * FROM holdingLoad")
    List<CacheLoadEntity> getHoldingLoadList();

    @Update()
    void updateHoldingLoad(CacheLoadEntity cacheLoadEntity);

    @Delete
    void deleteHoldingLoad(CacheLoadEntity cacheLoadEntity);

    @Query("DELETE FROM holdingLoad")
    void deleteHoldingLoadTable();

}
