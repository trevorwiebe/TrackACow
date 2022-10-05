package com.trevorwiebe.trackacow.presentation.feedlot

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.CallUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class FeedLotViewModel @AssistedInject constructor(
    private val callUseCases: CallUseCases,
    @Assisted defaultArgs: Bundle? = null
): ViewModel() {

    private var readCallByLotIdAndDateJob: Job? = null

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
    }

    fun onEvent(event: FeedLotEvents){
        when(event){
            is FeedLotEvents.OnSave -> {
                if(event.isUpdate){
                    updateCallModel(event.callModel)
                }else{
                    createCallModel(event.callModel)
                }
            }
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
}

sealed class FeedLotEvents{
    data class OnSave(val callModel: CallModel, val isUpdate: Boolean): FeedLotEvents()
}