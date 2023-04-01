package com.trevorwiebe.trackacow.domain.repository.local

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

    suspend fun createFeedListRemote(feedModelList: List<CacheFeedModel>)

    suspend fun deleteFeedList(feedModelList: List<FeedModel>)
}