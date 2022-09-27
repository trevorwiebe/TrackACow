package com.trevorwiebe.trackacow.data.local.cacheDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugEntity;

import java.util.List;

@Dao
public interface CacheDrugDao {

    @Insert
    void insertHoldingDrug(CacheDrugEntity cacheDrugEntity);

    @Insert
    void insertListHoldingDrug(List<CacheDrugEntity> holdingDrugEntities);

    @Query("SELECT * FROM HoldingDrug WHERE drugId = :id")
    CacheDrugEntity getHoldingDrugById(String id);

    @Query("SELECT * FROM HoldingDrug")
    List<CacheDrugEntity> getHoldingDrugList();

    @Query("DELETE FROM HoldingDrug WHERE drugId = :drugId")
    void deleteHoldingDrugById(String drugId);

    @Query("DELETE FROM HoldingDrug")
    void deleteHoldingDrugTable();

    @Update
    void updateDrug(CacheDrugEntity cacheDrugEntity);

    @Delete
    void deleteDrug(CacheDrugEntity cacheDrugEntity);


}
