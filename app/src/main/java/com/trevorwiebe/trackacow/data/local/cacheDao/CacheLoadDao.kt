package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLoadEntity

@Dao
interface CacheLoadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheLoad(cacheLoadEntity: CacheLoadEntity?)

    @Query("SELECT * FROM cache_load")
    suspend fun getCacheLoads(): List<CacheLoadEntity>

    @Query("DELETE FROM cache_load")
    suspend fun deleteCacheLoads()

}