package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.feed.CacheFeedModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import kotlinx.coroutines.flow.Flow

interface FeedRepositoryRemote {

    suspend fun insertOrUpdateFeedRemoteList(feedModelList: List<FeedModel>)

    suspend fun insertCacheFeeds(feedModelList: List<CacheFeedModel>)

    fun readFeedsByLotId(lotId: String): Flow<List<FeedModel>>

    suspend fun deleteFeedRemoteList(feedModelList: List<FeedModel>)

    suspend fun updateFeedWithNewLotIdRemote(lotIdToSave: String, lotIdsToDelete: List<String>)
}