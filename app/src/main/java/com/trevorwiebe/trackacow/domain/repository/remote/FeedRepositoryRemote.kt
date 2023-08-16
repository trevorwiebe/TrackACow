package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.feed.CacheFeedModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import kotlinx.coroutines.flow.Flow

interface FeedRepositoryRemote {

    suspend fun insertOrUpdateFeedRemoteList(feedModelList: List<FeedModel>)

    suspend fun insertCacheFeeds(feedModelList: List<CacheFeedModel>)

    fun readFeedsByLotId(lotId: String): Flow<List<FeedModel>>

    fun readFeedsAndRationsByLotId(lotId: String): Flow<Pair<List<RationModel>, List<FeedModel>>>

    suspend fun deleteFeedRemoteList(feedModelList: List<FeedModel>)

    suspend fun updateFeedWithNewLotIdRemote(lotIdToSave: String, lotIdsToDelete: List<String>)
}