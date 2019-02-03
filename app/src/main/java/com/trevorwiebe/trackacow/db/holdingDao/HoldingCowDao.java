package com.trevorwiebe.trackacow.db.holdingDao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingCowEntity;

import java.util.List;

@Dao
public interface HoldingCowDao {

    @Insert
    void insertHoldingCow(HoldingCowEntity holdingCowEntity);

    @Insert
    void insertHoldingCowList(List<HoldingCowEntity> holdingCowEntityList);

    @Query("SELECT * FROM HoldingCow WHERE cowId = :id")
    HoldingCowEntity getHoldingCowById(String id);

    @Query("SELECT * FROM HoldingCow")
    List<HoldingCowEntity> getHoldingCowEntityList();

    @Query("DELETE FROM HoldingCow")
    void deleteHoldingCowTable();

    @Update
    void updateHoldingCow(HoldingCowEntity holdingCowEntity);

    @Delete
    void deleteCow(HoldingCowEntity holdingCowEntity);

}
