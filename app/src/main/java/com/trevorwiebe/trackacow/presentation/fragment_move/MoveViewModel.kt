package com.trevorwiebe.trackacow.presentation.fragment_move

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MoveViewModel @Inject constructor(
    private val penUseCases: PenUseCases
): ViewModel() {

    private val _uiState = MutableStateFlow(MoveUiState())
    val uiState: StateFlow<MoveUiState> = _uiState.asStateFlow()

    init {
        readPensAndLots()
    }

    private fun readPensAndLots(){
        penUseCases.readPenAndLotModelUC()
            .map { thisPenAndLotList ->
                _uiState.update { uiState ->
                    uiState.copy(
                        penAndLotList = thisPenAndLotList
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}