package com.trevorwiebe.trackacow.presentation.archives

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ArchivesViewModel @Inject constructor(
    private val lotUseCases: LotUseCases,
) : ViewModel() {

    init {
        readArchivedLots()
    }

    private val _uiState = MutableStateFlow(ArchivesUiState())
    val uiState: StateFlow<ArchivesUiState> = _uiState.asStateFlow()

    private var archivedLotJob: Job? = null

    private fun readArchivedLots() {
        archivedLotJob?.cancel()
        archivedLotJob = lotUseCases.readArchivedLots()
            .map { thisArchivedLotList ->
                _uiState.update {
                    it.copy(
                        archivedLotList = thisArchivedLotList
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}

data class ArchivesUiState(
    val archivedLotList: List<LotModel> = emptyList()
)