package com.trevorwiebe.trackacow.presentation.fragment_move

import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel

data class MoveUiState(
    val penAndLotList: List<PenAndLotModel> = emptyList()
)
