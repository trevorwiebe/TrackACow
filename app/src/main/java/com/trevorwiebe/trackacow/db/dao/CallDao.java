package com.trevorwiebe.trackacow.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.trevorwiebe.trackacow.db.entities.CallEntity;

import java.util.List;

@Dao
public interface CallDao {

    @Insert
    void insertCall(CallEntity callEntity);

    @Insert
    void insertCallList(List<CallEntity> callEntities);

    @Query("SELECT * FROM call")
    List<CallEntity> getCallEntities();

    @Query("SELECT * FROM call WHERE id = :id")
    CallEntity getCallEntity(String id);

    @Query("SELECT * FROM call WHERE date = :date AND lotId = :lotId")
    CallEntity getCallEntityByDateAndLotId(long date, String lotId);

    @Query("UPDATE call SET amountFed = :amountFed WHERE id = :callId")
    void updateCallByCallId(String callId, int amountFed);

    @Query("SELECT * FROM call WHERE lotId = :lotId")
    List<CallEntity> getCallEntitiesByLotId(String lotId);

    @Query("DELETE FROM call")
    void deleteCallTable();

    @Update
    void updateCallEntity(CallEntity callEntity);

    @Delete
    void deleteCallEntity(CallEntity callEntity);

}
