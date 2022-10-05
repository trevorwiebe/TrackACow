package com.trevorwiebe.trackacow.presentation.fragment_pen_feed.ui_model

import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel

data class FeedPenListUiModel(
    val date: Long,
    val callModel: CallModel,
    val feedList: List<FeedModel>
)