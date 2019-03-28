package com.trevorwiebe.trackacow.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.trackacow.db.entities.ArchivedLotEntity;

import java.util.List;

@Dao
public interface ArchivedLotDao {

    @Insert
    void insertArchivedLotEntity(ArchivedLotEntity archivedLotEntity);

    @Insert
    void insertArchivedLotEntityList(List<ArchivedLotEntity> archivedLotEntities);

    @Query("SELECT * FROM archivedLot")
    List<ArchivedLotEntity> getArchiveLots();

    @Query("SELECT * FROM archivedLot WHERE lotId = :lotId")
    ArchivedLotEntity getArchivedLotById(String lotId);

    @Update
    void updateArchivedLot(ArchivedLotEntity archivedLotEntity);

    @Delete
    void deleteArchivedLot(ArchivedLotEntity archivedLotEntity);

    @Query("DELETE FROM archivedLot")
    void deleteArchivedLotTable();

}
