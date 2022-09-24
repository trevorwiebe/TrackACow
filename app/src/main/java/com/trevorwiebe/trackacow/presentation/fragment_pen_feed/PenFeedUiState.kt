package com.trevorwiebe.trackacow.presentation.fragment_pen_feed

import com.trevorwiebe.trackacow.domain.models.call.CallModel

data class PenFeedUiState (
    val callList: List<CallModel> = emptyList()
)