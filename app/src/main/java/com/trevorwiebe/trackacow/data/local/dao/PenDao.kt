package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.PenEntity
import com.trevorwiebe.trackacow.data.entities.compound_entities.PenAndLotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPen(penEntity: PenEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPenList(penEntities: List<PenEntity>): List<Long>

    @Query("SELECT * FROM Pen WHERE penCloudDatabaseId = :id")
    fun getPenById(id: String): Flow<PenEntity?>

    @Query("SELECT * FROM Pen ORDER BY penName ASC")
    fun getPenList(): Flow<List<PenEntity>>

    @Query(
        "SELECT * FROM Pen LEFT JOIN lot ON lot.lotPenCloudDatabaseId = Pen.penCloudDatabaseId " +
                "AND lot.archived = 0"
    )
    fun getPenAndLotListIncludeEmptyPens(): Flow<List<PenAndLotEntity>>

    @Query(
        "SELECT * FROM Pen JOIN lot ON lot.lotPenCloudDatabaseId = Pen.penCloudDatabaseId " +
                "AND lot.archived = 0 ORDER BY Pen.penName ASC"
    )
    fun getPenAndLotListExcludeEmptyPens(): Flow<List<PenAndLotEntity>>

    @Query("UPDATE Pen SET penName = :penName WHERE penCloudDatabaseId = :penId")
    fun updatePenNameById(penName: String, penId: String)

    @Update
    suspend fun updatePen(penEntity: PenEntity)

    @Update
    suspend fun updatePenList(penList: List<PenEntity>)

    @Query("DELETE FROM Pen")
    suspend fun deleteAllPens()

    @Delete
    suspend fun deletePen(penEntity: PenEntity)

    @Transaction
    suspend fun insertOrUpdatePenList(penList: List<PenEntity>) {
        val insertResult = insertPenList(penList)
        val updateList = mutableListOf<PenEntity>()
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(penList[i])
        }
        if (updateList.isNotEmpty()) updatePenList(updateList)
    }
}