package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.LotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLot(lotEntity: LotEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLotEntityList(lotEntities: List<LotEntity>)

    @Query("SELECT * FROM lot")
    fun getLotEntities(): Flow<List<LotEntity>>

    @Query("SELECT * FROM lot WHERE lotPrimaryKey = :lotPrimaryKey")
    fun getLotByLotId(lotPrimaryKey: Int): Flow<LotEntity?>

    @Query("SELECT * FROM lot WHERE lotPenCloudDatabaseId = :penId")
    fun getLotEntitiesByPenId(penId: String): Flow<List<LotEntity>>

    @Update
    fun updateLotEntity(lotEntity: LotEntity)

    @Query("UPDATE lot SET lotName = :lotName, customerName = :customerName, date = :date, notes = :notes WHERE lotCloudDatabaseId = :lotId")
    fun updateLotByFields(
        lotName: String?,
        customerName: String?,
        notes: String?,
        date: Long,
        lotId: String
    )

    @Query("UPDATE lot SET lotPenCloudDatabaseId = :penId WHERE lotCloudDatabaseId = :lotId")
    suspend fun updateLotWithNewPenId(lotId: String, penId: String)

    @Query("DELETE FROM lot")
    fun deleteLotEntityTable()

    @Query("DELETE FROM lot WHERE lotCloudDatabaseId = :lotId")
    fun deleteLotEntity(lotId: String)

    // Deprecated
    @Deprecated("Use suspend function instead")
    @Query("UPDATE lot SET lotPenCloudDatabaseId = :penId WHERE lotCloudDatabaseId = :lotId")
    fun updateLotWithNewPenId2(lotId: String, penId: String)

    @Deprecated("Use flows return types")
    @Query("SELECT * FROM lot")
    fun getLotEntityList(): List<LotEntity>

    @Deprecated("Use getLotEntitiesByPenId with return type of Flow instead")
    @Query("SELECT * FROM lot WHERE lotPenCloudDatabaseId = :penId")
    fun getLotEntitiesByPenId2(penId: String): List<LotEntity>

    @Deprecated("Use flow return types")
    @Query("SELECT * FROM lot WHERE lotCloudDatabaseId = :lotId")
    fun getLotEntityById(lotId: String): LotEntity?
}