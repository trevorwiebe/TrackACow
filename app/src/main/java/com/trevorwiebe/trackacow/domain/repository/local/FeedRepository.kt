package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.feed.CacheFeedModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import kotlinx.coroutines.flow.Flow

interface FeedRepository {

    suspend fun createOrUpdateFeedList(feedModelList: List<FeedModel>): List<Long>

    fun getFeedsByLotId(lotId: String): Flow<List<FeedModel>>

    fun readFeedsByLotIdAndDate(
        lotId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<FeedModel>>

    suspend fun updateFeedsWithNewLot(lotId: String, oldLotIds: List<String>)

    suspend fun deleteFeedList(feedModelList: List<FeedModel>)

    suspend fun deleteAllFeeds()

    suspend fun insertOrUpdateFeedList(feedList: List<FeedModel>)

    // cache functions
    suspend fun createCacheFeedList(feedModelList: List<CacheFeedModel>)

    suspend fun getCacheFeeds(): List<CacheFeedModel>
}