package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.FeedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedEntity(feedEntity: FeedEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedEntityList(feedEntityList: List<FeedEntity>)

    @Query("SELECT * FROM feed WHERE lotId = :lotId")
    fun getFeedsByLotId(lotId: String): Flow<List<FeedEntity>>

    @Query("SELECT * FROM feed WHERE lotId = :lotId AND date BETWEEN :startDate AND :endDate")
    fun getFeedsByLotIdAndDate(
        lotId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<FeedEntity>>

    @Update
    suspend fun updateFeedEntity(feedEntity: FeedEntity)

    @Delete
    suspend fun deleteFeedEntity(feedEntity: FeedEntity)

    @Query("DELETE FROM feed WHERE id IN (:idList)")
    suspend fun deleteFeedByIdList(idList: List<String>)

    @Query("DELETE FROM feed")
    suspend fun deleteAllFeeds()
}