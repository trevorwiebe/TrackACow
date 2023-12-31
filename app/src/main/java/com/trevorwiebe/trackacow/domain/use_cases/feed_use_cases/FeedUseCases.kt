package com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases

data class FeedUseCases(
        val readFeedsByLotId: ReadFeedsByLotId,
        val readFeedsByLotIdAndDate: ReadFeedsByLotIdAndDate,
        val readFeedsAndRationsTotalsByLotIdAndDate: ReadFeedsAndRationsTotalsByLotIdAndDate,
        val createAndUpdateFeedList: CreateAndUpdateFeedList
)
