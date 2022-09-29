package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.PenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPen(penEntity: PenEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPenList(penEntities: List<PenEntity>)

    @Query("SELECT * FROM Pen WHERE penId = :id")
    fun getPenById(id: String): Flow<PenEntity?>

    @Deprecated("Use getPenByPenId with return type: Flow, instead")
    @Query("SELECT * FROM Pen WHERE penId = :id")
    fun getPenById2(id: String): PenEntity?

    @Query("SELECT * FROM Pen ORDER BY penName ASC")
    fun getPenList(): Flow<List<PenEntity>>

    @Deprecated("Use getPenList with return type: Flow, instead")
    @Query("SELECT * FROM Pen ORDER BY penName ASC")
    fun getPenList2(): List<PenEntity>

    @Query("UPDATE Pen SET penName = :penName WHERE penId = :penId")
    fun updatePenNameById(penName: String, penId: String)

    @Query("DELETE FROM Pen")
    fun deletePenTable()

    @Update
    fun updatePen(penEntity: PenEntity)

    @Delete
    fun deletePen(penEntity: PenEntity)
}