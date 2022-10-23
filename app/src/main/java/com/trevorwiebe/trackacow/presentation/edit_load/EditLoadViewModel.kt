package com.trevorwiebe.trackacow.presentation.edit_load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.use_cases.load_use_cases.LoadUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditLoadViewModel @Inject constructor(
    private val loadUseCases: LoadUseCases
): ViewModel() {

    fun onEvent(event: EditLoadUiEvent){
        when(event){
            is EditLoadUiEvent.OnLoadUpdated -> {
                Log.d("TAG", "onEvent: here")
                updateLoad(event.loadModel)
            }
            is EditLoadUiEvent.OnLoadDeleted -> {
                deleteLoad(event.loadModel)
            }
        }
    }

    private fun updateLoad(loadModel: LoadModel){
        viewModelScope.launch {
            loadUseCases.updateLoad(loadModel)
        }
    }

    private fun deleteLoad(loadModel: LoadModel){
        viewModelScope.launch {
            loadUseCases.deleteLoad(loadModel)
        }
    }

}

sealed class EditLoadUiEvent {
    data class OnLoadUpdated(val loadModel: LoadModel): EditLoadUiEvent()
    data class OnLoadDeleted(val loadModel: LoadModel): EditLoadUiEvent()
}