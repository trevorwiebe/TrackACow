package com.trevorwiebe.trackacow.presentation.medicated_cows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases.CowUseCases
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.DrugsGivenUseCases
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.presentation.medicated_cows.ui.CowUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class MedicatedCowsViewModel @AssistedInject constructor(
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

    private var drugsGivenAndDrugModelList: List<DrugsGivenAndDrugModel> = mutableListOf()
    private var cowList: List<CowModel> = mutableListOf()

    private val _uiState = MutableStateFlow(MedicatedCowsUiState())
    val uiState: StateFlow<MedicatedCowsUiState> = _uiState.asStateFlow()

    init {
        readDrugsAndDrugsGivenByLotId(lotId)
        readCowsByLotId(lotId)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readDrugsAndDrugsGivenByLotId(lotId: String) {
        val drug = drugsGivenUseCases.readDrugsGivenAndDrugsByLotId(lotId)
        viewModelScope.launch {
            drug.dataFlow.collect { (thisDrugsGivenAndDrugModelList, source) ->
                drugsGivenAndDrugModelList = thisDrugsGivenAndDrugModelList
                        as List<DrugsGivenAndDrugModel>
                _uiState.update {
                    it.copy(
                        cowUiModelList = buildCowUiModelList(drugsGivenAndDrugModelList, cowList),
                        drugDataSource = source,
                        drugIsFetchingFromCloud = drug.isFetchingFromCloud
                    )
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readCowsByLotId(lotId: String) {
        val cow = cowUseCases.readCowsByLotId(lotId)
        viewModelScope.launch {
            cow.dataFlow.collect { (thisCowList, source) ->
                cowList = thisCowList as List<CowModel>
                _uiState.update { uiState ->
                    uiState.copy(
                        cowUiModelList = buildCowUiModelList(drugsGivenAndDrugModelList, cowList),
                        cowDataSource = source,
                        cowIsFetchingFromCloud = cow.isFetchingFromCloud
                    )
                }
            }
        }
    }

    private fun buildCowUiModelList(
        privateDrugsGivenAndDrugList: List<DrugsGivenAndDrugModel>,
        cowList: List<CowModel>
    ): List<CowUiModel>? {

        if (cowList.isEmpty()) return null
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

data class MedicatedCowsUiState(
    val cowUiModelList: List<CowUiModel>? = null,
    val cowDataSource: DataSource = DataSource.Local,
    val cowIsFetchingFromCloud: Boolean = false,
    val drugDataSource: DataSource = DataSource.Local,
    val drugIsFetchingFromCloud: Boolean = false,
    val selectedLot: LotModel? = null,
)