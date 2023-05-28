package com.trevorwiebe.trackacow.presentation.add_edit_pen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditPensViewModel @Inject constructor(
    private val penUseCases: PenUseCases
) : ViewModel() {

    fun onEvent(event: AddEditPensEvent) {
        when (event) {
            is AddEditPensEvent.OnPenAdded -> {
                createPen(event.penModel)
            }

            is AddEditPensEvent.OnPenUpdated -> {
                updatePen(event.penModel)
            }

            is AddEditPensEvent.OnPenDeleted -> {
                deletePen(event.penModel)
            }
        }
    }

    private fun createPen(penModel: PenModel) {
        viewModelScope.launch {
            penUseCases.createPenUC(penModel)
        }
    }

    private fun updatePen(penModel: PenModel) {
        viewModelScope.launch {
            penUseCases.updatePenUC(penModel)
        }
    }

    private fun deletePen(penModel: PenModel) {
        viewModelScope.launch {
            penUseCases.deletePenUC(penModel)
        }
    }
}

sealed class AddEditPensEvent {
    data class OnPenAdded(val penModel: PenModel) : AddEditPensEvent()
    data class OnPenUpdated(val penModel: PenModel) : AddEditPensEvent()
    data class OnPenDeleted(val penModel: PenModel) : AddEditPensEvent()
}