package com.trevorwiebe.trackacow.presentation.manage_rations

import com.trevorwiebe.trackacow.domain.models.ration.RationModel

data class ManageRationsUiState(
    val rationsList: List<RationModel> = emptyList()
)
