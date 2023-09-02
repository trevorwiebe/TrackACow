package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.compound_model.FeedAndRationModel
import com.trevorwiebe.trackacow.domain.models.feed.CacheFeedModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import kotlinx.coroutines.flow.Flow

interface FeedRepository {

    suspend fun createOrUpdateFeedList(feedModelList: List<FeedModel>)

    fun getFeedsByLotId(lotId: String): Flow<List<FeedModel>>

    fun readFeedsByLotIdAndDate(
            lotId: String,
            startDate: Long,
            endDate: Long
    ): Flow<List<FeedModel>>

    fun readFeedsAndRationTotalByLotIdAndDate(
        lotId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<FeedAndRationModel>>

    suspend fun updateFeedsWithNewLot(lotId: String, oldLotIds: List<String>)

    suspend fun deleteFeedList(feedModelList: List<FeedModel>)

    suspend fun deleteAllFeeds()

    suspend fun syncCloudFeedByLotId(feedModelList: List<FeedModel>, lotId: String)

    suspend fun syncCloudFeedByLotIdAndDate(
        feedModelList: List<FeedModel>,
        lotId: String,
        startDate: Long,
        endDate: Long
    )

    // cache functions
    suspend fun createCacheFeedList(feedModelList: List<CacheFeedModel>)

    suspend fun getCacheFeeds(): List<CacheFeedModel>

    suspend fun deleteCacheFeeds()
}