package com.trevorwiebe.trackacow.db.holdingDao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugEntity;

import java.util.List;

@Dao
public interface HoldingDrugDao {

    @Insert
    void insertHoldingDrug(HoldingDrugEntity holdingDrugEntity);

    @Insert
    void insertListHoldingDrug(List<HoldingDrugEntity> holdingDrugEntities);

    @Query("SELECT * FROM HoldingDrug WHERE drugId = :id")
    HoldingDrugEntity getHoldingDrugById(String id);

    @Query("SELECT * FROM HoldingDrug")
    List<HoldingDrugEntity> getHoldingDrugList();

    @Query("DELETE FROM HoldingDrug")
    void deleteHoldingDrugTable();

    @Update
    void updateDrug(HoldingDrugEntity holdingDrugEntity);

    @Delete
    void deleteDrug(HoldingDrugEntity holdingDrugEntity);


}
