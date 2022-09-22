package com.trevorwiebe.trackacow.presentation.manage_rations

import com.trevorwiebe.trackacow.domain.models.RationModel

data class ManageRationsUiState(
    val rationsList: List<RationModel> = emptyList()
)
