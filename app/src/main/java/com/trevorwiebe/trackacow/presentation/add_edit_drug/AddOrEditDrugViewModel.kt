package com.trevorwiebe.trackacow.presentation.add_edit_drug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.DrugUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOrEditDrugViewModel @Inject constructor(
    private val drugUseCases: DrugUseCases
): ViewModel() {

    fun onEvent(event: AddOrEditDrugUiEvent){
        when (event) {
            is AddOrEditDrugUiEvent.OnDrugAdded -> {
                createDrug(event.drugModel)
            }
            is AddOrEditDrugUiEvent.OnDrugUpdated -> {
                updateDrug(event.drugModel)
            }
            is AddOrEditDrugUiEvent.OnDrugDeleted -> {
                deleteDrug(event.drugModel)
            }
        }
    }

    private fun createDrug(drugModel: DrugModel){
        viewModelScope.launch {
            drugUseCases.createDrug(drugModel)
        }
    }

    private fun updateDrug(drugModel: DrugModel) {
        viewModelScope.launch {
            drugUseCases.updateDrug(drugModel)
        }
    }

    private fun deleteDrug(drugModel: DrugModel) {
        viewModelScope.launch {
            drugUseCases.deleteDrug(drugModel)
        }
    }

}

sealed class AddOrEditDrugUiEvent{
    data class OnDrugAdded(val drugModel: DrugModel): AddOrEditDrugUiEvent()
    data class OnDrugUpdated(val drugModel: DrugModel) : AddOrEditDrugUiEvent()
    data class OnDrugDeleted(val drugModel: DrugModel) : AddOrEditDrugUiEvent()
}