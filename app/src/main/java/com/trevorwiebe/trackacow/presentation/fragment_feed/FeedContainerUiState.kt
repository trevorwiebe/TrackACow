package com.trevorwiebe.trackacow.presentation.fragment_feed

import com.trevorwiebe.trackacow.data.entities.LotEntity
import com.trevorwiebe.trackacow.data.entities.PenEntity
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel

data class FeedContainerUiState (
    val penAndLotList: List<PenAndLotModel> = emptyList()
)