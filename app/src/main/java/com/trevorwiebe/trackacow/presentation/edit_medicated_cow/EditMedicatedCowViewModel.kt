package com.trevorwiebe.trackacow.presentation.edit_medicated_cow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases.CowUseCases
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.DrugsGivenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMedicatedCowViewModel @Inject constructor(
    private val cowUseCases: CowUseCases,
    private val drugsGivenUseCases: DrugsGivenUseCases
) : ViewModel() {

    fun onEvent(event: EditMedicatedCowUiEvent) {
        when (event) {
            is EditMedicatedCowUiEvent.OnCowUpdate -> {
                updateCow(event.cowModel)
            }
            is EditMedicatedCowUiEvent.OnCowDeleted -> {
                deleteCow(event.cowModel)
            }
            is EditMedicatedCowUiEvent.OnDrugsGivenByCowIdDeleted -> {
                deleteDrugsGivenByCowId(event.cowId)
            }
        }
    }

    private fun updateCow(cowModel: CowModel) {
        viewModelScope.launch {
            cowUseCases.updateCow(cowModel)
        }
    }

    private fun deleteCow(cowModel: CowModel) {
        viewModelScope.launch {
            cowUseCases.deleteCow(cowModel)
        }
    }

    private fun deleteDrugsGivenByCowId(cowId: String) {
        viewModelScope.launch {
            drugsGivenUseCases.deleteDrugsGivenByCowId(cowId)
        }
    }

}

sealed class EditMedicatedCowUiEvent {
    data class OnCowUpdate(val cowModel: CowModel) : EditMedicatedCowUiEvent()
    data class OnCowDeleted(val cowModel: CowModel) : EditMedicatedCowUiEvent()
    data class OnDrugsGivenByCowIdDeleted(val cowId: String) : EditMedicatedCowUiEvent()
}