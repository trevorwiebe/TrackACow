package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.LotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createLot(lotEntity: LotEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLotList(lotEntities: List<LotEntity>): List<Long>

    @Query("SELECT * FROM lot WHERE archived = 0")
    fun getLotEntities(): Flow<List<LotEntity>>

    @Query("SELECT * FROM lot WHERE lotCloudDatabaseId = :lotCloudDatabaseId AND archived = 0")
    fun getLotByLotId(lotCloudDatabaseId: String): Flow<LotEntity?>

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

    @Update()
    suspend fun updateLotList(lotList: List<LotEntity>)

    @Delete
    suspend fun deleteLot(lotEntity: LotEntity)

    @Query("DELETE FROM lot WHERE lotCloudDatabaseId IN (:lotIdList)")
    suspend fun deleteLotList(lotIdList: List<String>)

    @Query("DELETE FROM lot")
    suspend fun deleteAllLots()

    @Transaction
    suspend fun insertOrUpdateLotList(lotList: List<LotEntity>) {
        val insertResult = insertLotList(lotList)
        val updateList = mutableListOf<LotEntity>()
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(lotList[i])
        }
        if (updateList.isNotEmpty()) updateLotList(lotList)
    }
}