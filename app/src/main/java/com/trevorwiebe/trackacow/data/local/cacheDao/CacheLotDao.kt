package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLotEntity

@Dao
interface CacheLotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheLot(cacheLotEntity: CacheLotEntity)

    @Update
    fun updateHoldingLot(cacheLotEntity: CacheLotEntity)

    @Query("SELECT * FROM cache_lot")
    suspend fun getCacheLots(): List<CacheLotEntity>

    @Query("DELETE FROM cache_lot")
    suspend fun deleteCacheLots()

}