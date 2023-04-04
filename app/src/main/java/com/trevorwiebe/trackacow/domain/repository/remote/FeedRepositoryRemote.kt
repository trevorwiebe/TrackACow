package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.feed.CacheFeedModel

interface FeedRepositoryRemote {

    suspend fun insertOrUpdateFeedRemoteList(feedModelList: List<CacheFeedModel>)
}