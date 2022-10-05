package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getFeedsByLotId(lotId: String): Flow<List<FeedModel>>
}