package com.trevorwiebe.trackacow.presentation.feedlot

import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel

data class FeedLotUiState(
    val callModel: CallModel? = CallModel(0, 0, 0, "", 0, ""),
    val rationList: List<RationModel> = emptyList()
)
