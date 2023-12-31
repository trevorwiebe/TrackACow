package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trevorwiebe.trackacow.data.cacheEntities.CacheRationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CacheRationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoldingRation(cacheRationEntity: CacheRationEntity)

    @Query("SELECT * FROM cache_ration")
    fun getHoldingRations(): Flow<List<CacheRationEntity>>

    @Delete
    suspend fun deleteHoldingRation(cacheRationEntity: CacheRationEntity)

    @Query("SELECT * FROM cache_ration")
    suspend fun getCacheRations(): List<CacheRationEntity>

    @Query("DELETE FROM cache_ration")
    suspend fun deleteCacheRations()
}