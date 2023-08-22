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

    // TODO: update this to delete
    @Transaction
    suspend fun insertOrUpdate(feedList: List<FeedEntity>) {
        val insertResult = insertFeedEntityList(feedList)
        val updateList = mutableListOf<FeedEntity>()
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(feedList[i])
        }
        if (updateList.isNotEmpty()) updateFeedList(feedList)
    }
}