package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLoadEntity

@Dao
interface CacheLoadDao {

    @Insert
    suspend fun insertCacheLoad(cacheLoadEntity: CacheLoadEntity?)

}