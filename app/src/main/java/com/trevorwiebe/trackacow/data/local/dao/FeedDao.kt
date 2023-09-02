package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.FeedEntity
import com.trevorwiebe.trackacow.data.entities.compound_entities.FeedAndRationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedEntity(feedEntity: FeedEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFeedEntityList(feedEntityList: List<FeedEntity>): List<Long>

    @Upsert
    suspend fun upsertFeedEntityList(feedEntityList: List<FeedEntity>)

    @Query("SELECT * FROM feed WHERE lotId = :lotId")
    fun getFeedsByLotId(lotId: String): Flow<List<FeedEntity>>

    @Query("SELECT * FROM feed WHERE lotId = :lotId AND date BETWEEN :startDate AND :endDate")
    fun getFeedsByLotIdAndDate(
        lotId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<FeedEntity>>

    @Query(
        "SELECT SUM(feed.feed) AS 'feed', feed.date, feed.lotId, ration.rationCloudDatabaseId, ration.rationName FROM feed " +
                "LEFT JOIN ration ON ration.rationCloudDatabaseId = feed.rationCloudId " +
                "WHERE lotId = :lotId AND date BETWEEN :startDate AND :endDate " +
                "GROUP BY ration.rationCloudDatabaseId " +
                "ORDER BY ration.rationName"
    )
    fun getFeedsAndRationsTotalsByLotIdAndDate(
        lotId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<FeedAndRationEntity>>

    @Update
    suspend fun updateFeedEntity(feedEntity: FeedEntity)

    @Query("UPDATE feed SET lotId = :lotId WHERE lotId IN (:oldLotIds)")
    suspend fun updateFeedsWithNewLotId(lotId: String, oldLotIds: List<String>)

    @Update
    suspend fun updateFeedList(feedEntityList: List<FeedEntity>)

    @Delete
    suspend fun deleteFeedEntity(feedEntity: FeedEntity)

    @Query("DELETE FROM feed WHERE id IN (:idList)")
    suspend fun deleteFeedByIdList(idList: List<String>)

    @Query("DELETE FROM feed")
    suspend fun deleteAllFeeds()

    @Query("DELETE FROM feed WHERE lotId = :lotId")
    suspend fun deleteFeedByLotId(lotId: String)

    @Query("DELETE FROM feed WHERE lotId = :lotId AND date BETWEEN :startDate AND :endDate")
    suspend fun deleteFeedByLotIdAndDate(lotId: String, startDate: Long, endDate: Long)

    @Transaction
    suspend fun syncCloudFeedByLotId(feedList: List<FeedEntity>, lotId: String) {
        deleteFeedByLotId(lotId)
        insertFeedEntityList(feedList)
    }

    @Transaction
    suspend fun syncCloudFeedByLotIdAndDate(
        feedList: List<FeedEntity>,
        lotId: String,
        startDate: Long,
        endDate: Long
    ) {
        deleteFeedByLotIdAndDate(lotId, startDate, endDate)
        insertFeedEntityList(feedList)
    }
}