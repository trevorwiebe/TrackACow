package com.trevorwiebe.trackacow.presentation.feedlot

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.CallUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
            val date: Long = defaultArgs.getLong("date")
            val lotId: String = defaultArgs.getString("lotId") ?: ""
            readCallByLotIdAndDate(lotId, date)
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