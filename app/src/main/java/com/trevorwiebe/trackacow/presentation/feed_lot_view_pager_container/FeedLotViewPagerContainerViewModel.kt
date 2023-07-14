package com.trevorwiebe.trackacow.presentation.feed_lot_view_pager_container

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases.RationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FeedLotViewPagerContainerViewModel @Inject constructor(
    private val penUseCases: PenUseCases,
    private val rationUseCases: RationUseCases,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedLotViewPagerContainerUiState())
    val uiState: StateFlow<FeedLotViewPagerContainerUiState> = _uiState.asStateFlow()

    private var penAndLotModelsJob: Job? = null
    private var rationJob: Job? = null

    init {
        getPenAndLotModelsExcludeEmptyPens()
        getRationList()
    }

    private fun getPenAndLotModelsExcludeEmptyPens() {
        penAndLotModelsJob?.cancel()
        penAndLotModelsJob = penUseCases.readPenAndLotModelExcludeEmptyPens()
            .map { thisPenAndLotList ->
                _uiState.update {
                    it.copy(
                        penAndLotList = thisPenAndLotList
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getRationList() {
        rationJob?.cancel()
        rationJob = rationUseCases.readAllRationsUC()
            .map { thisRationList ->
                Log.d("TAG", "getRationList: $thisRationList")
                _uiState.update {
                    it.copy(
                            rationList = thisRationList
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}

data class FeedLotViewPagerContainerUiState(
    val penAndLotList: List<PenAndLotModel> = emptyList(),
    val rationList: List<RationModel> = emptyList(),
)