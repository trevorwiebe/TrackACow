package com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases

import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.repository.local.FeedRepository
import com.trevorwiebe.trackacow.domain.repository.remote.FeedRepositoryRemote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart

data class ReadFeedsByLotId(
    private val feedRepository: FeedRepository,
    private val feedRepositoryRemote: FeedRepositoryRemote
){
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(lotId: String): Flow<List<FeedModel>> {

        val localFeedFlow = feedRepository.getFeedsByLotId(lotId)
        val cloudFeedFlow = feedRepositoryRemote.readFeedsByLotId(lotId)

        return localFeedFlow
            .flatMapLatest { localData ->
                cloudFeedFlow.onStart { emit(localData) }
            }
    }
}
