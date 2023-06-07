package com.trevorwiebe.trackacow.presentation.add_lot_and_load

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.use_cases.load_use_cases.LoadUseCases
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddLotAndLoadViewModel @Inject constructor(
    val lotUseCases: LotUseCases,
    val loadUseCases: LoadUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddLotAndLoadState())
    val uiState: StateFlow<AddLotAndLoadState> = _uiState.asStateFlow()

    fun onEvent(event: AddLotAndLoadEvents) {
        when (event) {
            is AddLotAndLoadEvents.OnPenFilled -> {
                onPenFilled(event.lotModel, event.loadModel)
            }
        }
    }

    private fun onPenFilled(lotModel: LotModel, loadModel: LoadModel) {
        viewModelScope.launch {
            val newLotId = lotUseCases.createLot(lotModel)
            loadModel.lotId = newLotId
            loadUseCases.createLoad(loadModel)

            lotModel.lotCloudDatabaseId = newLotId

            _uiState.update {
                it.copy(isPenFilled = true)
            }
        }
    }
}

sealed class AddLotAndLoadEvents {
    data class OnPenFilled(val lotModel: LotModel, val loadModel: LoadModel) : AddLotAndLoadEvents()
}

data class AddLotAndLoadState(
    val isPenFilled: Boolean = false
)