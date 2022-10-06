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

    @Query("SELECT * FROM lot WHERE lotPenId = :penId")
    fun getLotEntitiesByPenId(penId: String): Flow<List<LotEntity>>

    @Update
    fun updateLotEntity(lotEntity: LotEntity)

    @Query("UPDATE lot SET lotName = :lotName, customerName = :customerName, date = :date, notes = :notes WHERE lotId = :lotId")
    fun updateLotByFields(
        lotName: String?,
        customerName: String?,
        notes: String?,
        date: Long,
        lotId: String
    )

    @Query("UPDATE lot SET lotPenId = :penId WHERE lotId = :lotId")
    suspend fun updateLotWithNewPenId(lotId: String, penId: String)

    @Query("DELETE FROM lot")
    fun deleteLotEntityTable()

    @Query("DELETE FROM lot WHERE lotId = :lotId")
    fun deleteLotEntity(lotId: String)

    // Deprecated
    @Deprecated("Use suspend function instead")
    @Query("UPDATE lot SET lotPenId = :penId WHERE lotId = :lotId")
    fun updateLotWithNewPenId2(lotId: String, penId: String)

    @Deprecated("Use flows return types")
    @Query("SELECT * FROM lot")
    fun getLotEntityList(): List<LotEntity>

    @Deprecated("Use getLotEntitiesByPenId with return type of Flow instead")
    @Query("SELECT * FROM lot WHERE lotPenId = :penId")
    fun getLotEntitiesByPenId2(penId: String): List<LotEntity>

    @Deprecated("Use flow return types")
    @Query("SELECT * FROM lot WHERE lotId = :lotId")
    fun getLotEntityById(lotId: String): LotEntity?
}