package com.trevorwiebe.trackacow.data.local.holdingDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingDrugsGivenEntity;

import java.util.List;

@Dao
public interface HoldingDrugsGivenDao {

    @Insert
    void insertHoldingDrugsGiven(HoldingDrugsGivenEntity holdingDrugsGivenEntity);

    @Insert
    void insertHoldingDrugsGivenList(List<HoldingDrugsGivenEntity> holdingDrugsGivenEntities);

    @Query("SELECT * FROM HoldingDrugsGiven WHERE cowId = :cowId")
    List<HoldingDrugsGivenEntity> getHoldingDrugsGivenByCowId(String cowId);

    @Query("SELECT * FROM HoldingDrugsGiven")
    List<HoldingDrugsGivenEntity> getHoldingDrugsGivenList();

    @Query("DELETE FROM HoldingDrugsGiven")
    void deleteHoldingDrugsGivenTable();

    @Update
    void updateHoldingDrugsGiven(HoldingDrugsGivenEntity holdingDrugsGivenEntity);

    @Delete
    void deleteHoldingDrugsGiven(HoldingDrugsGivenEntity holdingDrugsGivenEntity);

}
