package com.trevorwiebe.trackacow.presentation.fragment_pen_feed

import com.trevorwiebe.trackacow.domain.models.call.CallModel

data class FeedPenListUiState (
    val callList: List<CallModel> = emptyList()
)