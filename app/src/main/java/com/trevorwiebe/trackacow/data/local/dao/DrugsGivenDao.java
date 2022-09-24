package com.trevorwiebe.trackacow.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.local.entities.DrugsGivenEntity;

import java.util.List;

@Dao
public interface DrugsGivenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDrugsGiven(DrugsGivenEntity drugsGivenEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDrugsGivenList(List<DrugsGivenEntity> drugsGivenEntities);

    @Query("SELECT * FROM DrugsGiven WHERE cowId = :cowId")
    List<DrugsGivenEntity> getDrugsGivenByCowId(String cowId);

    @Query("SELECT * FROM DrugsGiven WHERE cowId IN(:cowIdList)")
    List<DrugsGivenEntity> getDrugsGivenByCowIdList(List<String> cowIdList);

    @Query("SELECT * FROM DrugsGiven WHERE lotId IN(:lotIds)")
    List<DrugsGivenEntity> getDrugsGivenByLotIds(List<String> lotIds);

    @Query("SELECT * FROM DrugsGiven WHERE drugGivenId = :drugGivenId")
    DrugsGivenEntity getDrugGivenByDrugGivenId(String drugGivenId);

    @Query("SELECT * FROM DrugsGiven WHERE (lotId = :lotId) AND (date BETWEEN :startDate AND :endDate)")
    List<DrugsGivenEntity> getDrugsGivenByLotIdAndDateRange(String lotId, long startDate, long endDate);

    @Query("UPDATE DrugsGiven SET amountGiven = :amountGiven WHERE drugGivenId = :drugGivenId")
    void updateDrugGivenAmountGiven(int amountGiven, String drugGivenId);

    @Query("UPDATE DrugsGiven Set date = :date WHERE cowId = :cowId")
    void updateDrugsGivenDateByCowId(long date, String cowId);

    @Query("DELETE FROM DrugsGiven WHERE lotId = :lotId")
    void deleteDrugsGivenByLotId(String lotId);

    @Query("DELETE FROM DrugsGiven WHERE cowId = :cowId")
    void deleteDrugsGivenByCowId(String cowId);

    @Query("DELETE FROM DrugsGiven WHERE drugGivenId = :drugId")
    void deleteDrugGivenById(String drugId);

    @Query("DELETE FROM DrugsGiven")
    void deleteDrugsGivenTable();

    @Update
    void updateDrugsGiven(DrugsGivenEntity drugsGivenEntity);

    @Delete
    void deleteDrugsGiven(DrugsGivenEntity drugsGivenEntity);

}
