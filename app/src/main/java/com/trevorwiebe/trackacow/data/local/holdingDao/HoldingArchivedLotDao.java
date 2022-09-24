package com.trevorwiebe.trackacow.data.local.holdingDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.local.holdingUpdateEntities.HoldingArchivedLotEntity;

import java.util.List;

@Dao
public interface HoldingArchivedLotDao {

    @Insert
    void insertHoldingArchivedLot(HoldingArchivedLotEntity holdingArchivedLotEntity);

    @Query("SELECT * FROM holdingArchivedLot")
    List<HoldingArchivedLotEntity> getHoldingArchivedLotList();

    @Update
    void updateHoldingArchivedLot(HoldingArchivedLotEntity holdingArchivedLotEntity);

    @Delete
    void deleteHoldingArchivedLot(HoldingArchivedLotEntity holdingArchivedLotEntity);

    @Query("DELETE FROM holdingArchivedLot")
    void deleteHoldingArchivedLotTable();

}
