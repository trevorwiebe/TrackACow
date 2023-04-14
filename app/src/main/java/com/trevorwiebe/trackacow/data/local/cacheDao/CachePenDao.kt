package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CachePenEntity

@Dao
interface CachePenDao {

    @Insert
    suspend fun insertCachePen(cachePenEntity: CachePenEntity)

}