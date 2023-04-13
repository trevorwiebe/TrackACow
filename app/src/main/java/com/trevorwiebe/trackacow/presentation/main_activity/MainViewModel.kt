package com.trevorwiebe.trackacow.presentation.main_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.use_cases.DeleteAllLocalData
import com.trevorwiebe.trackacow.domain.use_cases.InitiateCloudDatabaseMigration5to6
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
        private val initiateCloudDatabaseMigration5to6: InitiateCloudDatabaseMigration5to6,
        private val deleteAllLocalData: DeleteAllLocalData
) : ViewModel() {

    init {

    }

    fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.OnInitiateCloudDatabaseMigration -> {
                initiateCloudDatabaseMigration5to6.invoke(event.appVersion)
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

}

sealed class MainUiEvent {
    data class OnInitiateCloudDatabaseMigration(val appVersion: Long) : MainUiEvent()
    object OnSignedOut : MainUiEvent()
}