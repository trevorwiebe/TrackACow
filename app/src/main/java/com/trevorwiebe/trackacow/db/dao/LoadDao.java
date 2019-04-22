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

    @Query("SELECT * FROM load WHERE lotId = :lotId")
    List<LoadEntity> getLoadsByLotId(String lotId);

    @Update
    void updateLoad(LoadEntity loadEntity);

    @Delete
    void deleteLoad(LoadEntity loadEntity);

}
