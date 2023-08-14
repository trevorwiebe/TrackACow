package com.trevorwiebe.trackacow.presentation.edit_drugs_given

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
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

class EditDrugsGivenListViewModel @AssistedInject constructor(
        private val drugsGivenUseCases: DrugsGivenUseCases,
        @Assisted("cowId") private val cowId: String
) : ViewModel() {

    init {
        readDrugsGivenByCowId(cowId)
    }

    @AssistedFactory
    interface EditDrugsGivenListViewModelFactory {
        fun create(
                @Assisted("cowId") cowId: String
        ): EditDrugsGivenListViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun providesFactory(
                assistedFactory: EditDrugsGivenListViewModelFactory,
                cowId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(cowId) as T
            }
        }
    }

    private val _uiState = MutableStateFlow(EditDrugsGivenListUiState())
    val uiState: StateFlow<EditDrugsGivenListUiState> = _uiState.asStateFlow()

    @Suppress("UNCHECKED_CAST")
    private fun readDrugsGivenByCowId(cowId: String) {
        val drugUses = drugsGivenUseCases.readDrugsGivenAndDrugsByCowId(listOf(cowId))
        viewModelScope.launch {
            drugUses.dataFlow.collect { (thisDrugList, source) ->
                _uiState.update {
                    it.copy(
                        drugsGivenList = thisDrugList as List<DrugsGivenAndDrugModel>,
                        dataSource = source,
                        isFetchingFromCloud = drugUses.isFetchingFromCloud
                    )
                }
            }
        }
    }

    data class EditDrugsGivenListUiState(
        val drugsGivenList: List<DrugsGivenAndDrugModel> = emptyList(),
        val isFetchingFromCloud: Boolean = false,
        val dataSource: DataSource = DataSource.Local
    )
}