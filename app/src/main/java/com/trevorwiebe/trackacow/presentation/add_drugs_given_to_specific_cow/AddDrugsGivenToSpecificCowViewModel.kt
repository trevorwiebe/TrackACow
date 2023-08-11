package com.trevorwiebe.trackacow.presentation.add_drugs_given_to_specific_cow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.DrugUseCases
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.DrugsGivenUseCases
import com.trevorwiebe.trackacow.domain.utils.DataSource
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun onEvent(event: AddDrugsGivenToSpecificCowEvent) {
        when (event) {
            is AddDrugsGivenToSpecificCowEvent.OnDrugListCreated -> {
                createDrugList(event.drugGivenList)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readDrugs() {
        val dataFlow = drugsUseCases.readDrugsUC().dataFlow
        viewModelScope.launch {
            dataFlow.collect { (drugList, source) ->
                _uiState.update { uiState ->
                    uiState.copy(
                        drugsList = drugList as List<DrugModel>,
                        dataSource = source,
                        isFetchingFromCloud =
                        drugsUseCases.readDrugsUC().isFetchingFromCloud
                    )
                }
            }
        }
    }

    private fun createDrugList(drugGivenList: List<DrugGivenModel>) {
        viewModelScope.launch {
            drugsGivenUseCases.createDrugsGivenList(drugGivenList)
        }
    }

}

data class AddDrugsGivenToSpecificCowUiState(
    val drugsList: List<DrugModel> = emptyList(),
    val dataSource: DataSource = DataSource.Local,
    val isFetchingFromCloud: Boolean = false
)

sealed class AddDrugsGivenToSpecificCowEvent {
    data class OnDrugListCreated(val drugGivenList: List<DrugGivenModel>) :
        AddDrugsGivenToSpecificCowEvent()
}