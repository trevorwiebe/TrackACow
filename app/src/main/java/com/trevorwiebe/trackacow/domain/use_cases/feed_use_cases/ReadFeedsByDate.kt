package com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases

import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.repository.local.FeedRepository
import kotlinx.coroutines.flow.Flow

data class ReadFeedsByDate(
    private val feedRepository: FeedRepository
) {
    operator fun invoke(lotId: String, startDate: Long, endDate: Long): Flow<List<FeedModel>> {
        return feedRepository.readFeedsByDate(lotId, startDate, endDate)
    }
}