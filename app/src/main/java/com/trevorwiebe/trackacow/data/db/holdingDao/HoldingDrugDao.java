package com.trevorwiebe.trackacow.data.db.holdingDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.trevorwiebe.trackacow.data.db.holdingUpdateEntities.HoldingDrugEntity;

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

    @Query("DELETE FROM HoldingDrug WHERE drugId = :drugId")
    void deleteHoldingDrugById(String drugId);

    @Query("DELETE FROM HoldingDrug")
    void deleteHoldingDrugTable();

    @Update
    void updateDrug(HoldingDrugEntity holdingDrugEntity);

    @Delete
    void deleteDrug(HoldingDrugEntity holdingDrugEntity);


}
