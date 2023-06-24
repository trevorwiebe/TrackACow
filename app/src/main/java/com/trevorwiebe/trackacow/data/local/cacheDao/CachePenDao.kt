package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CachePenEntity

@Dao
interface CachePenDao {

    @Insert
    suspend fun insertCachePen(cachePenEntity: CachePenEntity)

    @Query("SELECT * FROM cache_pen")
    suspend fun getCachePens(): List<CachePenEntity>

    @Query("DELETE FROM cache_pen")
    suspend fun deleteCachePens()

}