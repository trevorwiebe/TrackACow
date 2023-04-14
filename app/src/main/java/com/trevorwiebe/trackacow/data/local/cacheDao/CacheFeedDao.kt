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

}