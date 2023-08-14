package com.trevorwiebe.trackacow.presentation.edit_medicated_cow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases.CowUseCases
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.DrugsGivenUseCases
import com.trevorwiebe.trackacow.domain.utils.DataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditMedicatedCowViewModel @AssistedInject constructor(
    private val cowUseCases: CowUseCases,
    private val drugsGivenUseCases: DrugsGivenUseCases,
    @Assisted("cowId") private val cowId: String
) : ViewModel() {

    @AssistedFactory
    interface EditMedicatedCowsViewModelFactory {
        fun create(
            @Assisted("cowId") cowId: String
        ): EditMedicatedCowViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun providesFactory(
            assistedFactory: EditMedicatedCowsViewModelFactory,
            cowId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(cowId) as T
            }
        }
    }

    private val _uiState = MutableStateFlow(EditMedicatedCowUiState())
    val uiState: StateFlow<EditMedicatedCowUiState> = _uiState.asStateFlow()

    init {
        readCow(cowId)
        readDrugsGivenByCowId(cowId)
    }

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

    private fun readCow(cowId: String) {
        val cow = cowUseCases.readCowByCowId(cowId)
        viewModelScope.launch {
            cow.dataFlow.collect { (thisCowModel, source) ->
                _uiState.update {
                    it.copy(
                        cowModel = thisCowModel as CowModel,
                        cowDataSource = source,
                        cowIsFetchingFromCloud = cow.isFetchingFromCloud
                    )
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readDrugsGivenByCowId(cowId: String) {
        val drug = drugsGivenUseCases.readDrugsGivenAndDrugsByCowId(listOf(cowId))
        viewModelScope.launch {
            drug.dataFlow.collect { (thisDrugList, source) ->
                _uiState.update {
                    it.copy(
                        drugsGivenList = thisDrugList as List<DrugsGivenAndDrugModel>,
                        drugDataSource = source,
                        drugIsFetchingFromCloud = drug.isFetchingFromCloud
                    )
                }
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

data class EditMedicatedCowUiState(
    val cowModel: CowModel? = null,
    val cowDataSource: DataSource = DataSource.Local,
    val cowIsFetchingFromCloud: Boolean = false,
    val drugsGivenList: List<DrugsGivenAndDrugModel> = emptyList(),
    val drugDataSource: DataSource = DataSource.Local,
    val drugIsFetchingFromCloud: Boolean = false
)

sealed class EditMedicatedCowUiEvent {
    data class OnCowUpdate(val cowModel: CowModel) : EditMedicatedCowUiEvent()
    data class OnCowDeleted(val cowModel: CowModel) : EditMedicatedCowUiEvent()
    data class OnDrugsGivenByCowIdDeleted(val cowId: String) : EditMedicatedCowUiEvent()
}