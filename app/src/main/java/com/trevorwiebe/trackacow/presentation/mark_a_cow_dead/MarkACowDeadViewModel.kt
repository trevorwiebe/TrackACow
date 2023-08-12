package com.trevorwiebe.trackacow.presentation.mark_a_cow_dead

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases.CowUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MarkACowDeadViewModel @AssistedInject constructor(
    private val cowUseCases: CowUseCases,
    @Assisted("lotId") private val lotId: String
) : ViewModel() {

    @AssistedFactory
    interface MarkACowDeadViewModelFactory {
        fun create(
            @Assisted("lotId") lotId: String
        ): MarkACowDeadViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun providesFactory(
            assistedFactory: MarkACowDeadViewModelFactory,
            lotId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(lotId) as T
            }
        }
    }

    private var deadCowJob: Job? = null

    private val _uiState = MutableStateFlow(MarkACowDeadUiState())
    val uiState: StateFlow<MarkACowDeadUiState> = _uiState.asStateFlow()

    init {
        readDeadCowsByLotId(lotId)
    }

    fun onEvent(event: MarkACowDeadEvent) {
        when (event) {
            is MarkACowDeadEvent.OnDeadCowCreated -> {
                createCow(event.cowModel)
            }
        }
    }

    private fun createCow(cowModel: CowModel) {
        viewModelScope.launch {
            cowUseCases.createCow(cowModel)
        }
    }

    // TODO: add progress bar
    @Suppress("UNCHECKED_CAST")
    private fun readDeadCowsByLotId(lotId: String) {
        deadCowJob?.cancel()
        deadCowJob = cowUseCases.readDeadCowsByLotId(lotId).dataFlow
            .map { (thisDeadCowList, source) ->
                _uiState.update {
                    it.copy(
                        deadCowList = thisDeadCowList as List<CowModel>
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}

data class MarkACowDeadUiState(
    val deadCowList: List<CowModel> = emptyList()
)

sealed class MarkACowDeadEvent {
    data class OnDeadCowCreated(val cowModel: CowModel) : MarkACowDeadEvent()
}