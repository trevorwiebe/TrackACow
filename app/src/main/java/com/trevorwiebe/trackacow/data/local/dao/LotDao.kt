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

    @Deprecated("Use flows return types")
    @Query("SELECT * FROM lot")
    fun getLotEntityList(): List<LotEntity>

    @Query("SELECT * FROM lot WHERE penId = :penId")
    fun getLotEntitiesByPenId(penId: String): Flow<List<LotEntity>>

    @Deprecated("Use getLotEntitiesByPenId with return type of Flow instead")
    @Query("SELECT * FROM lot WHERE penId = :penId")
    fun getLotEntitiesByPenId2(penId: String): List<LotEntity>

    @Deprecated("Use flow return types")
    @Query("SELECT * FROM lot WHERE lotId = :lotId")
    fun getLotEntityById(lotId: String): LotEntity?

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

    @Query("UPDATE lot SET penId = :penId WHERE lotId = :lotId")
    fun updateLotWithNewPenId(lotId: String, penId: String)

    @Query("DELETE FROM lot")
    fun deleteLotEntityTable()

    @Query("DELETE FROM lot WHERE lotId = :lotId")
    fun deleteLotEntity(lotId: String)
}