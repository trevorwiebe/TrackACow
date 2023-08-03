package com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.repository.local.FeedRepository
import com.trevorwiebe.trackacow.domain.repository.remote.FeedRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadFeedsByLotIdAndDate(
        private val feedRepository: FeedRepository,
        private val feedRepositoryRemote: FeedRepositoryRemote,
        private val context: Application
) {

    // TODO: update this with data source identification

    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String, startDate: Long, endDate: Long): Flow<List<FeedModel>> {
        val localFeedFlow = feedRepository.readFeedsByLotIdAndDate(lotId, startDate, endDate)
        val cloudFeedFlow = feedRepositoryRemote.readFeedsByLotId(lotId)

        return if (Utility.haveNetworkConnection(context)) {
            localFeedFlow.flatMapConcat { localData ->
                cloudFeedFlow
                    .map { feedList ->
                            feedRepository.insertOrUpdateFeedList(feedList)
                            feedList.filter { feedModel ->
                                feedModel.date in startDate..endDate
                            }
                        }
                        .onStart { emit(localData) }
            }
        } else {
            localFeedFlow
        }
    }
}