package com.trevorwiebe.trackacow.presentation.drugs_given_reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.use_cases.CalculateDrugsGiven
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.DrugsGivenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class DrugsGivenViewModel @Inject constructor(
    private val drugsGivenUseCases: DrugsGivenUseCases,
    private val calculateDrugsGiven: CalculateDrugsGiven
) : ViewModel() {

    private val _uiState = MutableStateFlow(DrugsGivenUiState())
    val uiState: StateFlow<DrugsGivenUiState> = _uiState.asStateFlow()

    private var drugsGivenJob: Job? = null

    fun onEvent(event: DrugsGivenUiEvent) {
        when (event) {
            is DrugsGivenUiEvent.OnAllClicked -> {

            }
            is DrugsGivenUiEvent.CustomDateSelected -> {
                getDrugsGivenByDates(event.lotId, event.startDate, event.endDate)
            }
        }
    }

    // TODO: add progress bar
    @Suppress("UNCHECKED_CAST")
    private fun getDrugsGivenByDates(lotId: String, startDate: Long, endDate: Long) {
        drugsGivenJob?.cancel()
        drugsGivenJob =
            drugsGivenUseCases.readDrugsGivenAndDrugsByLotIdAndDate(
                lotId,
                startDate,
                endDate
            ).dataFlow
                .map { (thisDrugsAndDrugsModelList, source) ->
                    _uiState.update {
                        it.copy(
                            drugsGivenAndDrugList = calculateDrugsGiven.invoke(
                                thisDrugsAndDrugsModelList as List<DrugsGivenAndDrugModel>
                            )
                        )
                    }
                }
                .launchIn(viewModelScope)
    }
}

data class DrugsGivenUiState(
    val drugsGivenAndDrugList: List<DrugsGivenAndDrugModel> = emptyList()
)

sealed class DrugsGivenUiEvent {
    object OnAllClicked : DrugsGivenUiEvent()
    data class CustomDateSelected(val lotId: String, val startDate: Long, val endDate: Long) :
        DrugsGivenUiEvent()
}