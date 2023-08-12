package com.trevorwiebe.trackacow.presentation.archives

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import com.trevorwiebe.trackacow.domain.utils.DataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    @Suppress("UNCHECKED_CAST")
    private fun readArchivedLots() {
        val readLots = lotUseCases.readArchivedLots()
        viewModelScope.launch {
            readLots.dataFlow.map { (thisArchivedLotList, source) ->
                _uiState.update {
                    it.copy(
                            archivedLotList = thisArchivedLotList as List<LotModel>,
                            dataSource = source,
                            isFetchingFromCloud = readLots.isFetchingFromCloud
                    )
                }
            }
        }
    }
}

data class ArchivesUiState(
        val archivedLotList: List<LotModel> = emptyList(),
        val dataSource: DataSource = DataSource.Local,
        val isFetchingFromCloud: Boolean = false
)