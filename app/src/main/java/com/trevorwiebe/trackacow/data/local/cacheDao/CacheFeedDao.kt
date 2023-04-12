package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheFeedEntity

@Dao
interface CacheFeedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoldingFeedList(holdingFeedEntities: List<CacheFeedEntity>)

    @Update
    suspend fun updateHoldingFeedEntity(cacheFeedEntity: CacheFeedEntity)

    @Delete
    suspend fun deleteFeedEntity(cacheFeedEntity: CacheFeedEntity)


    /* Deprecated functions */

    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHoldingFeed(cacheFeedEntity: CacheFeedEntity)

    @Deprecated("use function with return type: flow")
    @Query("SELECT * FROM cache_feed")
    fun getHoldingFeedEntities(): List<CacheFeedEntity>

    @Deprecated("use suspend function")
    @Query("DELETE FROM cache_feed")
    fun deleteHoldingFeedTable()
}