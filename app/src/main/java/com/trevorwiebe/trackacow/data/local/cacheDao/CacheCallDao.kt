package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheCallEntity
import com.trevorwiebe.trackacow.data.entities.CallEntity

@Dao
interface CacheCallDao {
    @Insert
    suspend fun insertHoldingCall(cacheCallEntity: CacheCallEntity)

    @get:Query("SELECT * FROM cache_call")
    val holdingCallEntities: List<CacheCallEntity>

    @Query("DELETE FROM cache_call")
    fun deleteCallTable()

    @Update
    fun updateCallEntity(callEntity: CallEntity)

    @Delete
    fun deleteCallEntity(callEntity: CallEntity)
}