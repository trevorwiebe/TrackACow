package com.trevorwiebe.trackacow.presentation.medicated_cows

import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.presentation.medicated_cows.ui.CowUiModel

data class MedicatedCowsUiState(
    val cowUiModelList: List<CowUiModel>? = null,
    val cowDataSource: DataSource = DataSource.Local,
    val isFetchingCowFromCloud: Boolean = false,
    val selectedLot: LotModel? = null,
    val lotDataSource: DataSource = DataSource.Local,
    val isFetchingLotFromCloud: Boolean = false
)
