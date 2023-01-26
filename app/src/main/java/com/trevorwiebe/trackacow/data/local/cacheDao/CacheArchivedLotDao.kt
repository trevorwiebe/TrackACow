package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheArchivedLotEntity

@Dao
interface CacheArchivedLotDao {

    @Insert
    suspend fun insertCacheArchiveLot(cacheArchivedLotEntity: CacheArchivedLotEntity)

    @Deprecated("use suspend function")
    @Insert
    fun insertHoldingArchivedLot(cacheArchivedLotEntity: CacheArchivedLotEntity?)

    @Deprecated("use flow function")
    @Query("SELECT * FROM holdingArchivedLot")
    fun getHoldingArchivedLotList(): List<CacheArchivedLotEntity?>?

    @Deprecated("use suspend function")
    @Update
    fun updateHoldingArchivedLot(cacheArchivedLotEntity: CacheArchivedLotEntity?)

    @Deprecated("use suspend function")
    @Delete
    fun deleteHoldingArchivedLot(cacheArchivedLotEntity: CacheArchivedLotEntity?)

    @Deprecated("use suspend function")
    @Query("DELETE FROM holdingArchivedLot")
    fun deleteHoldingArchivedLotTable()
}