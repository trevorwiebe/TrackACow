package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLotEntity

@Dao
interface CacheLotDao {

    @Insert
    suspend fun insertCacheLot(cacheLotEntity: CacheLotEntity)


    @Update
    fun updateHoldingLot(cacheLotEntity: CacheLotEntity)

    // Deprecated
    @Deprecated("use flow return function")
    @Query("SELECT * FROM holdingLot")
    fun getHoldingLotList(): List<CacheLotEntity>

    @Deprecated("use suspend function")
    @Insert
    fun insertHoldingLot(cacheLotEntity: CacheLotEntity)

    @Deprecated("use suspend function")
    @Delete
    fun deleteHoldingLot(cacheLotEntity: CacheLotEntity)

    @Deprecated("use flow return function")
    @Query("DELETE FROM holdingLot")
    fun deleteHoldingLotTable()
}