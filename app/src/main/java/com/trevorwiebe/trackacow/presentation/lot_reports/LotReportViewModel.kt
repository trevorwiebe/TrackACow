package com.trevorwiebe.trackacow.presentation.lot_reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.use_cases.CalculateDrugsGiven
import com.trevorwiebe.trackacow.domain.use_cases.archive_lot_use_cases.ArchiveLotUseCases
import com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases.CowUseCases
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.DrugsGivenUseCases
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.FeedUseCases
import com.trevorwiebe.trackacow.domain.use_cases.load_use_cases.LoadUseCases
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import com.trevorwiebe.trackacow.domain.utils.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class LotReportViewModel @AssistedInject constructor(
    private val lotUseCases: LotUseCases,
    private val drugsGivenUseCases: DrugsGivenUseCases,
    private val loadUseCases: LoadUseCases,
    private val cowUseCases: CowUseCases,
    private val feedUseCases: FeedUseCases,
    private val archiveLotUseCases: ArchiveLotUseCases,
    private val calculateDrugsGiven: CalculateDrugsGiven,
    @Assisted("reportType") private val reportType: Int,
    @Assisted("lotId") private val lotId: Int,
    @Assisted("lotCloudDatabaseId") private val lotCloudDatabaseId: String
): ViewModel() {

    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

    @AssistedFactory
    interface LotReportViewModelFactory {
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
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(reportType, lotId, lotCloudDatabaseId) as T
            }
        }
    }

    private var lotJob: Job? = null
    private var drugsGivenJob: Job? = null
    private var loadJob: Job? = null
    private var cowJob: Job? = null
    private var feedJob: Job? = null

    private val _uiState = MutableStateFlow(LotReportUiState())
    val uiState: StateFlow<LotReportUiState> = _uiState.asStateFlow()

    init {
        if (reportType == Constants.LOT) {
            readLotByLotId(lotId)
        }

        readDrugsGivenAndDrugsByLotCloudDatabaseId(lotCloudDatabaseId)
        readLoadsByLotId(lotCloudDatabaseId)
        readDeadCowsByLotId(lotCloudDatabaseId)
        readFeedsByLotId(lotCloudDatabaseId)

    }

    fun onEvent(event: LotReportEvents) {
        when (event) {
            is LotReportEvents.OnArchiveLot -> {
                addArchivedLot(event.lotModel)
                deleteLot(event.lotModel)
            }
        }
    }

    private fun readLotByLotId(lotId: Int) {
        lotJob?.cancel()
        lotJob = lotUseCases.readLotsByLotId(lotId)
            .map { thisLotModel ->
                _uiState.update {
                    it.copy(lotModel = thisLotModel)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun readDrugsGivenAndDrugsByLotCloudDatabaseId(lotCloudDatabaseId: String){
        drugsGivenJob?.cancel()
        drugsGivenJob = drugsGivenUseCases
            .readDrugsGivenAndDrugsByLotId(lotCloudDatabaseId)
            .map { drugsGivenAndDrugList ->
                _uiState.update {
                    it.copy(drugsGivenAndDrugList = calculateDrugsGiven.invoke(drugsGivenAndDrugList))
                }
            }
            .launchIn(viewModelScope)
    }

    private fun readLoadsByLotId(lotId: String){
        loadJob?.cancel()
        loadJob = loadUseCases.readLoadsByLotId(lotId)
            .map { thisLoadList ->
                _uiState.update {
                    it.copy(loadList = thisLoadList)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun readDeadCowsByLotId(lotId: String) {
        cowJob?.cancel()
        cowJob = cowUseCases.readDeadCowsByLotId(lotId)
            .map { thisDeadCowList ->
                _uiState.update {
                    it.copy(deadCowList = thisDeadCowList)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun readFeedsByLotId(lotId: String) {
        feedJob?.cancel()
        feedJob = feedUseCases.readFeedsByLotId(lotId)
            .map { thisFeedList ->
                val amount = numberFormat.format(thisFeedList.sumOf { it.feed })
                _uiState.update {
                    it.copy(feedAmount = amount)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun addArchivedLot(lotModel: LotModel?) {
        if (lotModel == null) return
        viewModelScope.launch {
            archiveLotUseCases.createArchiveLot(lotModel)
        }
    }

    private fun deleteLot(lotModel: LotModel?) {
        if (lotModel == null) return
        viewModelScope.launch {
            lotUseCases.deleteLot(lotModel)
        }
    }

}

data class LotReportUiState(
    val lotModel: LotModel? = null,
    val drugsGivenAndDrugList: List<DrugsGivenAndDrugModel> = emptyList(),
    val loadList: List<LoadModel> = emptyList(),
    val deadCowList: List<CowModel> = emptyList(),
    val feedAmount: String = ""
)

sealed class LotReportEvents {
    data class OnArchiveLot(val lotModel: LotModel?) : LotReportEvents()
}