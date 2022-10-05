package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.PenEntity
import com.trevorwiebe.trackacow.data.entities.compound_entities.PenAndLotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPen(penEntity: PenEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPenList(penEntities: List<PenEntity>)

    @Query("SELECT * FROM Pen WHERE penPenId = :id")
    fun getPenById(id: String): Flow<PenEntity?>

    @Query("SELECT * FROM Pen ORDER BY penName ASC")
    fun getPenList(): Flow<List<PenEntity>>

    @Query("SELECT * FROM Pen LEFT JOIN lot ON lot.lotPenId = Pen.penPenId")
    fun getPenAndLotList(): Flow<List<PenAndLotEntity>>

    @Query("UPDATE Pen SET penName = :penName WHERE penPenId = :penId")
    fun updatePenNameById(penName: String, penId: String)

    @Query("DELETE FROM Pen")
    fun deletePenTable()

    @Update
    fun updatePen(penEntity: PenEntity)

    @Delete
    fun deletePen(penEntity: PenEntity)

    // Deprecated
    @Deprecated("Use getPenByPenId with return type: Flow, instead")
    @Query("SELECT * FROM Pen WHERE penPenId = :id")
    fun getPenById2(id: String): PenEntity?

    @Deprecated("Use getPenList with return type: Flow, instead")
    @Query("SELECT * FROM Pen ORDER BY penName ASC")
    fun getPenList2(): List<PenEntity>
}