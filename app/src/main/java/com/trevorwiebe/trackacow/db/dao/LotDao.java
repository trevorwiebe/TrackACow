package com.trevorwiebe.trackacow.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.trackacow.db.entities.LotEntity;

import java.util.List;

@Dao
public interface LotDao {

    @Insert
    void insertLot(LotEntity lotEntity);

    @Insert
    void insertLotEntityList(List<LotEntity> lotEntities);

    @Query("SELECT * FROM lot")
    List<LotEntity> getLotEntityList();

    @Query("SELECT * FROM lot WHERE penId = :penId")
    List<LotEntity> getLotEntitiesByPenId(String penId);

    @Query("SELECT * FROM lot WHERE lotId = :lotId")
    LotEntity getLotEntityById(String lotId);

    @Update
    void updateLotEntity(LotEntity lotEntity);

    @Query("UPDATE lot SET lotName = :lotName, customerName = :customerName, totalHead = :totalHead, notes = :notes WHERE lotId = :lotId")
    void updateLotByFields(String lotName, String customerName, int totalHead, String notes, String lotId);

    @Query("UPDATE lot SET penId = :penId WHERE lotId = :lotId")
    void updateLotWithNewPenId(String lotId, String penId);

    @Query("DELETE FROM lot")
    void deleteLotEntityList();

    @Delete
    void deleteLotEntity(LotEntity lotEntity);

}
