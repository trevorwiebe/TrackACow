package com.trevorwiebe.trackacow.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DrugsGivenDao {

    @Insert
    void insertDrugsGiven(DrugsGivenEntity drugsGivenEntity);

    @Insert
    void insertDrugsGivenList(List<DrugsGivenEntity> drugsGivenEntities);

    @Query("SELECT * FROM DrugsGiven WHERE cowId = :cowId")
    List<DrugsGivenEntity> getDrugsGivenByCowId(String cowId);

    @Query("SELECT * FROM DrugsGiven WHERE cowId IN(:cowIdList)")
    List<DrugsGivenEntity> getDrugsGivenByCowIdList(List<String> cowIdList);

    @Query("SELECT * FROM DrugsGiven WHERE penId = :penId")
    List<DrugsGivenEntity> getDrugsGivenByPenId(String penId);

    @Query("SELECT * FROM DrugsGiven")
    List<DrugsGivenEntity> getDrugsGivenList();

    @Query("UPDATE DrugsGiven SET amountGiven = :amountGiven WHERE drugGiveId = :drugGivenId")
    void updateDrugGivenAmountGiven(int amountGiven, String drugGivenId);

    @Query("DELETE FROM DrugsGiven WHERE penId = :penId")
    void deleteDrugsGivenByPenId(String penId);

    @Query("DELETE FROM DrugsGiven WHERE cowId = :cowId")
    void deleteDrugsGivenByCowId(String cowId);

    @Query("DELETE FROM DrugsGiven WHERE drugId = :drugId")
    void deleteDrugGivenById(String drugId);

    @Query("DELETE FROM DrugsGiven")
    void deleteDrugsGivenTable();

    @Update
    void updateDrugsGiven(DrugsGivenEntity drugsGivenEntity);

    @Delete
    void deleteDrugsGiven(DrugsGivenEntity drugsGivenEntity);

}
