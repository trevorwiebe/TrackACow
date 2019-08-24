package com.trevorwiebe.trackacow.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;

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

    @Query("SELECT * FROM DrugsGiven WHERE lotId IN(:lotIds)")
    List<DrugsGivenEntity> getDrugsGivenByLotIds(List<String> lotIds);

    @Query("SELECT * FROM DrugsGiven WHERE drugGiveId = :drugGivenId")
    DrugsGivenEntity getDrugGivenByDrugGivenId(String drugGivenId);

    @Query("SELECT * FROM DrugsGiven")
    List<DrugsGivenEntity> getDrugsGivenList();

    @Query("UPDATE DrugsGiven SET amountGiven = :amountGiven WHERE drugGiveId = :drugGivenId")
    void updateDrugGivenAmountGiven(int amountGiven, String drugGivenId);

    @Query("DELETE FROM DrugsGiven WHERE lotId = :lotId")
    void deleteDrugsGivenByLotId(String lotId);

    @Query("DELETE FROM DrugsGiven WHERE cowId = :cowId")
    void deleteDrugsGivenByCowId(String cowId);

    @Query("DELETE FROM DrugsGiven WHERE drugGiveId = :drugId")
    void deleteDrugGivenById(String drugId);

    @Query("DELETE FROM DrugsGiven")
    void deleteDrugsGivenTable();

    @Update
    void updateDrugsGiven(DrugsGivenEntity drugsGivenEntity);

    @Delete
    void deleteDrugsGiven(DrugsGivenEntity drugsGivenEntity);

}
