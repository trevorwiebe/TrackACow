package com.trevorwiebe.trackacow.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trevorwiebe.trackacow.db.entities.PenEntity;

import java.util.List;

@Dao
public interface PenDao {

    @Insert
    void insertPen(PenEntity penEntity);

    @Insert
    void insertPenList(List<PenEntity> penEntities);

    @Query("SELECT * FROM Pen WHERE penId = :id")
    PenEntity getPenById(String id);

    @Query("SELECT * FROM Pen")
    List<PenEntity> getPenList();

    @Query("UPDATE Pen SET penName = :penName WHERE penId = :penId")
    void updatePenNameById(String penName, String penId);

    @Query("DELETE FROM Pen")
    void deletePenTable();

    @Update
    void updatePen(PenEntity penEntity);

    @Delete
    void deletePen(PenEntity penEntity);

}
