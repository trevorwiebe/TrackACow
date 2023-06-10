package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.feed.FeedModel

interface FeedRepositoryRemote {

    suspend fun insertOrUpdateFeedRemoteList(feedModelList: List<FeedModel>)

    suspend fun deleteFeedRemoteList(feedModelList: List<FeedModel>)

    suspend fun updateFeedWithNewLotIdRemote(lotIdToSave: String, lotIdsToDelete: List<String>)
}