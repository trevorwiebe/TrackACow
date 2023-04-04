package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLoadEntity

@Dao
interface CacheLoadDao {

    @Insert
    suspend fun insertCacheLoad(cacheLoadEntity: CacheLoadEntity?)

    @Deprecated("use flow return type")
    @Insert
    fun insertHoldingLoad(cacheLoadEntity: CacheLoadEntity?)

    @Deprecated("use flow return type")
    @Query("SELECT * FROM holdingLoad")
    fun getHoldingLoadList(): List<CacheLoadEntity?>?

    @Deprecated("use flow return type")
    @Update
    fun updateHoldingLoad(cacheLoadEntity: CacheLoadEntity?)

    @Deprecated("use flow return type")
    @Delete
    fun deleteHoldingLoad(cacheLoadEntity: CacheLoadEntity?)

    @Deprecated("use flow return type")
    @Query("DELETE FROM holdingLoad")
    fun deleteHoldingLoadTable()
}