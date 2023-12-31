package com.trevorwiebe.trackacow.presentation.fragment_report

import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.utils.DataSource

data class ReportsUiState(
    val lotList: List<LotModel> = emptyList(),
    val dataSource: DataSource = DataSource.Local,
    val isFetchingFromCloud: Boolean = false
)