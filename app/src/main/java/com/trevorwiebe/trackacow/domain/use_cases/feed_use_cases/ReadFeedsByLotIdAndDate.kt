package com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases

import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.repository.local.FeedRepository
import com.trevorwiebe.trackacow.domain.repository.remote.FeedRepositoryRemote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadFeedsByLotIdAndDate(
    private val feedRepository: FeedRepository,
    private val feedRepositoryRemote: FeedRepositoryRemote
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(lotId: String, startDate: Long, endDate: Long): Flow<List<FeedModel>> {
        val localFeedFlow = feedRepository.readFeedsByLotIdAndDate(lotId, startDate, endDate)
        val cloudFeedFlow = feedRepositoryRemote.readFeedsByLotId(lotId)

//        val feedQuery = firebaseDatabase
//            .getReference(feedDatabaseString)
//            .orderByChild("lotId")
//            .equalTo(lotId)
//
//        val feedCloudFlow = feedQuery.addQueryListValueEventListenerFlow(FeedModel::class.java)

        return localFeedFlow
            .flatMapLatest { localData ->
                cloudFeedFlow
                    .map {
                        it.filter { feedModel ->
                            feedModel.date in startDate..endDate
                        }
                    }
                    .onStart { emit(localData) }
            }
    }
}