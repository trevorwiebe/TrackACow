package com.trevorwiebe.trackacow.presentation.fragment_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.trevorwiebe.trackacow.domain.use_cases.DeleteAllLocalData
import com.trevorwiebe.trackacow.domain.use_cases.UploadCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsFragmentViewModel @Inject constructor(
    private val uploadCache: UploadCache,
    private val firebaseAuth: FirebaseAuth,
    private val deleteAllLocalData: DeleteAllLocalData
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsFragmentUiState())
    val uiState: StateFlow<SettingsFragmentUiState> = _uiState.asStateFlow()

    init {
        getCurrentUser()
    }

    fun onEvent(event: SettingsFragmentEvent) {
        when (event) {
            is SettingsFragmentEvent.OnUploadCache -> {
                startCacheUpload()
            }

            is SettingsFragmentEvent.OnSignOut -> {
                signOut()
            }
        }
    }

    private fun getCurrentUser() {
        _uiState.update {
            it.copy(firebaseUser = firebaseAuth.currentUser)
        }
    }

    private fun startCacheUpload() {
        viewModelScope.launch {
            uploadCache.invoke()
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            deleteAllLocalData.invoke()
        }
        firebaseAuth.signOut()
    }

}

data class SettingsFragmentUiState(
    val firebaseUser: FirebaseUser? = null
)

sealed class SettingsFragmentEvent {
    object OnUploadCache : SettingsFragmentEvent()
    object OnSignOut : SettingsFragmentEvent()
}