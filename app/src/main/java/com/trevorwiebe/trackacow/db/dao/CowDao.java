package com.trevorwiebe.trackacow.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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

    @Query("SELECT * FROM Cow WHERE penId = :id")
    List<CowEntity> getCowEntitiesByPenId(String id);

    @Query("SELECT * FROM Cow WHERE isAlive = 0 AND penId = :id")
    List<CowEntity> getDeadCowEntitiesByPenId(String id);

    @Query("UPDATE Cow SET tagNumber = :tagNumber, notes =:notes WHERE cowId = :id")
    void updateCowById(String id, int tagNumber, String notes);

    @Query("DELETE FROM Cow WHERE penId = :penId")
    void deleteCowsByPenId(String penId);

    @Query("DELETE FROM cow")
    void deleteCowTable();

    @Update
    void updateCow(CowEntity cowEntity);

    @Delete
    void deleteCow(CowEntity cowEntity);
}
