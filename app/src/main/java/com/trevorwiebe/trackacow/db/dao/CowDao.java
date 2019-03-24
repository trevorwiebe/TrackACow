package com.trevorwiebe.trackacow.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.trackacow.db.entities.CowEntity;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface CowDao {

    @Insert
    void insertCow(CowEntity cowEntity);

    @Insert
    void insertCowList(List<CowEntity> cowEntityList);

    @Query("SELECT * FROM Cow WHERE cowId = :id")
    CowEntity getCowById(String id);

    @Query("SELECT * FROM Cow WHERE lotId IN(:ids)")
    List<CowEntity> getCowEntitiesByLotIds(List<String> ids);

    @Query("SELECT * FROM Cow WHERE lotId IN(:ids) AND isAlive = 0")
    List<CowEntity> getDeadCowEntitiesByLotIds(List<String> ids);

    @Query("UPDATE Cow SET tagNumber = :tagNumber, date = :date, notes =:notes WHERE cowId = :id")
    void updateCowById(String id, int tagNumber, long date, String notes);

    @Query("DELETE FROM Cow WHERE lotId = :lotId")
    void deleteCowsByLotId(String lotId);

    @Query("DELETE FROM cow")
    void deleteCowTable();

    @Update
    void updateCow(CowEntity cowEntity);

    @Delete
    void deleteCow(CowEntity cowEntity);
}
