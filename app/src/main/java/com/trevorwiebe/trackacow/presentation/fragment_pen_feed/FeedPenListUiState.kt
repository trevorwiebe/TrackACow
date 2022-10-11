package com.trevorwiebe.trackacow.presentation.fragment_pen_feed

import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.presentation.fragment_pen_feed.ui_model.FeedPenListUiModel

data class FeedPenListUiState (
    val feedPenUiList: List<FeedPenListUiModel> = emptyList(),
    val lotList: List<LotModel> = emptyList(),
    val isLoading: Boolean = true
)