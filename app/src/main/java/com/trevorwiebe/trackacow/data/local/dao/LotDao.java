package com.trevorwiebe.trackacow.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.local.entities.LotEntity;

import java.util.List;

@Dao
public interface LotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLot(LotEntity lotEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLotEntityList(List<LotEntity> lotEntities);

    @Query("SELECT * FROM lot")
    List<LotEntity> getLotEntityList();

    @Query("SELECT * FROM lot WHERE penId = :penId")
    List<LotEntity> getLotEntitiesByPenId(String penId);

    @Query("SELECT * FROM lot WHERE lotId = :lotId")
    LotEntity getLotEntityById(String lotId);

    @Update
    void updateLotEntity(LotEntity lotEntity);

    @Query("UPDATE lot SET lotName = :lotName, customerName = :customerName, date = :date, notes = :notes WHERE lotId = :lotId")
    void updateLotByFields(String lotName, String customerName, String notes, long date, String lotId);

    @Query("UPDATE lot SET penId = :penId WHERE lotId = :lotId")
    void updateLotWithNewPenId(String lotId, String penId);

    @Query("DELETE FROM lot")
    void deleteLotEntityTable();

    @Query("DELETE FROM lot WHERE lotId = :lotId")
    void deleteLotEntity(String lotId);

}
