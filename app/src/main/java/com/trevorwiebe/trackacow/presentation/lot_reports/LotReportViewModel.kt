package com.trevorwiebe.trackacow.presentation.lot_reports

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.DrugsGivenUseCases
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import com.trevorwiebe.trackacow.domain.utils.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*

class LotReportViewModel @AssistedInject constructor(
    private val lotUseCases: LotUseCases,
    private val drugsGivenUseCases: DrugsGivenUseCases,
    @Assisted("reportType") private val reportType: Int,
    @Assisted("lotId") private val lotId: Int,
    @Assisted("lotCloudDatabaseId") private val lotCloudDatabaseId: String
): ViewModel() {

    @AssistedFactory
    interface LotReportViewModelFactory{
        fun create(
            @Assisted("reportType") reportType: Int,
            @Assisted("lotId") lotId: Int,
            @Assisted("lotCloudDatabaseId") lotCloudDatabaseId: String
        ): LotReportViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun providesFactory(
            assistedFactory: LotReportViewModelFactory,
            reportType: Int,
            lotId: Int,
            lotCloudDatabaseId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(reportType, lotId, lotCloudDatabaseId) as T
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

        readDrugsGivenAndDrugsByLotCloudDatabaseId(lotCloudDatabaseId)

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


    private fun readArchiveLotByLotId(lotId: Int){

    }

    private fun readDrugsGivenAndDrugsByLotId(lotId: Int){

    }

    private fun readDrugsGivenAndDrugsByLotCloudDatabaseId(lotCloudDatabaseId: String){
        Log.d("TAG", "readDrugsGivenAndDrugsByLotCloudDatabaseId: $lotCloudDatabaseId")
        drugsGivenUseCases.readDrugsGivenAndDrugsByLotId(lotCloudDatabaseId)
            .map { drugsGivenAndDrugList ->
//                Log.d("TAG", "readDrugsGivenAndDrugsByLotCloudDatabaseId: $drugsGivenAndDrugList")
                _uiState.update {
                    it.copy(drugsGivenAndDrugList = drugsGivenAndDrugList)
                }
            }
            .launchIn(viewModelScope)
    }

}

data class LotReportUiState(
    val lotModel: LotModel? = null,
    val drugsGivenAndDrugList: List<DrugsGivenAndDrugModel> = emptyList()
)