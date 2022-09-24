package com.trevorwiebe.trackacow.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.data.db.entities.CallEntity;

import java.util.List;

@Dao
public interface CallDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCall(CallEntity callEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCallList(List<CallEntity> callEntities);

    @Query("SELECT * FROM call")
    List<CallEntity> getCallEntities();

    @Query("SELECT * FROM call WHERE id = :id")
    CallEntity getCallEntity(String id);

    @Query("SELECT * FROM call WHERE date = :date AND lotId = :lotId")
    CallEntity getCallEntityByDateAndLotId(long date, String lotId);

    @Query("UPDATE call SET callAmount = :callAmount WHERE id = :callId")
    void updateCallByCallId(String callId, int callAmount);

    @Query("SELECT * FROM call WHERE lotId = :lotId")
    List<CallEntity> getCallEntitiesByLotId(String lotId);

    @Query("DELETE FROM call")
    void deleteCallTable();

    @Update
    void updateCallEntity(CallEntity callEntity);

    @Delete
    void deleteCallEntity(CallEntity callEntity);

}
