package com.trevorwiebe.trackacow.presentation.medicated_cows

import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.presentation.medicated_cows.ui.CowUiModel

data class MedicatedCowsUiState(
    val cowUiModelList: List<CowUiModel>? = null,
    val selectedLot: LotModel? = null,
)
