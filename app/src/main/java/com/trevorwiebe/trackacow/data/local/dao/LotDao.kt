package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.LotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createLot(lotEntity: LotEntity)

    @Query("SELECT * FROM lot WHERE archived = 0")
    fun getLotEntities(): Flow<List<LotEntity>>

    @Query("SELECT * FROM lot WHERE lotPrimaryKey = :lotPrimaryKey AND archived = 0")
    fun getLotByLotId(lotPrimaryKey: Int): Flow<LotEntity?>

    @Query("SELECT * FROM lot WHERE lotPenCloudDatabaseId = :penId AND archived = 0")
    fun getLotEntitiesByPenId(penId: String): Flow<List<LotEntity>>

    @Query("SELECT * FROM lot WHERE archived = 1")
    fun getArchivedLots(): Flow<List<LotEntity>>

    @Query("UPDATE lot SET lotPenCloudDatabaseId = :penId WHERE lotCloudDatabaseId = :lotId")
    suspend fun updateLotWithNewPenId(lotId: String, penId: String)

    @Query("UPDATE lot SET archived = 1, dateArchived = :date WHERE lotPrimaryKey = :lotPrimaryKey")
    suspend fun archiveLot(lotPrimaryKey: Int, date: Long)

    @Query(
        "UPDATE lot SET " +
                "lotName = :lotName, customerName = :customerName, notes = :notes, date = :date " +
                "WHERE lotPrimaryKey = :lotPrimaryKey"
    )
    suspend fun updateLot(
            lotPrimaryKey: Int,
            lotName: String,
            customerName: String?,
            notes: String?,
            date: Long
    )

    @Delete
    suspend fun deleteLot(lotEntity: LotEntity)

    @Query("DELETE FROM lot")
    suspend fun deleteAllLots()
}