package com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.FeedRepository
import com.trevorwiebe.trackacow.domain.repository.remote.FeedRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.SourceIdentifiedListFlow
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadFeedsByLotId(
        private val feedRepository: FeedRepository,
        private val feedRepositoryRemote: FeedRepositoryRemote,
        private val context: Application
){

    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String): SourceIdentifiedListFlow {

        val localFeedFlow = feedRepository.getFeedsByLotId(lotId)
            .map { feedList -> feedList to DataSource.Local }
        val cloudFeedFlow = feedRepositoryRemote.readFeedsByLotId(lotId)
            .map { feedList -> feedList to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)

        val resultsFlow = if (isFetchingFromCloud) {
            localFeedFlow.flatMapConcat { (localData, fromLocalFlowSource) ->
                cloudFeedFlow.onStart {
                    emit(localData to fromLocalFlowSource)
                }.map { (feedList, fromCloudFlowSource) ->
                    if (fromCloudFlowSource == DataSource.Cloud) {
                        feedRepository.syncCloudFeedByLotId(feedList, lotId)
                    }
                    feedList to fromCloudFlowSource
                }
            }
        } else {
            localFeedFlow
        }

        return SourceIdentifiedListFlow(resultsFlow, isFetchingFromCloud)
    }
}
