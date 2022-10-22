package com.trevorwiebe.trackacow.presentation.lot_reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.DrugUseCases
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import com.trevorwiebe.trackacow.domain.utils.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*

class LotReportViewModel @AssistedInject constructor(
    private val lotUseCases: LotUseCases,
    private val drugUseCases: DrugUseCases,
    @Assisted("reportType") private val reportType: Int,
    @Assisted("lotId") private val lotId: Int
): ViewModel() {

    @AssistedFactory
    interface LotReportViewModelFactory{
        fun create(
            @Assisted("reportType") reportType: Int,
            @Assisted("lotId") lotId: Int
        ): LotReportViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun providesFactory(
            assistedFactory: LotReportViewModelFactory,
            reportType: Int,
            lotId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(reportType, lotId) as T
            }
        }
    }

    private val _uiState = MutableStateFlow(LotReportUiState())
    val uiState: StateFlow<LotReportUiState> = _uiState.asStateFlow()

    init {
        if(reportType == Constants.LOT){
            readLotByLotId(lotId)
        }else if(reportType == Constants.ARCHIVE){
            readArchiveLotByLotId(lotId)
        }

        readDrugs()
    }

    private fun readLotByLotId(lotId: Int){
        lotUseCases.readLotsByLotId(lotId)
            .map { thisLotModel ->
                _uiState.update {
                    it.copy(lotModel = thisLotModel)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun readDrugs(){
        drugUseCases.readDrugsUC()
            .map { thisDrugList ->
                _uiState.update {
                    it.copy(drugList = thisDrugList)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun readArchiveLotByLotId(lotId: Int){

    }

}

data class LotReportUiState(
    val lotModel: LotModel? = null,
    val drugList: List<DrugModel> = emptyList()
)