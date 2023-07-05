package com.trevorwiebe.trackacow.presentation.edit_lot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditLotViewModel @Inject constructor(
    private val lotUseCases: LotUseCases
): ViewModel() {

    fun onEvent(event: EditLotUiEvent){
        when(event){
            is EditLotUiEvent.OnLotUpdated -> {
                updateLotByLotId(event.lotModel)
            }
        }
    }

    private fun updateLotByLotId(lotModel: LotModel){
        viewModelScope.launch {
            lotUseCases.updateLot(lotModel)
        }
    }
}

sealed class EditLotUiEvent{
    data class OnLotUpdated(val lotModel: LotModel): EditLotUiEvent()
}