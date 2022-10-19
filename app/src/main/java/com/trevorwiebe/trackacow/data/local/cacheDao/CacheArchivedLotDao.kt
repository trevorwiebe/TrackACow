package com.trevorwiebe.trackacow.data.local.cacheDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.cacheEntities.CacheArchivedLotEntity;

import java.util.List;

@Dao
public interface CacheArchivedLotDao {

    @Insert
    void insertHoldingArchivedLot(CacheArchivedLotEntity cacheArchivedLotEntity);

    @Query("SELECT * FROM holdingArchivedLot")
    List<CacheArchivedLotEntity> getHoldingArchivedLotList();

    @Update
    void updateHoldingArchivedLot(CacheArchivedLotEntity cacheArchivedLotEntity);

    @Delete
    void deleteHoldingArchivedLot(CacheArchivedLotEntity cacheArchivedLotEntity);

    @Query("DELETE FROM holdingArchivedLot")
    void deleteHoldingArchivedLotTable();

}
