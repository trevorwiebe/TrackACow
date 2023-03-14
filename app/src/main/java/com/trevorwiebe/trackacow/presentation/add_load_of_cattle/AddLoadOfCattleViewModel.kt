package com.trevorwiebe.trackacow.presentation.add_load_of_cattle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.use_cases.load_use_cases.LoadUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddLoadOfCattleViewModel @Inject constructor(
    private val loadUseCases: LoadUseCases,
) : ViewModel() {

    fun onEvent(event: AddLoadOfCattleEvents) {
        when (event) {
            is AddLoadOfCattleEvents.OnLoadCreated -> {
                createLoad(event.loadModel)
            }
        }
    }

    private fun createLoad(loadModel: LoadModel) {
        viewModelScope.launch {
            loadUseCases.createLoad(loadModel)
        }
    }

}

sealed class AddLoadOfCattleEvents {
    data class OnLoadCreated(val loadModel: LoadModel) : AddLoadOfCattleEvents()
}