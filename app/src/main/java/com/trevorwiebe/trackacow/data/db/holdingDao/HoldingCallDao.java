package com.trevorwiebe.trackacow.data.db.holdingDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.db.holdingUpdateEntities.HoldingCallEntity;
import com.trevorwiebe.trackacow.data.db.entities.CallEntity;

import java.util.List;

@Dao
public interface HoldingCallDao {

    @Insert
    void insertHoldingCall(HoldingCallEntity holdingCallEntity);

    @Insert
    void insertHoldingCallList(List<HoldingCallEntity> holdingCallEntities);

    @Query("SELECT * FROM holdingCall")
    List<HoldingCallEntity> getHoldingCallEntities();

    @Query("DELETE FROM holdingCall")
    void deleteCallTable();

    @Update
    void updateCallEntity(CallEntity callEntity);

    @Delete
    void deleteCallEntity(CallEntity callEntity);

}
