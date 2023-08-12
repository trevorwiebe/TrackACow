package com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.FeedRepository
import com.trevorwiebe.trackacow.domain.repository.remote.FeedRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.SourceIdentifiedListFlow
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.flow.map

data class ReadFeedsAndRationsTotalsByLotIdAndDate(
        private val feedRepository: FeedRepository,
        private val feedRepositoryRemote: FeedRepositoryRemote,
        private val context: Application
) {

    // TODO: update this to pull from cloud
    operator fun invoke(lotId: String, startDate: Long, endDate: Long): SourceIdentifiedListFlow {
        val localFeedAndRationFlow = feedRepository.readFeedsAndRationTotalByLotIdAndDate(
                lotId, startDate, endDate
        ).map { feedAndRation -> feedAndRation to DataSource.Local }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)

        return SourceIdentifiedListFlow(localFeedAndRationFlow, isFetchingFromCloud)
    }

}