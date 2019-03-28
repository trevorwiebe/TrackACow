package com.trevorwiebe.trackacow.db.holdingDao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingArchivedLotEntity;

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
