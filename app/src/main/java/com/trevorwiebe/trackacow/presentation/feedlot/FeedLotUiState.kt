package com.trevorwiebe.trackacow.presentation.feedlot

import com.trevorwiebe.trackacow.domain.models.call.CallModel

data class FeedLotUiState(
    val callModel: CallModel? = CallModel(0, 0, 0, "", "")
)
