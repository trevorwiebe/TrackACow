package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.ArchivedLotEntity

@Dao
interface ArchivedLotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArchiveLot(archivedLotEntity: ArchivedLotEntity): Long

    // Deprecated Functions
    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArchivedLotEntity(archivedLotEntity: ArchivedLotEntity?)

    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArchivedLotEntityList(archivedLotEntities: List<ArchivedLotEntity?>?)

    @Deprecated("use flow function")
    @Query("SELECT * FROM archivedLot")
    fun getArchiveLots(): List<ArchivedLotEntity?>?

    @Deprecated("use flow function")
    @Query("SELECT * FROM archivedLot WHERE lotId = :lotId")
    fun getArchivedLotById(lotId: String?): ArchivedLotEntity?

    @Deprecated("use suspend function")
    @Update
    fun updateArchivedLot(archivedLotEntity: ArchivedLotEntity?)

    @Deprecated("use suspend function")
    @Delete
    fun deleteArchivedLot(archivedLotEntity: ArchivedLotEntity?)

    @Deprecated("use suspend function")
    @Query("DELETE FROM archivedLot")
    fun deleteArchivedLotTable()
}