package com.trevorwiebe.trackacow.presentation.medicated_cows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases.CowUseCases
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.DrugsGivenUseCases
import com.trevorwiebe.trackacow.domain.use_cases.load_use_cases.LoadUseCases
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import com.trevorwiebe.trackacow.presentation.medicated_cows.ui.CowUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class MedicatedCowsViewModel @AssistedInject constructor(
    private val lotUseCases: LotUseCases,
    private val loadUseCases: LoadUseCases,
    private val cowUseCases: CowUseCases,
    private val drugsGivenUseCases: DrugsGivenUseCases,
    @Assisted("lotId") private val lotId: String
): ViewModel() {

    @AssistedFactory
    interface MedicatedCowsViewModelFactory{
        fun create(
            @Assisted("lotId") lotId: String
        ): MedicatedCowsViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun providesFactory(
            assistedFactory: MedicatedCowsViewModelFactory,
            lotId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(lotId) as T
            }
        }
    }

    private var cowJob: Job? = null
    private var drugsJob: Job? = null

    private var drugsGivenAndDrugModelList: List<DrugsGivenAndDrugModel> = mutableListOf()
    private var cowList: List<CowModel> = mutableListOf()

    private val _uiState = MutableStateFlow(MedicatedCowsUiState())
    val uiState: StateFlow<MedicatedCowsUiState> = _uiState.asStateFlow()

    init {
        readDrugsAndDrugsGivenByLotId(lotId)
        readCowsByLotId(lotId)
    }

    fun onEvent(event: MedicatedCowsEvents) {
        when (event) {
            is MedicatedCowsEvents.OnPenFilled -> {
                onPenFilled(event.lotModel, event.loadModel)
            }
        }
    }

    private fun readDrugsAndDrugsGivenByLotId(lotId: String) {
        drugsJob?.cancel()
        drugsJob = drugsGivenUseCases.readDrugsGivenAndDrugsByLotId(lotId)
            .map { thisDrugsGivenAndDrugModelList ->
                drugsGivenAndDrugModelList = thisDrugsGivenAndDrugModelList
                _uiState.update {
                    it.copy(
                        cowUiModelList = buildCowUiModelList(drugsGivenAndDrugModelList, cowList)
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun onPenFilled(lotModel: LotModel, loadModel: LoadModel) {
        viewModelScope.launch {
            val lotId = lotUseCases.createLot(lotModel)
            loadModel.lotId = lotId
            loadUseCases.createLoad(loadModel)
        }
    }

    private fun readCowsByLotId(lotId: String) {
        cowJob?.cancel()
        cowJob = cowUseCases.readCowsByLotId(lotId)
            .map { thisCowList ->
                cowList = thisCowList
                _uiState.update {
                    it.copy(
                        cowUiModelList = buildCowUiModelList(drugsGivenAndDrugModelList, cowList)
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun buildCowUiModelList(
        privateDrugsGivenAndDrugList: List<DrugsGivenAndDrugModel>,
        cowList: List<CowModel>
    ): List<CowUiModel>? {

        if(cowList.isEmpty() || privateDrugsGivenAndDrugList.isEmpty()) return null
        val cowUiModelList: MutableList<CowUiModel> = mutableListOf()
        for (i in cowList.indices) {
            val cowUiModel = CowUiModel(
                cowList[i],
                privateDrugsGivenAndDrugList.filter { it.drugsGivenCowId == cowList[i].cowId }
            )
            cowUiModelList.add(cowUiModel)
        }
        return cowUiModelList
    }
}

sealed class MedicatedCowsEvents {
    data class OnPenFilled(val lotModel: LotModel, val loadModel: LoadModel) : MedicatedCowsEvents()
}