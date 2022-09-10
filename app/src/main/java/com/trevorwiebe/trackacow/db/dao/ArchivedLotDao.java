package com.trevorwiebe.trackacow.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.db.entities.ArchivedLotEntity;

import java.util.List;

@Dao
public interface ArchivedLotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArchivedLotEntity(ArchivedLotEntity archivedLotEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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
