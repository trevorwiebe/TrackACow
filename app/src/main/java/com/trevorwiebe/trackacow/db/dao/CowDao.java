package com.trevorwiebe.trackacow.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.db.entities.CowEntity;

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
