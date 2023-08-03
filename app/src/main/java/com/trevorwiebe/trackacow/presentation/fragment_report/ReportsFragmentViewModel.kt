package com.trevorwiebe.trackacow.presentation.fragment_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsFragmentViewModel @Inject constructor(
    private val lotUseCases: LotUseCases
): ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        getLots()
    }

    private fun getLots() {
        val dataFlow = lotUseCases.readLots().dataFlow
        viewModelScope.launch {
            dataFlow.collect { (lotList, source) ->
                _uiState.update {
                    it.copy(
                        lotList = lotList as List<LotModel>,
                        dataSource = source
                    )
                }
            }
        }

        _uiState.update { it.copy(isFetchingFromCloud = lotUseCases.readLots().isFetchingFromCloud) }
    }

}