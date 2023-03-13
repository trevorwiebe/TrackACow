package com.trevorwiebe.trackacow.presentation.sign_in

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import android.widget.EditText
import android.widget.ProgressBar
import android.os.Bundle
import com.trevorwiebe.trackacow.R
import android.view.WindowManager
import androidx.core.content.ContextCompat
import android.widget.TextView
import android.content.Intent
import com.google.firebase.auth.FirebaseAuthException
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import android.view.LayoutInflater
import com.google.android.material.textfield.TextInputEditText
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.trevorwiebe.trackacow.presentation.activities.CreateAccountActivity
import com.trevorwiebe.trackacow.presentation.activities.MainActivity

class SignInActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mSigningIn: ProgressBar
    private lateinit var mSigningInWithGoogle: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // finally change the color
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccent)

        mEmail = findViewById(R.id.sign_in_email)
        mPassword = findViewById(R.id.sign_in_password)
        mSigningIn = findViewById(R.id.signing_in)
        mSigningInWithGoogle = findViewById(R.id.signing_in_with_google)
        val signInBtn = findViewById<Button>(R.id.sign_in_btn)
        val forgotPassword = findViewById<TextView>(R.id.forgot_password)
        val signInWithGoogle = findViewById<Button>(R.id.sign_in_with_google)
        val createAccount = findViewById<Button>(R.id.create_account)

        signInBtn.setOnClickListener {

            var shouldSave = true

            if (mPassword.length() == 0) {
                mPassword.requestFocus()
                mPassword.error = "Please enter your password"
                shouldSave = false
            } else {
                mPassword.error = null
            }

            if (mEmail.length() == 0) {
                mEmail.requestFocus()
                mEmail.error = "Please enter your email"
                shouldSave = false
            } else {
                mEmail.error = null
            }

            if (shouldSave) {
                mSigningIn.visibility = View.VISIBLE
                val email = mEmail.text.toString()
                val password = mPassword.text.toString()
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this@SignInActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        } else {
                            mSigningIn.visibility = View.INVISIBLE
                            val errorCode = (task.exception as FirebaseAuthException).errorCode
                            val errorMessage = task.exception?.localizedMessage
                            val messageTitle = "Error signing in"
                            showMessage(messageTitle, errorMessage, errorCode)
                        }
                    }
            }
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        signInWithGoogle.setOnClickListener {
            mSigningInWithGoogle.visibility = View.VISIBLE
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
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
                                    showMessage(
                                        "Success!",
                                        "The link has been sent to " + emailAddress.text.toString(),
                                        NO_ERROR
                                    )
                                } else {
                                    val errorCode = (task.exception as FirebaseAuthException).errorCode
                                    val errorMessage = (task.exception as FirebaseAuthException).localizedMessage
                                    showMessage(
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
        // TODO: Need to fix this deprecation issue
        createAccount.setOnClickListener {
            val createAccountIntent = Intent(this@SignInActivity, CreateAccountActivity::class.java)
            startActivityForResult(createAccountIntent, CREATE_ACCOUNT_CODE)
        }
    }

    // TODO: Need to fix this deprecation issue
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                fireBaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                val errorMessage = e.localizedMessage
                val errorCode = (task.exception as FirebaseAuthException).errorCode
                showMessage("Error signing in", errorMessage, errorCode)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun fireBaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    val errorMessage = (task.exception as FirebaseAuthException).localizedMessage
                    val errorCode = (task.exception as FirebaseAuthException).errorCode
                    showMessage("Error signing in", errorMessage, errorCode)
                }
            }
    }

    private fun showMessage(title: String, errorMessage: String?, errorType: String?) {
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
        mSigningIn.visibility = View.INVISIBLE
        mSigningInWithGoogle.visibility = View.INVISIBLE
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    companion object {
        private const val TAG = "SignInActivity"
        private const val RC_SIGN_IN = 477
        private const val CREATE_ACCOUNT_CODE = 848
        const val NO_ERROR = "NO_ERROR"
        const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
        const val ERROR_USER_MISMATCH = "ERROR_USER_MISMATCH"
        const val ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL"
        const val ERROR_USER_DISABLED = "ERROR_USER_DISABLED"
        const val ERROR_INVALID_CUSTOM_TOKEN = "ERROR_INVALID_CUSTOM_TOKEN"
        const val ERROR_CUSTOM_TOKEN_MISMATCH = "ERROR_CUSTOM_TOKEN_MISMATCH"
        const val ERROR_INVALID_CREDENTIAL = "ERROR_INVALID_CREDENTIAL"
        const val ERROR_REQUIRES_RECENT_LOGIN = "ERROR_REQUIRES_RECENT_LOGIN"
        const val ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL = "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL"
        const val ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"
        const val ERROR_CREDENTIAL_ALREADY_IN_USE = "ERROR_CREDENTIAL_ALREADY_IN_USE"
        const val ERROR_USER_TOKEN_EXPIRED = "ERROR_USER_TOKEN_EXPIRED"
        const val ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
        const val ERROR_INVALID_USER_TOKEN = "ERROR_INVALID_USER_TOKEN"
        const val ERROR_OPERATION_NOT_ALLOWED = "ERROR_OPERATION_NOT_ALLOWED"
        const val ERROR_WEAK_PASSWORD = "ERROR_WEAK_PASSWORD"
        const val ERROR_MISSING_EMAIL = "ERROR_MISSING_EMAIL"
    }
}