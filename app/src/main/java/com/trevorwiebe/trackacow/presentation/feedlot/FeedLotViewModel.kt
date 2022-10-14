package com.trevorwiebe.trackacow.presentation.feedlot

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.preferences.AppPreferences
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.CallUseCases
import com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases.RationUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class FeedLotViewModel @AssistedInject constructor(
    private val callUseCases: CallUseCases,
    private val rationUseCases: RationUseCases,
    private val appPreferences: AppPreferences,
    @Assisted defaultArgs: Bundle? = null
): ViewModel() {

    private var readCallByLotIdAndDateJob: Job? = null
    private var readRationsJob: Job? = null

    @AssistedFactory
    interface FeedLotViewModelFactory {
        fun create(defaultArgs: Bundle?): FeedLotViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun providesFactory(
            assistedFactory: FeedLotViewModelFactory,
            defaultArgs: Bundle?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(defaultArgs) as T
            }
        }
    }

    private val _uiState = MutableStateFlow(FeedLotUiState())
    val uiState: StateFlow<FeedLotUiState> = _uiState.asStateFlow()

    init {
        if(defaultArgs != null){

            val date = defaultArgs.getLong("feed_ui_model_date")
            val lotId = defaultArgs.getString("lot_id") ?: ""

            // convert lot date to beginning of the day
            val c = Calendar.getInstance()
            c.timeInMillis = date
            c[Calendar.HOUR_OF_DAY] = 0
            c[Calendar.MINUTE] = 0
            c[Calendar.SECOND] = 0
            c[Calendar.MILLISECOND] = 0
            val dateStarted = c.timeInMillis

            readCallByLotIdAndDate(lotId, dateStarted)
        }

        readRations()

        _uiState.update {
            it.copy(lastUsedRationId = getLastUsedRationId())
        }
    }

    fun onEvent(event: FeedLotEvents){
        when(event){
            is FeedLotEvents.OnSave -> {

                saveLastUsedRation(event.callModel.callRationId ?: -1)

                if(event.isUpdate){
                    updateCallModel(event.callModel)
                }else{
                    createCallModel(event.callModel)
                }
            }
        }
    }

    private fun getLastUsedRationId(): Int {
        return appPreferences.getLastUsedRation(AppPreferences.KEY_LAST_USED_RATION)
    }

    private fun saveLastUsedRation(rationId: Int){
        viewModelScope.launch {
            appPreferences.saveLastUsedRation(rationId)
        }
    }

    private fun createCallModel(callModel: CallModel){
        viewModelScope.launch {
            callUseCases.createCallUC(callModel)
        }
    }

    private fun updateCallModel(callModel: CallModel){
        viewModelScope.launch {
            callUseCases.updateCallUC(callModel)
        }
    }

    private fun readCallByLotIdAndDate(lotId: String, date: Long){
        readCallByLotIdAndDateJob?.cancel()
        readCallByLotIdAndDateJob = callUseCases.readCallsByLotIdAndDateUC(lotId, date)
            .map { receivedCallModel ->
                _uiState.update { uiState ->
                    uiState.copy(
                        callModel = receivedCallModel
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun readRations(){
        readRationsJob?.cancel()
        readRationsJob = rationUseCases.readAllRationsUC()
            .map { thisRationList ->
                _uiState.update { uiState ->
                    uiState.copy(
                        rationList = thisRationList
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}

sealed class FeedLotEvents{
    data class OnSave(val callModel: CallModel, val isUpdate: Boolean): FeedLotEvents()
}