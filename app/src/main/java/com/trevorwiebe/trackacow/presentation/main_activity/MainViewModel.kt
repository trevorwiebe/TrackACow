package com.trevorwiebe.trackacow.presentation.main_activity

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevorwiebe.trackacow.domain.use_cases.DatabaseVersionHelper
import com.trevorwiebe.trackacow.domain.use_cases.DeleteAllLocalData
import com.trevorwiebe.trackacow.domain.use_cases.InitiateCloudDatabaseMigration5to6
import com.trevorwiebe.trackacow.domain.use_cases.InitiateCloudDatabaseMigration6to7
import com.trevorwiebe.trackacow.domain.use_cases.UploadCache
import com.trevorwiebe.trackacow.domain.utils.Utility
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
    private val initiateCloudDatabaseMigration6to7: InitiateCloudDatabaseMigration6to7,
    private val deleteAllLocalData: DeleteAllLocalData,
    private val dbVersionHelper: DatabaseVersionHelper,
    private val uploadCache: UploadCache,
    private val context: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        setDbVersion()
    }

    fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.OnInitiateCloudDatabaseMigration5to6 -> {
                migrateDatabase5to6(dbVersionHelper.getDbVersion())
            }

            is MainUiEvent.OnInitiateCloudDatabaseMigration6to7 -> {
                migrateDatabase6to7(dbVersionHelper.getDbVersion())
            }

            is MainUiEvent.OnSignedOut -> {
                deleteAllData()
            }

            is MainUiEvent.CheckCache -> {
                syncCache()
            }
        }
    }

    private fun deleteAllData() {
        viewModelScope.launch {
            deleteAllLocalData.invoke()
        }
    }

    private fun migrateDatabase5to6(dbVersion: Long) {
        _uiState.update {
            it.copy(
                dbVersion = dbVersion,
                cloudDatabaseMigrationInProgress = true
            )
        }
        initiateCloudDatabaseMigration5to6.invoke(dbVersion)
            .continueWith { task ->
                // TODO: add error handling here
                _uiState.update { it.copy(cloudDatabaseMigrationInProgress = false) }
                Log.d("TAG", "migrateDatabase: ${task.result.data}")
            }
    }

    private fun migrateDatabase6to7(dbVersion: Long) {
        initiateCloudDatabaseMigration6to7.invoke(dbVersion)
            .continueWith { task ->
                Log.i("TAG", "migrateDatabase6to7: ${task.result.data}")
            }
    }

    private fun syncCache() {
        if (
            Utility.isThereNewDataToUpload(context) &&
            Utility.haveNetworkConnection(context)
        ) {
            viewModelScope.launch {
                uploadCache.invoke()
            }
        }
    }

    private fun setDbVersion() {
        _uiState.update { it.copy(dbVersion = dbVersionHelper.getDbVersion()) }
    }

}

data class MainUiState(
    val cloudDatabaseMigrationInProgress: Boolean = false,
    val dbVersion: Long = 0
)

sealed class MainUiEvent {
    object OnInitiateCloudDatabaseMigration5to6 : MainUiEvent()
    object OnInitiateCloudDatabaseMigration6to7 : MainUiEvent()
    object CheckCache : MainUiEvent()
    object OnSignedOut : MainUiEvent()
}