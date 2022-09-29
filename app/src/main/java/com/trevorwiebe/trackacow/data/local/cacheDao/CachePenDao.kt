package com.trevorwiebe.trackacow.data.local.cacheDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.cacheEntities.CachePenEntity;

import java.util.List;

@Dao
public interface CachePenDao {

    @Insert
    void insertHoldingPen(CachePenEntity cachePenEntity);

    @Insert
    void insertHoldingPenList(List<CachePenEntity> holdingPenEntities);

    @Query("SELECT * FROM HoldingPen WHERE penId = :id")
    CachePenEntity getHoldingPenById(String id);

    @Query("SELECT * FROM HoldingPen")
    List<CachePenEntity> getHoldingPenList();

    @Query("DELETE FROM HoldingPen")
    void deleteHoldingPenTable();

    @Update
    void updateHoldingPen(CachePenEntity cachePenEntity);

    @Delete
    void deleteHoldingPen(CachePenEntity cachePenEntity);

}
