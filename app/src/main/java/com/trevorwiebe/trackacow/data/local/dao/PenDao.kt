package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.PenEntity
import com.trevorwiebe.trackacow.data.entities.compound_entities.PenAndLotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPen(penEntity: PenEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPenList(penEntities: List<PenEntity>)

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
                "AND lot.archived = 0"
    )
    fun getPenAndLotListExcludeEmptyPens(): Flow<List<PenAndLotEntity>>

    @Query("UPDATE Pen SET penName = :penName WHERE penCloudDatabaseId = :penId")
    fun updatePenNameById(penName: String, penId: String)

    @Query("UPDATE Pen SET penName = :penName WHERE penPrimaryKey = :penPrimaryKey")
    suspend fun updatePen(penName: String, penPrimaryKey: Int)

    @Query("DELETE FROM Pen")
    suspend fun deleteAllPens()

    @Delete
    suspend fun deletePen(penEntity: PenEntity)


    // Deprecated

    @Deprecated("Use suspend function")
    @Update
    fun updatePen2(penEntity: PenEntity)

    @Deprecated("Use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPen2(penEntity: PenEntity)

    @Deprecated("Use getPenByPenId with return type: Flow, instead")
    @Query("SELECT * FROM Pen WHERE penCloudDatabaseId = :id")
    fun getPenById2(id: String): PenEntity?

    @Deprecated("Use getPenList with return type: Flow, instead")
    @Query("SELECT * FROM Pen ORDER BY penName ASC")
    fun getPenList2(): List<PenEntity>

    @Deprecated("Use suspend function")
    @Delete
    fun deletePen2(penEntity: PenEntity)
}