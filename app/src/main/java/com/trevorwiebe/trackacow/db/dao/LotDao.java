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

    @Update
    void updateLotEntity(LotEntity lotEntity);

    @Query("DELETE FROM lot")
    void deleteLotEntityList();

    @Delete
    void deleteLotEntity(LotEntity lotEntity);

}
