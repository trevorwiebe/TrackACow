package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CachePenEntity

@Dao
interface CachePenDao {

    @Insert
    suspend fun insertCachePen(cachePenEntity: CachePenEntity)

    @Insert
    fun insertHoldingPenList(holdingPenEntities: List<CachePenEntity>)

    @Query("SELECT * FROM cache_pen WHERE penCloudDatabaseId = :id")
    fun getHoldingPenById(id: String?): CachePenEntity?

    @get:Query("SELECT * FROM cache_pen")
    val holdingPenList: List<CachePenEntity>

    @Query("DELETE FROM cache_pen")
    fun deleteHoldingPenTable()

    @Update
    fun updateHoldingPen(cachePenEntity: CachePenEntity)

    @Delete
    fun deleteHoldingPen(cachePenEntity: CachePenEntity)

    // Deprecated
    @Deprecated("use suspend function")
    @Insert
    fun insertHoldingPen2(cachePenEntity: CachePenEntity)

}