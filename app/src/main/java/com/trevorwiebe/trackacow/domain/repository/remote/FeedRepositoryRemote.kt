package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.feed.FeedModel

interface FeedRepositoryRemote {

    suspend fun insertOrUpdateFeedRemoteList(feedModelList: List<FeedModel>)
}