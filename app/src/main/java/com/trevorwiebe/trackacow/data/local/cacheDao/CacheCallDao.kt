package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheCallEntity

@Dao
interface CacheCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoldingCall(cacheCallEntity: CacheCallEntity)

    @Query("SELECT * FROM cache_call")
    suspend fun getCacheCalls(): List<CacheCallEntity>

    @Query("DELETE FROM cache_call")
    suspend fun deleteCacheCalls()
}