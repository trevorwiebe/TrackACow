package com.trevorwiebe.trackacow.presentation.fragment_pen_feed

import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel

data class FeedPenListUiState (
    val callList: List<CallModel> = emptyList(),
    val lotList: List<LotModel> = emptyList()
)