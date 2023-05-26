package com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.repository.local.FeedRepository
import com.trevorwiebe.trackacow.domain.utils.addQueryListValueEventListenerFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadFeedsByLotIdAndDate(
    private val feedRepository: FeedRepository,
    private val firebaseDatabase: FirebaseDatabase,
    private val feedDatabaseString: String
) {
    // TODO: fix the issue in this class where the date filtering is applied to the local flow
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(lotId: String, startDate: Long, endDate: Long): Flow<List<FeedModel>> {
        val localFeedFlow = feedRepository.readFeedsByLotIdAndDate(lotId, startDate, endDate)

        val feedQuery = firebaseDatabase
            .getReference(feedDatabaseString)
            .orderByChild("lotId")
            .equalTo(lotId)

        val feedCloudFlow = feedQuery.addQueryListValueEventListenerFlow(FeedModel::class.java)

        return feedCloudFlow
            .flatMapLatest { cloudData ->
                feedRepository.insertOrUpdateFeedList(cloudData)
                localFeedFlow
                    .map {
                        it.filter { feedModel ->
                            feedModel.date in startDate..endDate
                        }
                    }
                    .onStart { emit(cloudData) }
            }
    }
}