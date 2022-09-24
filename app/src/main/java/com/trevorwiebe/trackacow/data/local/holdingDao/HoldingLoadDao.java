package com.trevorwiebe.trackacow.data.local.holdingDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.local.holdingUpdateEntities.HoldingLoadEntity;

import java.util.List;

@Dao
public interface HoldingLoadDao {

    @Insert
    void insertHoldingLoad(HoldingLoadEntity holdingLoadEntity);

    @Query("SELECT * FROM holdingLoad")
    List<HoldingLoadEntity> getHoldingLoadList();

    @Update()
    void updateHoldingLoad(HoldingLoadEntity holdingLoadEntity);

    @Delete
    void deleteHoldingLoad(HoldingLoadEntity holdingLoadEntity);

    @Query("DELETE FROM holdingLoad")
    void deleteHoldingLoadTable();

}
