package com.trevorwiebe.trackacow.db.holdingDao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugsGivenEntity;

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
