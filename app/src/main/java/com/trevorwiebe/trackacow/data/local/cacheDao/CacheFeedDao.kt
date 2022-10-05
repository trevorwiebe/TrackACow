package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheFeedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CacheFeedDao {

    @Insert
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
    @Query("SELECT * FROM holdingFeed")
    fun getHoldingFeedEntities(): List<CacheFeedEntity>

    @Deprecated("use suspend function")
    @Query("DELETE FROM holdingFeed")
    fun deleteHoldingFeedTable()
}