package com.trevorwiebe.trackacow.presentation.lot_reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.use_cases.CalculateDrugsGiven
import com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases.CowUseCases
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.DrugsGivenUseCases
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.FeedUseCases
import com.trevorwiebe.trackacow.domain.use_cases.load_use_cases.LoadUseCases
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import com.trevorwiebe.trackacow.domain.utils.DataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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
    private val calculateDrugsGiven: CalculateDrugsGiven,
    @Assisted("lotCloudDatabaseId") private val lotCloudDatabaseId: String
): ViewModel() {

    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

    @AssistedFactory
    interface LotReportViewModelFactory {
        fun create(
            @Assisted("lotCloudDatabaseId") lotCloudDatabaseId: String
        ): LotReportViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun providesFactory(
            assistedFactory: LotReportViewModelFactory,
            lotCloudDatabaseId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(lotCloudDatabaseId) as T
            }
        }
    }

    private val _uiState = MutableStateFlow(LotReportUiState())
    val uiState: StateFlow<LotReportUiState> = _uiState.asStateFlow()

    init {
        readLotByLotId(lotCloudDatabaseId)
        readDrugsGivenAndDrugsByLotCloudDatabaseId(lotCloudDatabaseId)
        readLoadsByLotId(lotCloudDatabaseId)
        readDeadCowsByLotId(lotCloudDatabaseId)
        readFeedsByLotId(lotCloudDatabaseId)

    }

    fun onEvent(event: LotReportEvents) {
        when (event) {
            is LotReportEvents.OnArchiveLot -> {
                archivedLot(event.lotModel)
            }
        }
    }

    private fun readLotByLotId(lotCloudDatabaseId: String) {
        val lot = lotUseCases.readLotsByLotId(lotCloudDatabaseId)
        viewModelScope.launch {
            lot.dataFlow.collect { (thisLotModel, source) ->
                _uiState.update {
                    it.copy(
                        lotModel = thisLotModel as LotModel,
                        lotDataSource = source,
                        lotIsFetchingFromCloud = lot.isFetchingFromCloud
                    )
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readDrugsGivenAndDrugsByLotCloudDatabaseId(lotCloudDatabaseId: String) {
        val drug = drugsGivenUseCases.readDrugsGivenAndDrugsByLotId(lotCloudDatabaseId)
        viewModelScope.launch {
            drug.dataFlow.collect { (drugsGivenAndDrugList, source) ->
                _uiState.update {
                    it.copy(
                        drugsGivenAndDrugList = calculateDrugsGiven.invoke(
                            drugsGivenAndDrugList as List<DrugsGivenAndDrugModel>
                        ),
                        drugDataSource = source,
                        drugIsFetchingFromCloud = drug.isFetchingFromCloud
                    )
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readLoadsByLotId(lotId: String) {
        val load = loadUseCases.readLoadsByLotId(lotId)
        viewModelScope.launch {
            load.dataFlow.collect { (thisLoadList, source) ->
                _uiState.update {
                    it.copy(
                        loadList = thisLoadList as List<LoadModel>,
                        loadDataSource = source,
                        loadIsFetchingFromCloud = load.isFetchingFromCloud
                    )
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readDeadCowsByLotId(lotId: String) {
        val deadCows = cowUseCases.readDeadCowsByLotId(lotId)
        viewModelScope.launch {
            deadCows.dataFlow.collect { (thisDeadCowList, source) ->
                _uiState.update {
                    it.copy(
                        deadCowList = thisDeadCowList as List<CowModel>,
                        deadCowDataSource = source,
                        deadCowIsFetchingFromCloud = deadCows.isFetchingFromCloud
                    )
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readFeedsByLotId(lotId: String) {
        val feed = feedUseCases.readFeedsByLotId(lotId)
        viewModelScope.launch {
            feed.dataFlow.collect { (thisFeedList, source) ->
                val feedList = thisFeedList as List<FeedModel>
                val amount = numberFormat.format(feedList.sumOf { it.feed })
                _uiState.update {
                    it.copy(
                        feedAmount = amount,
                        feedDataSource = source,
                        feedIsFetchingFromCloud = feed.isFetchingFromCloud
                    )
                }
            }
        }
    }

    private fun archivedLot(lotModel: LotModel?) {
        if (lotModel == null) return
        viewModelScope.launch {
            lotUseCases.archiveLot(lotModel)
        }
    }
}

data class LotReportUiState(
    val lotModel: LotModel? = null,
    val lotIsFetchingFromCloud: Boolean = false,
    val lotDataSource: DataSource = DataSource.Local,

    val drugsGivenAndDrugList: List<DrugsGivenAndDrugModel> = emptyList(),
    val drugIsFetchingFromCloud: Boolean = false,
    val drugDataSource: DataSource = DataSource.Local,

    val loadList: List<LoadModel> = emptyList(),
    val loadIsFetchingFromCloud: Boolean = false,
    val loadDataSource: DataSource = DataSource.Local,

    val deadCowList: List<CowModel> = emptyList(),
    val deadCowIsFetchingFromCloud: Boolean = false,
    val deadCowDataSource: DataSource = DataSource.Local,

    val feedAmount: String = "",
    val feedIsFetchingFromCloud: Boolean = false,
    val feedDataSource: DataSource = DataSource.Local
)

sealed class LotReportEvents {
    data class OnArchiveLot(val lotModel: LotModel?) : LotReportEvents()
}