package com.trevorwiebe.trackacow.presentation.medicated_cows

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases.CowUseCases
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.DrugsGivenUseCases
import com.trevorwiebe.trackacow.presentation.medicated_cows.ui.CowUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*


class MedicatedCowsViewModel @AssistedInject constructor(
    private val cowUseCases: CowUseCases,
    private val drugsGivenUseCases: DrugsGivenUseCases,
    @Assisted("penAndLotModel") private val penAndLotModel: PenAndLotModel?
): ViewModel() {

    @AssistedFactory
    interface MedicatedCowsViewModelFactory{
        fun create(
            @Assisted("penAndLotModel") penAndLotModel: PenAndLotModel?
        ): MedicatedCowsViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun providesFactory(
            assistedFactory: MedicatedCowsViewModelFactory,
            penAndLotModel: PenAndLotModel?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(penAndLotModel) as T
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

        _uiState.update {
            it.copy(statePenAndLotModel = penAndLotModel)
        }

        if(penAndLotModel?.lotCloudDatabaseId != null){
            readDrugsAndDrugsGivenByLotId(penAndLotModel.lotCloudDatabaseId!!)
            readCowsByLotId(penAndLotModel.lotCloudDatabaseId!!)
        }

    }

    private fun readDrugsAndDrugsGivenByLotId(lotId: String){
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

    private fun readCowsByLotId(lotId: String){
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
        for(i in cowList.indices){
            val cowUiModel = CowUiModel(
                cowList[i],
                privateDrugsGivenAndDrugList.filter { it.drugsGivenCowId == cowList[i].cowId }
            )
            cowUiModelList.add(cowUiModel)
        }
        return cowUiModelList
    }
}