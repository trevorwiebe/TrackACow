package com.trevorwiebe.trackacow.presentation.fragment_medicate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import com.trevorwiebe.trackacow.domain.utils.DataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicateListViewModel @Inject constructor(
    private val penUseCases: PenUseCases
): ViewModel() {

    private val _uiState = MutableStateFlow(MedicateListUiState())
    val uiState: StateFlow<MedicateListUiState> = _uiState.asStateFlow()

    init {
        getPensAndLots()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getPensAndLots() {
        val pen = penUseCases.readPenAndLotModelIncludeEmptyPens()
        viewModelScope.launch {
            pen.dataFlow.collect { (thisPenAndLotList, source) ->
                _uiState.update { uiState ->
                    uiState.copy(
                        penAndLotModelList = thisPenAndLotList as List<PenAndLotModel>,
                        isFetchingFromCloud = pen.isFetchingFromCloud,
                        dataSource = source
                    )
                }
            }
        }
    }

}

data class MedicateListUiState(
    val penAndLotModelList: List<PenAndLotModel> = emptyList(),
    val isFetchingFromCloud: Boolean = false,
    val dataSource: DataSource = DataSource.Local
)