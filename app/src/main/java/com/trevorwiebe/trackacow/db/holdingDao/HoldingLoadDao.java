package com.trevorwiebe.trackacow.db.holdingDao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLoadEntity;

import java.util.List;

@Dao
public interface HoldingLoadDao {

    @Insert
    void insertHoldingLoad(HoldingLoadEntity holdingLoadEntity);

    @Query("SELECT * FROM holdingLoad")
    List<HoldingLoadEntity> getHoldingLoadList();

    @Update()
    void updateHoldingLoad(HoldingLoadEntity holdingLoadEntity);

    @Delete
    void deleteHoldingLoad(HoldingLoadEntity holdingLoadEntity);

}
