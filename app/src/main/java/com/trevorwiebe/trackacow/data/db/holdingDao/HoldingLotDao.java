package com.trevorwiebe.trackacow.data.db.holdingDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.db.holdingUpdateEntities.HoldingLotEntity;

import java.util.List;

@Dao
public interface HoldingLotDao {

    @Insert
    void insertHoldingLot(HoldingLotEntity holdingLotEntity);

    @Query("SELECT * FROM holdingLot")
    List<HoldingLotEntity> getHoldingLotList();

    @Update
    void updateHoldingLot(HoldingLotEntity holdingLotEntity);

    @Delete
    void deleteHoldingLot(HoldingLotEntity holdingLotEntity);

    @Query("DELETE FROM holdingLot")
    void deleteHoldingLotTable();

}
