package com.trevorwiebe.trackacow.presentation.main_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.use_cases.DeleteAllLocalData
import com.trevorwiebe.trackacow.domain.use_cases.InitiateCloudDatabaseMigration5to6
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
        private val initiateCloudDatabaseMigration5to6: InitiateCloudDatabaseMigration5to6,
        private val deleteAllLocalData: DeleteAllLocalData
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {

    }

    fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.OnInitiateCloudDatabaseMigration -> {
                migrateDatabase(event.appVersion)
            }
            is MainUiEvent.OnSignedOut -> {
                deleteAllData()
            }
        }
    }

    private fun deleteAllData() {
        viewModelScope.launch {
            deleteAllLocalData.invoke()
        }
    }

    private fun migrateDatabase(appVersion: Long) {
        _uiState.update { it.copy(cloudDatabaseMigrationInProgress = true) }
        initiateCloudDatabaseMigration5to6.invoke(appVersion)
            .continueWith { task ->
                _uiState.update { it.copy(cloudDatabaseMigrationInProgress = false) }
            }
    }

}

data class MainUiState(
    val cloudDatabaseMigrationInProgress: Boolean = false
)

sealed class MainUiEvent {
    data class OnInitiateCloudDatabaseMigration(val appVersion: Long) : MainUiEvent()
    object OnSignedOut : MainUiEvent()
}