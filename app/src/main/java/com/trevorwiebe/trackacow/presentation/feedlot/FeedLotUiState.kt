package com.trevorwiebe.trackacow.presentation.feedlot

import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel

data class FeedLotUiState(
    val callModel: CallAndRationModel? = CallAndRationModel(
        0, 0, 0, "", 0, "", 0, "", ""
    ),
    val rationList: List<RationModel> = emptyList(),
    val lastUsedRationId: Int = -1
)
