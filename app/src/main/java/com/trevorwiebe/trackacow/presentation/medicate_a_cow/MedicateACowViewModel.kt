package com.trevorwiebe.trackacow.presentation.medicate_a_cow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases.CowUseCases
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.DrugUseCases
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.DrugsGivenUseCases
import com.trevorwiebe.trackacow.domain.utils.DataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MedicateACowViewModel @AssistedInject constructor(
    private val cowUseCases: CowUseCases,
    private val drugUseCases: DrugUseCases,
    private val drugsGivenUseCases: DrugsGivenUseCases,
    @Assisted("penAndLotModel") private val penAndLotModel: PenAndLotModel
) : ViewModel() {

    @AssistedFactory
    interface MedicateACowViewModelFactory {
        fun create(
            @Assisted("penAndLotModel") penAndLotModel: PenAndLotModel?
        ): MedicateACowViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun providesFactory(
            assistedFactory: MedicateACowViewModelFactory,
            penAndLotModel: PenAndLotModel?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(penAndLotModel) as T
            }
        }
    }

    private var drugsGivenByCows: Job? = null

    private val _uiState = MutableStateFlow(MedicateACowUiState())
    val uiState: StateFlow<MedicateACowUiState> = _uiState.asStateFlow()

    init {
        readDrugs()
        readCowsByLotId(penAndLotModel.lotCloudDatabaseId ?: "")
    }

    fun onEvent(event: MedicateACowEvent) {
        when (event) {
            is MedicateACowEvent.OnCowFound -> {
                updateStateWithCowFoundList(event.cowModelList)
            }

            is MedicateACowEvent.OnCowAndDrugListCreated -> {
                createCowAndDrugList(event.cowModel, event.drugsGivenList)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readDrugs() {
        val dataFlow = drugUseCases.readDrugsUC().dataFlow
        viewModelScope.launch {
            dataFlow.collect { (drugList, source) ->
                _uiState.update { uiState ->
                    uiState.copy(
                        drugList = drugList as List<DrugModel>,
                        drugDataSource = source
                    )
                }
            }
        }
        _uiState.update { it.copy(isFetchingDrugsFromCloud = drugUseCases.readDrugsUC().isFetchingFromCloud) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readCowsByLotId(lotId: String) {
        val dataFlow = cowUseCases.readCowsByLotId(lotId).dataFlow
        viewModelScope.launch {
            dataFlow.collect { (cowList, source) ->
                _uiState.update { uiState ->
                    uiState.copy(
                        medicatedCowList = cowList as List<CowModel>,
                        cowDataSource = source
                    )
                }
            }
        }
        _uiState.update { it.copy(isFetchingCowsFromCloud = cowUseCases.readCowsByLotId(lotId).isFetchingFromCloud) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateStateWithCowFoundList(cowList: List<CowModel>) {
        _uiState.update {
            it.copy(cowFoundList = cowList)
        }
        if (cowList.isNotEmpty()) {
            val cowIdList = cowList.map { it.cowId }
            drugsGivenByCows?.cancel()
            drugsGivenByCows = drugsGivenUseCases.readDrugsGivenAndDrugsByCowId(cowIdList).dataFlow
                .map { (thisDrugAndDrugGivenList, source) ->
                    _uiState.update {
                        it.copy(drugAndDrugGivenListForFoundCows = thisDrugAndDrugGivenList as List<DrugsGivenAndDrugModel>)
                    }
                }
                .launchIn(viewModelScope)
        } else {
            _uiState.update {
                it.copy(drugAndDrugGivenListForFoundCows = emptyList())
            }
        }
    }

    private fun createCowAndDrugList(cowModel: CowModel, drugList: List<DrugGivenModel>) {
        viewModelScope.launch {
            val cowId = cowUseCases.createCow(cowModel)
            drugList.map { it.drugsGivenCowId = cowId }
            drugsGivenUseCases.createDrugsGivenList(drugList)
        }
    }

}

data class MedicateACowUiState(
    val drugList: List<DrugModel> = emptyList(),
    val isFetchingDrugsFromCloud: Boolean = false,
    val drugDataSource: DataSource = DataSource.Local,
    val cowFoundList: List<CowModel> = emptyList(),
    val medicatedCowList: List<CowModel> = emptyList(),
    val isFetchingCowsFromCloud: Boolean = false,
    val cowDataSource: DataSource = DataSource.Local,
    val drugAndDrugGivenListForFoundCows: List<DrugsGivenAndDrugModel> = emptyList()
)

sealed class MedicateACowEvent {
    data class OnCowAndDrugListCreated(
        val cowModel: CowModel,
        val drugsGivenList: List<DrugGivenModel>
    ) : MedicateACowEvent()

    data class OnCowFound(val cowModelList: List<CowModel>) : MedicateACowEvent()
}