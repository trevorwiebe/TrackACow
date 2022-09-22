package com.trevorwiebe.trackacow.presentation.add_or_edit_rations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.RationModel
import com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases.RationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOrEditViewModel @Inject constructor(
    private val rationUseCases: RationUseCases
): ViewModel() {

    fun onEvent(event: AddOrEditRationsEvents){
        when(event){
            is AddOrEditRationsEvents.OnRationAdded -> {
                addRation(event.rationModel)
            }
        }
    }

    private fun addRation(rationModel: RationModel){
        viewModelScope.launch {
            rationUseCases.addRationUC(rationModel)
        }
    }
}

sealed class AddOrEditRationsEvents{
    data class OnRationAdded(val rationModel: RationModel): AddOrEditRationsEvents()
}