package com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.repository.local.FeedRepository
import com.trevorwiebe.trackacow.domain.utils.addQueryListValueEventListenerFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onStart

data class ReadFeedsByLotId(
    private val feedRepository: FeedRepository,
    private val firebaseDatabase: FirebaseDatabase,
    private val feedDatabaseString: String
){
    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String): Flow<List<FeedModel>> {
        val localFeedFlow = feedRepository.getFeedsByLotId(lotId)

        val feedQuery = firebaseDatabase
            .getReference(feedDatabaseString)
            .orderByChild("lotId")
            .equalTo(lotId)

        val feedCloudFlow = feedQuery.addQueryListValueEventListenerFlow(FeedModel::class.java)

        return localFeedFlow
            .flatMapConcat { localData ->
                feedCloudFlow.onStart { emit(localData) }
            }
    }
}
