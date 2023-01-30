package com.trevorwiebe.trackacow.presentation.edit_drugs_given_to_specific

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.DrugsGivenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDrugsGivenToSpecificCowViewModel @Inject constructor(
    private val drugsGivenUseCase: DrugsGivenUseCases,
) : ViewModel() {

    fun onEvent(event: EditDrugsGivenToSpecificCowEvents) {
        when (event) {
            is EditDrugsGivenToSpecificCowEvents.OnDrugGivenDelete -> {
                deleteDrugGiven(event.drugGivenModel)
            }
            is EditDrugsGivenToSpecificCowEvents.OnDrugGivenEdited -> {
                editDrugGiven(event.drugGivenModel)
            }
        }
    }

    private fun deleteDrugGiven(drugGivenModel: DrugGivenModel?) {
        if (drugGivenModel != null) {
            viewModelScope.launch {
                drugsGivenUseCase.deleteDrugGivenByDrugGivenId(drugGivenModel)
            }
        }
    }

    private fun editDrugGiven(drugGivenModel: DrugGivenModel?) {
        if (drugGivenModel != null) {
            viewModelScope.launch {
                drugsGivenUseCase.updateDrugGiven(drugGivenModel)
            }
        }
    }
}

sealed class EditDrugsGivenToSpecificCowEvents {
    data class OnDrugGivenEdited(var drugGivenModel: DrugGivenModel?) :
        EditDrugsGivenToSpecificCowEvents()

    data class OnDrugGivenDelete(var drugGivenModel: DrugGivenModel?) :
        EditDrugsGivenToSpecificCowEvents()
}