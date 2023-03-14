package com.trevorwiebe.trackacow.presentation.add_drugs_given_to_specific_cow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.DrugUseCases
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.DrugsGivenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDrugsGivenToSpecificCowViewModel @Inject constructor(
    private val drugsUseCases: DrugUseCases,
    private val drugsGivenUseCases: DrugsGivenUseCases
) : ViewModel() {

    init {
        readDrugs()
    }

    private val _uiState = MutableStateFlow(AddDrugsGivenToSpecificCowUiState())
    val uiState: StateFlow<AddDrugsGivenToSpecificCowUiState> = _uiState.asStateFlow()

    private var drugsJob: Job? = null

    fun onEvent(event: AddDrugsGivenToSpecificCowEvent) {
        when (event) {
            is AddDrugsGivenToSpecificCowEvent.OnDrugListCreated -> {
                createDrugList(event.drugGivenList)
            }
        }
    }

    private fun readDrugs() {
        drugsJob?.cancel()
        drugsJob = drugsUseCases.readDrugsUC()
            .map { thisDrugList ->
                _uiState.update {
                    it.copy(
                        drugsList = thisDrugList
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun createDrugList(drugGivenList: List<DrugGivenModel>) {
        viewModelScope.launch {
            drugsGivenUseCases.createDrugsGivenList(drugGivenList)
        }
    }

}

data class AddDrugsGivenToSpecificCowUiState(
    val drugsList: List<DrugModel> = emptyList()
)

sealed class AddDrugsGivenToSpecificCowEvent {
    data class OnDrugListCreated(val drugGivenList: List<DrugGivenModel>) :
        AddDrugsGivenToSpecificCowEvent()
}