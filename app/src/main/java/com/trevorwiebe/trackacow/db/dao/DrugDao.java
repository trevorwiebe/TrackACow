package com.trevorwiebe.trackacow.db.dao;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.trackacow.db.entities.DrugEntity;

import java.util.List;

@Dao
public interface DrugDao {

    @Insert
    void insertDrug(DrugEntity drugEntity);

    @Insert
    void insertListDrug(List<DrugEntity> drugEntities);

    @Query("SELECT * FROM Drug WHERE drugId = :id")
    DrugEntity getDrugById(String id);

    @Query("SELECT * FROM Drug")
    List<DrugEntity> getDrugList();

    @Query("UPDATE Drug SET defaultAmount = :defaultAmount, drugName = :drugName WHERE drugId = :drugId")
    void updateDrugById(int defaultAmount, String drugName, String drugId);

    @Query("DELETE FROM Drug")
    void deleteDrugTable();

    @Query("DELETE FROM Drug WHERE drugId = :drugId")
    void deleteDrugById(String drugId);

    @Update
    void updateDrug(DrugEntity drugEntity);

    @Delete
    void deleteDrug(DrugEntity drugEntity);

}
