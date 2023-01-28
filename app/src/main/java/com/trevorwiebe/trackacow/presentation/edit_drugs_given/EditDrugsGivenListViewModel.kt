package com.trevorwiebe.trackacow.presentation.edit_drugs_given

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.DrugsGivenUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

class EditDrugsGivenListViewModel @AssistedInject constructor(
    private val drugsGivenUseCases: DrugsGivenUseCases,
    @Assisted("cowId") private val cowId: String
) : ViewModel() {

    @AssistedFactory
    interface EditDrugsGivenViewModelFactory {
        fun create(
            @Assisted("cowId") cowId: String
        ): EditDrugsGivenListViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun providesFactory(
            assistedFactory: EditDrugsGivenViewModelFactory,
            cowId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(cowId) as T
            }
        }
    }

    private var drugsGivenJob: Job? = null

    private val _uiState = MutableStateFlow(EditDrugsGivenUiState())
    val uiState: StateFlow<EditDrugsGivenUiState> = _uiState.asStateFlow()

    init {
        getDrugsAndDrugsGivenByCowId(cowId)
    }

    private fun getDrugsAndDrugsGivenByCowId(cowId: String) {
        drugsGivenJob?.cancel()
        drugsGivenJob = drugsGivenUseCases.readDrugsGivenAndDrugsByCowId(cowId)
            .map { thisDrugList ->
                _uiState.update {
                    it.copy(drugsGivenAndDrugsList = thisDrugList)
                }
            }
            .launchIn(viewModelScope)
    }

}

data class EditDrugsGivenUiState(
    val drugsGivenAndDrugsList: List<DrugsGivenAndDrugModel> = emptyList()
)