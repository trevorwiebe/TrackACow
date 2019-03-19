package com.trevorwiebe.trackacow.db.holdingDao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLotEntity;

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

}
