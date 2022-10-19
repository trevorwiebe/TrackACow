package com.trevorwiebe.trackacow.presentation.fragment_report

import com.trevorwiebe.trackacow.domain.models.lot.LotModel

data class ReportsUiState (
    val lotList: List<LotModel> = emptyList()
)