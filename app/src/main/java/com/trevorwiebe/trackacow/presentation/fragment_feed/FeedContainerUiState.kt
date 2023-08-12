package com.trevorwiebe.trackacow.presentation.fragment_feed

import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel

data class FeedContainerUiState (
    val penAndLotList: List<PenAndLotModel> = emptyList(),
    val isLoading: Boolean = false
)