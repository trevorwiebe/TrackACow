package com.trevorwiebe.trackacow.presentation.fragment_feed

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
class FeedContainerViewModel @Inject constructor(
    private val penUseCases: PenUseCases
): ViewModel() {

    private val _uiState = MutableStateFlow(FeedContainerUiState())
    val uiState: StateFlow<FeedContainerUiState> = _uiState.asStateFlow()

    init {
        getPenAndLotModels()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getPenAndLotModels() {
        val pen = penUseCases.readPenAndLotModelIncludeEmptyPens()
        viewModelScope.launch {
            pen.dataFlow.collect { (penAndLotList, source) ->
                _uiState.update { uiState ->
                    uiState.copy(
                        penAndLotList = penAndLotList as List<PenAndLotModel>,
                        dataSource = source,
                        isFetchingFromCloud = pen.isFetchingFromCloud
                    )
                }
            }
        }
    }
}

data class FeedContainerUiState(
    val penAndLotList: List<PenAndLotModel> = emptyList(),
    val dataSource: DataSource = DataSource.Local,
    val isFetchingFromCloud: Boolean = false
)