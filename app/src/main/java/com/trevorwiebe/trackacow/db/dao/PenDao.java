package com.trevorwiebe.trackacow.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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

    @Query("UPDATE Pen SET customerName = :customerName, isActive = :isActive, totalHead = :totalHead, notes = :notes WHERE penId = :penId")
    void updatePenByFields(String customerName, int isActive, int totalHead, String notes, String penId);

    @Query("UPDATE Pen SET penName = :penName WHERE penId = :penId")
    void updatePenNameById(String penName, String penId);

    @Query("DELETE FROM Pen")
    void deletePenTable();

    @Update
    void updatePen(PenEntity penEntity);

    @Delete
    void deletePen(PenEntity penEntity);

}
