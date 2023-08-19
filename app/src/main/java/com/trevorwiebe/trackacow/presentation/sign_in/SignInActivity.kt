package com.trevorwiebe.trackacow.presentation.sign_in

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.EditText
import android.widget.ProgressBar
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import android.view.WindowManager
import androidx.core.content.ContextCompat
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuthException
import android.view.LayoutInflater
import com.google.android.material.textfield.TextInputEditText
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(this, Identity.getSignInClient(this))
    }
    private lateinit var mName: EditText
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mSignInWithEmailBtn: Button
    private lateinit var mSignInWithEmailProgressBar: ProgressBar
    private lateinit var mSignInWithGoogleBtn: Button
    private lateinit var mSignInWithGoogleProgressBar: ProgressBar

    private val signInViewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // finally change the color
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

        mName = findViewById(R.id.sign_in_name)
        mEmail = findViewById(R.id.sign_in_email)
        mPassword = findViewById(R.id.sign_in_password)
        mSignInWithEmailProgressBar = findViewById(R.id.signing_in)
        mSignInWithGoogleProgressBar = findViewById(R.id.signing_in_with_google)
        mSignInWithEmailBtn = findViewById(R.id.sign_in_btn)
        val forgotPassword = findViewById<TextView>(R.id.forgot_password)
        mSignInWithGoogleBtn = findViewById(R.id.sign_in_with_google)

        mSignInWithEmailBtn.setOnClickListener {

            mSignInWithEmailProgressBar.visibility = View.VISIBLE

            signInViewModel.onEvent(
                SignInEvent.OnSignInWithEmail(
                    mName.text.toString(),
                    mEmail.text.toString(),
                    mPassword.text.toString()
                )
            )
        }

        val googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            result.checkResultAndExecute {
                lifecycleScope.launch {
                    val credential = googleAuthUiClient.getCredentialFromSignInIntent(
                        data ?: return@launch
                    )
                    signInViewModel.onEvent(SignInEvent.OnSignInWithGoogle(credential))
                }
            }
        }

        mSignInWithGoogleBtn.setOnClickListener {
            mSignInWithGoogleProgressBar.visibility = View.VISIBLE
            lifecycleScope.launch {
                val intentSender = googleAuthUiClient.getSignInIntent()
                googleSignInLauncher.launch(
                    IntentSenderRequest.Builder(
                        intentSender ?: return@launch
                    ).build()
                )
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                signInViewModel.state.collect {
                    if (it.isSignInSuccessful) {
                        finish()
                    } else {
                        if (it.signInErrorCode != NO_ERROR) {
                            showErrorMessage("Sign in error", it.signInError, it.signInErrorCode)
                        }
                    }
                }
            }
        }

        forgotPassword.setOnClickListener {
            val editTextView = LayoutInflater.from(this@SignInActivity)
                .inflate(R.layout.dialog_email_edit_text, null)
            val emailAddress =
                editTextView.findViewById<TextInputEditText>(R.id.dialog_edit_text_edit_text)
            val forgotPasswordDialog = AlertDialog.Builder(this@SignInActivity)
                .setTitle("Forgot your password")
                .setCancelable(false)
                .setMessage("Enter your email and we will send you a link to reset your password")
                .setView(editTextView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Send link", null)
                .create()
            forgotPasswordDialog.setOnShowListener {
                val button = forgotPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                button.setOnClickListener {
                    if (emailAddress.length() != 0) {
                        forgotPasswordDialog.dismiss()
                        val emailAddressStr = emailAddress.text.toString()
                        FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddressStr)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    showErrorMessage(
                                        "Success!",
                                        "The link has been sent to " + emailAddress.text.toString(),
                                        NO_ERROR
                                    )
                                } else {
                                    val errorCode = (task.exception as FirebaseAuthException).errorCode
                                    val errorMessage = (task.exception as FirebaseAuthException).localizedMessage
                                    showErrorMessage(
                                        "Sending failed",
                                        errorMessage,
                                        errorCode
                                    )
                                }
                            }
                    } else {
                        emailAddress.requestFocus()
                        emailAddress.error = "Please fill the blank"
                    }
                }
            }
            forgotPasswordDialog.show()
        }
    }

    private fun showErrorMessage(title: String, errorMessage: String?, errorType: String?) {
        if (errorType === ERROR_NO_EMAIL
            || errorType === ERROR_NO_PASSWORD
            || errorType === ERROR_NO_NAME
        ) {
            if (errorType === ERROR_NO_EMAIL) {
                mEmail.requestFocus()
                mEmail.error = "Email required"
            }
            if (errorType === ERROR_NO_PASSWORD) {
                mPassword.requestFocus()
                mPassword.error = "Password required"
            }
            if (errorType === ERROR_NO_NAME) {
                mName.requestFocus()
                mName.error = "Name required"
            }
        } else {
            val signInError = AlertDialog.Builder(this@SignInActivity)
            signInError.setTitle(title)
            signInError.setMessage(errorMessage)
            signInError.setPositiveButton("Ok") { dialog, which ->
                if (errorType === ERROR_INVALID_EMAIL
                    || errorType === ERROR_USER_DISABLED
                    || errorType === ERROR_USER_MISMATCH
                    || errorType === ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL
                    || errorType === ERROR_EMAIL_ALREADY_IN_USE
                    || errorType === ERROR_CREDENTIAL_ALREADY_IN_USE
                    || errorType === ERROR_USER_NOT_FOUND
                    || errorType === ERROR_MISSING_EMAIL
                ) {
                    mEmail.requestFocus()
                    mEmail.selectAll()
                } else if (errorType === ERROR_INVALID_CREDENTIAL
                    || errorType === ERROR_WRONG_PASSWORD
                    || errorType === ERROR_WEAK_PASSWORD
                ) {
                    mPassword.requestFocus()
                    mPassword.selectAll()
                }
            }
            signInError.show()
        }
        mSignInWithEmailProgressBar.visibility = View.INVISIBLE
        mSignInWithGoogleProgressBar.visibility = View.INVISIBLE
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finishAffinity()"))
    override fun onBackPressed() {
        finishAffinity()
    }

    companion object {
        const val NO_ERROR = "NO_ERROR"
        const val ERROR_NO_NAME = "ERROR_NO_NAME"
        const val ERROR_NO_EMAIL = "ERROR_NO_EMAIL"
        const val ERROR_NO_PASSWORD = "ERROR_NO_PASSWORD"
        const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
        const val ERROR_USER_MISMATCH = "ERROR_USER_MISMATCH"
        const val ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL"
        const val ERROR_USER_DISABLED = "ERROR_USER_DISABLED"
        const val ERROR_INVALID_CREDENTIAL = "ERROR_INVALID_CREDENTIAL"
        const val ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL =
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL"
        const val ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"
        const val ERROR_CREDENTIAL_ALREADY_IN_USE = "ERROR_CREDENTIAL_ALREADY_IN_USE"
        const val ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
        const val ERROR_WEAK_PASSWORD = "ERROR_WEAK_PASSWORD"
        const val ERROR_MISSING_EMAIL = "ERROR_MISSING_EMAIL"
        const val ERROR_UNKNOWN_ERROR = "ERROR_UNKNOWN_ERROR"
    }

    private inline fun ActivityResult.checkResultAndExecute(block: ActivityResult.() -> Unit) =
        if (resultCode == Activity.RESULT_OK) runCatching(block)
        else Result.failure(Exception("Something went wrong"))
}