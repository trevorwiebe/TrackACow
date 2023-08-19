package com.trevorwiebe.trackacow.presentation.sign_in

import android.app.Application
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val context: Application
) : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onEvent(signInEvent: SignInEvent) {
        when (signInEvent) {
            is SignInEvent.OnSignInWithEmail -> {
                signInWithEmailAndPassword(
                    signInEvent.name,
                    signInEvent.email,
                    signInEvent.password,
                )
            }

            is SignInEvent.OnSignInWithGoogle -> {
                signInWithGoogle(signInEvent.credential)
            }
        }
    }

    private fun signInWithEmailAndPassword(name: String, email: String, password: String) {
        var shouldSignIn = true

        if (name.isEmpty()) {
            _state.update {
                it.copy(
                    signInError = "Please enter your name.",
                    signInErrorCode = SignInActivity.ERROR_NO_NAME
                )
            }
            shouldSignIn = false
        } else {
            _state.update {
                it.copy(
                    signInError = null,
                    signInErrorCode = SignInActivity.NO_ERROR
                )
            }
        }

        if (password.isEmpty()) {
            _state.update {
                it.copy(
                    signInError = "Please enter your password.",
                    signInErrorCode = SignInActivity.ERROR_NO_PASSWORD
                )
            }
            shouldSignIn = false
        } else {
            _state.update {
                it.copy(
                    signInError = null,
                    signInErrorCode = SignInActivity.NO_ERROR
                )
            }
        }

        if (email.isEmpty()) {
            _state.update {
                it.copy(
                    signInError = "Please enter your email.",
                    signInErrorCode = SignInActivity.ERROR_NO_EMAIL
                )
            }
            shouldSignIn = false
        } else {
            _state.update {
                it.copy(
                    signInError = null,
                    signInErrorCode = SignInActivity.NO_ERROR
                )
            }
        }

        if (shouldSignIn) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _state.update { it.copy(isSignInSuccessful = true) }
                    } else {
                        val errorCode = try {
                            (task.exception as FirebaseAuthException).errorCode
                        } catch (e: Exception) {
                            SignInActivity.ERROR_UNKNOWN_ERROR
                        }

                        // if error code is user not found, try to create account with these credentials
                        if (errorCode == SignInActivity.ERROR_USER_NOT_FOUND) {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { createAccountTask ->
                                    if (createAccountTask.isSuccessful) {
                                        val signedInUser = createAccountTask.result.user
                                        if (signedInUser != null && signedInUser.displayName.isNullOrEmpty()) {
                                            val profileUpdates = UserProfileChangeRequest.Builder()
                                                .setDisplayName(name)
                                                .build()

                                            signedInUser.updateProfile(profileUpdates)
                                                .addOnCompleteListener {
                                                    _state.update { it.copy(isSignInSuccessful = true) }
                                                }
                                        } else {
                                            _state.update { it.copy(isSignInSuccessful = true) }
                                        }
                                    } else {

                                        // show create account error
                                        val createAccountErrorCode = try {
                                            (createAccountTask.exception as FirebaseAuthException).errorCode
                                        } catch (e: Exception) {
                                            SignInActivity.ERROR_UNKNOWN_ERROR
                                        }
                                        val createAccountErrorMessage =
                                            createAccountTask.exception?.localizedMessage
                                        _state.update {
                                            it.copy(
                                                signInError = createAccountErrorMessage,
                                                signInErrorCode = createAccountErrorCode
                                            )
                                        }
                                    }
                                }
                        } else {

                            // show sign in error
                            val errorMessage = task.exception?.localizedMessage
                            _state.update {
                                it.copy(
                                    signInError = errorMessage,
                                    signInErrorCode = errorCode
                                )
                            }

                        }
                    }
                }
        }
    }

    private fun signInWithGoogle(authCredential: AuthCredential) {
        auth.signInWithCredential(authCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.update { it.copy(isSignInSuccessful = true) }
                } else {
                    val errorCode = try {
                        (task.exception as FirebaseAuthException).errorCode
                    } catch (e: Exception) {
                        SignInActivity.ERROR_UNKNOWN_ERROR
                    }
                    val errorMessage = task.exception?.localizedMessage
                    _state.update {
                        it.copy(
                            signInError = errorMessage,
                            signInErrorCode = errorCode
                        )
                    }
                }
            }
    }

}

sealed class SignInEvent {
    data class OnSignInWithEmail(val name: String, val email: String, val password: String) :
        SignInEvent()

    data class OnSignInWithGoogle(val credential: AuthCredential) : SignInEvent()
}

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val signInErrorCode: String = SignInActivity.NO_ERROR
)