package com.trevorwiebe.trackacow.data.local.cacheDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.cacheEntities.CacheLotEntity;

import java.util.List;

@Dao
public interface CacheLotDao {

    @Insert
    void insertHoldingLot(CacheLotEntity cacheLotEntity);

    @Query("SELECT * FROM holdingLot")
    List<CacheLotEntity> getHoldingLotList();

    @Update
    void updateHoldingLot(CacheLotEntity cacheLotEntity);

    @Delete
    void deleteHoldingLot(CacheLotEntity cacheLotEntity);

    @Query("DELETE FROM holdingLot")
    void deleteHoldingLotTable();

}
