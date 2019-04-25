package com.trevorwiebe.trackacow.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.trackacow.db.entities.LoadEntity;

import java.util.List;

@Dao
public interface LoadDao {

    @Insert
    void insertLoad(LoadEntity loadEntity);

    @Insert
    void insertLoadList(List<LoadEntity> loadEntities);

    @Query("SELECT * FROM load WHERE lotId = :lotId")
    List<LoadEntity> getLoadsByLotId(String lotId);

    @Query("SELECT * FROM load WHERE loadId = :loadId")
    LoadEntity getLoadByLoadId(String loadId);

    @Query("UPDATE load SET numberOfHead = :headCount, date = :date, description = :memo WHERE loadId = :loadId")
    void updateLoadByFields(int headCount, long date, String memo, String loadId);

    @Update
    void updateLoad(LoadEntity loadEntity);

    @Delete
    void deleteLoad(LoadEntity loadEntity);

    @Query("DELETE FROM load")
    void deleteLoadTable();

    @Query("DELETE FROM load WHERE loadId = :loadId")
    void deleteLoadByLoadId(String loadId);

}
