package com.trevorwiebe.trackacow.presentation.medicated_cows

import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.presentation.medicated_cows.ui.CowUiModel

data class MedicatedCowsUiState(
    val statePenAndLotModel: PenAndLotModel? = null,
    val cowUiModelList: List<CowUiModel>? = null
)
