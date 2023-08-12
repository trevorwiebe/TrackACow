package com.trevorwiebe.trackacow.presentation.create_account

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.presentation.main_activity.MainActivity

class CreateAccountActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()
    private var mGoogleSignInClient: GoogleSignInClient? = null

    private lateinit var mName: EditText
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mCreatingAccount: ProgressBar
    private lateinit var mCreatingAccountWithGoogle: ProgressBar
    private lateinit var mCreateAccountBtn: Button
    private lateinit var mCreateAccountWithGoogleBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // finally change the color
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccentDark)
        mName = findViewById(R.id.create_account_name)
        mEmail = findViewById(R.id.create_account_email)
        mPassword = findViewById(R.id.create_account_password)
        mCreatingAccount = findViewById(R.id.creating_account)
        mCreatingAccountWithGoogle = findViewById(R.id.creating_account_with_google)
        mCreateAccountBtn = findViewById(R.id.create_account_btn)
        mCreateAccountWithGoogleBtn = findViewById(R.id.create_account_with_google)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mCreateAccountBtn.setOnClickListener {
            if (mName.length() == 0) {
                mName.requestFocus()
                mName.error = "Please fill in name"
            } else if (mEmail.length() == 0) {
                mEmail.requestFocus()
                mEmail.error = "Please fill in email"
            } else if (mPassword.length() == 0) {
                mPassword.requestFocus()
                mPassword.error = "Please fill in password"
            } else {
                mCreatingAccount.visibility = View.VISIBLE
                val name = mName.text.toString()
                val email = mEmail.text.toString()
                val password = mPassword.text.toString()
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result.user
                            if (user != null) {
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build()
                                user.updateProfile(profileUpdates)
                                    .addOnCompleteListener { updateTask ->
                                        if (updateTask.isSuccessful) {
                                            val intent = Intent(
                                                this@CreateAccountActivity,
                                                MainActivity::class.java
                                            )
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(intent)
                                        } else {
                                            val errorMessage =
                                                updateTask.exception?.localizedMessage
                                            showMessage(
                                                "Could not set name to account",
                                                errorMessage
                                            )
                                        }
                                    }
                            } else {
                                showMessage("User was null", "")
                            }
                        } else {
                            val errorMessage = task.exception?.localizedMessage
                            showMessage("There was an error", errorMessage)
                        }
                    }
            }
        }
        mCreateAccountWithGoogleBtn.setOnClickListener { openGoogleSignInForResult() }
    }

    var signInActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                fireBaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                val errorMessage = e.localizedMessage
                showMessage("Error creating account", errorMessage)
            }
        }
    }

    private fun openGoogleSignInForResult() {
        mCreatingAccountWithGoogle.visibility = View.VISIBLE
        val signInIntent = mGoogleSignInClient!!.signInIntent
        signInActivity.launch(signInIntent)
    }

    private fun fireBaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(this@CreateAccountActivity, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    val errorMessage = task.exception?.localizedMessage
                    showMessage("Error creating account", errorMessage)
                }
            }
    }

    private fun showMessage(title: String, errorMessage: String?) {
        val signInError = AlertDialog.Builder(this@CreateAccountActivity)
        signInError.setTitle(title)
        signInError.setMessage(errorMessage)
        signInError.setPositiveButton("Ok") { _, _ -> }
        signInError.show()
        mCreatingAccountWithGoogle.visibility = View.INVISIBLE
        mCreatingAccount.visibility = View.INVISIBLE
    }
}