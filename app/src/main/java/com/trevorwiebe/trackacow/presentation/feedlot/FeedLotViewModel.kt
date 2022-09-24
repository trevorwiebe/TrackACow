package com.trevorwiebe.trackacow.presentation.feedlot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.CallUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedLotViewModel @Inject constructor(
    private val callUseCases: CallUseCases
): ViewModel() {

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
}

sealed class FeedLotEvents{
    data class OnSave(val callModel: CallModel, val isUpdate: Boolean): FeedLotEvents()
}