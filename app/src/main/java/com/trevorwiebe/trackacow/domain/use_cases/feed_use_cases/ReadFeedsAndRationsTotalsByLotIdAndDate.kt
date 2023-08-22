package com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toFeedAndRationModel
import com.trevorwiebe.trackacow.domain.models.compound_model.FeedAndRationModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.repository.local.FeedRepository
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import com.trevorwiebe.trackacow.domain.repository.remote.FeedRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.SourceIdentifiedListFlow
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadFeedsAndRationsTotalsByLotIdAndDate(
    private val feedRepository: FeedRepository,
    private val rationRepository: RationsRepository,
    private val feedRepositoryRemote: FeedRepositoryRemote,
    private val context: Application
) {

    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String, startDate: Long, endDate: Long): SourceIdentifiedListFlow {
        val localFeedAndRationFlow = feedRepository.readFeedsAndRationTotalByLotIdAndDate(
            lotId, startDate, endDate
        ).map { feedAndRation -> feedAndRation to DataSource.Local }
        val cloudFeedAndRationFlow = feedRepositoryRemote.readFeedsAndRationsByLotId(lotId)
            .map { feedAndRation -> feedAndRation to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)

        val flowResults = if (isFetchingFromCloud) {
            localFeedAndRationFlow.flatMapConcat { (localData, source) ->
                cloudFeedAndRationFlow.flatMapConcat { (pair, source) ->
                    feedRepository.insertOrUpdateFeedList(pair.second)
                    rationRepository.syncCloudRationListToDatabase(pair.first)
                    flow {
                        val combineList = combineList(pair.second, pair.first, startDate, endDate)
                        emit(combineList to source)
                    }
                }.onStart { emit(localData to source) }
            }
        } else {
            localFeedAndRationFlow
        }

        return SourceIdentifiedListFlow(flowResults, isFetchingFromCloud)
    }
}

private fun combineList(
    feedList: List<FeedModel>,
    rationList: List<RationModel>,
    startDate: Long,
    endDate: Long
): List<FeedAndRationModel> {
    val result = mutableListOf<FeedAndRationModel>()
    feedList.forEach { feedModel ->
        val ration = rationList.find { it.rationCloudDatabaseId == feedModel.rationCloudId }
        val feedAndRationModel = feedModel.toFeedAndRationModel(ration)
        if (feedAndRationModel.date in startDate..endDate) {
            result.add(feedAndRationModel)
        }
    }
    val result2 = result.groupBy { it.rationName }
        .map { (rationName, feedAndRationModel) ->
            FeedAndRationModel(
                feedAndRationModel.sumOf { it.feed }, 0, "", "", rationName
            )
        }
    return result2
}