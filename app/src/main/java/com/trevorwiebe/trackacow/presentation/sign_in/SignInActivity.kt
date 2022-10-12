package com.trevorwiebe.trackacow.presentation.activities;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.trevorwiebe.trackacow.R;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 477;
    private static final int CREATE_ACCOUNT_CODE = 848;

    public static final String NO_ERROR = "NO_ERROR";
    public static final String ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD";
    public static final String ERROR_USER_MISMATCH = "ERROR_USER_MISMATCH";
    public static final String ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL";
    public static final String ERROR_USER_DISABLED = "ERROR_USER_DISABLED";
    public static final String ERROR_INVALID_CUSTOM_TOKEN = "ERROR_INVALID_CUSTOM_TOKEN";
    public static final String ERROR_CUSTOM_TOKEN_MISMATCH = "ERROR_CUSTOM_TOKEN_MISMATCH";
    public static final String ERROR_INVALID_CREDENTIAL = "ERROR_INVALID_CREDENTIAL";
    public static final String ERROR_REQUIRES_RECENT_LOGIN = "ERROR_REQUIRES_RECENT_LOGIN";
    public static final String ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL = "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL";
    public static final String ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE";
    public static final String ERROR_CREDENTIAL_ALREADY_IN_USE = "ERROR_CREDENTIAL_ALREADY_IN_USE";
    public static final String ERROR_USER_TOKEN_EXPIRED = "ERROR_USER_TOKEN_EXPIRED";
    public static final String ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND";
    public static final String ERROR_INVALID_USER_TOKEN = "ERROR_INVALID_USER_TOKEN";
    public static final String ERROR_OPERATION_NOT_ALLOWED = "ERROR_OPERATION_NOT_ALLOWED";
    public static final String ERROR_WEAK_PASSWORD = "ERROR_WEAK_PASSWORD";
    public static final String ERROR_MISSING_EMAIL = "ERROR_MISSING_EMAIL";

    private EditText mEmail;
    private EditText mPassword;
    private ProgressBar mSigningIn;
    private ProgressBar mSigningInWithGoogle;
    private TextView mForgotPassword;
    private Button mSignInBtn;
    private Button mSignInWithGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));

        mEmail = findViewById(R.id.sign_in_email);
        mPassword = findViewById(R.id.sign_in_password);
        mSignInBtn = findViewById(R.id.sign_in_btn);
        mSigningIn = findViewById(R.id.signing_in);
        mSigningInWithGoogle = findViewById(R.id.signing_in_with_google);
        mForgotPassword = findViewById(R.id.forgot_password);
        mSignInWithGoogle = findViewById(R.id.sign_in_with_google);
        Button createAccount = findViewById(R.id.create_account);

        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean shouldSave = true;

                if (mPassword.length() == 0) {
                    mPassword.requestFocus();
                    mPassword.setError("Please enter your password");
                    shouldSave = false;
                }else{
                    mPassword.setError(null);
                }

                if (mEmail.length() == 0) {
                    mEmail.requestFocus();
                    mEmail.setError("Please enter your email");
                    shouldSave = false;
                }else{
                    mEmail.setError(null);
                }

                if(shouldSave){

                    mSigningIn.setVisibility(View.VISIBLE);

                    String email = mEmail.getText().toString();
                    String password = mPassword.getText().toString();

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    } else {
                                        mSigningIn.setVisibility(View.INVISIBLE);

                                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                        String errorMessage = Objects.requireNonNull(task.getException()).getLocalizedMessage();
                                        String messageTitle = "Error signing in";

                                        showMessage(messageTitle, errorMessage, errorCode);

                                    }
                                }
                            });
                }
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mSignInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSigningInWithGoogle.setVisibility(View.VISIBLE);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View editTextView = LayoutInflater.from(SignInActivity.this).inflate(R.layout.dialog_email_edit_text, null);
                final TextInputEditText emailAddress = editTextView.findViewById(R.id.dialog_edit_text_edit_text);

                final AlertDialog forgotPasswordDialog = new AlertDialog.Builder(SignInActivity.this)
                        .setTitle("Forgot your password")
                        .setCancelable(false)
                        .setMessage("Enter your email and we will send you a link to reset your password")
                        .setView(editTextView)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Send link", null)
                        .create();

                forgotPasswordDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        Button button = (forgotPasswordDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                if (emailAddress.length() != 0) {
                                    forgotPasswordDialog.dismiss();
                                    String emailAddressStr = emailAddress.getText().toString();
                                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddressStr)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        showMessage("Success!", "The link has been sent to " + emailAddress.getText().toString(), NO_ERROR);
                                                    } else {
                                                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                                        showMessage("Failure", task.getException().getLocalizedMessage(), errorCode);
                                                    }
                                                }
                                            });
                                } else {
                                    emailAddress.requestFocus();
                                    emailAddress.setError("Please fill the blank");
                                }
                            }
                        });
                    }
                });
                forgotPasswordDialog.show();
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createAccountIntent = new Intent(SignInActivity.this, CreateAccountActivity.class);
                startActivityForResult(createAccountIntent, CREATE_ACCOUNT_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                fireBaseAuthWithGoogle(account);
            } catch (ApiException e) {
                String errorMessage = e.getLocalizedMessage();
                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                showMessage("Error signing in", errorMessage, errorCode);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            String errorMessage = task.getException().getLocalizedMessage();
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            showMessage("Error signing in", errorMessage, errorCode);
                        }
                    }
                });
    }

    private void showMessage(String title, String errorMessage, String errorType) {
        AlertDialog.Builder signInError = new AlertDialog.Builder(SignInActivity.this);
        signInError.setTitle(title);
        signInError.setMessage(errorMessage);
        signInError.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(errorType == ERROR_INVALID_EMAIL
                    || errorType == ERROR_USER_DISABLED
                    || errorType == ERROR_USER_MISMATCH
                    || errorType == ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL
                    || errorType == ERROR_EMAIL_ALREADY_IN_USE
                    || errorType == ERROR_CREDENTIAL_ALREADY_IN_USE
                    || errorType == ERROR_USER_NOT_FOUND
                    || errorType == ERROR_MISSING_EMAIL
                ){
                    mEmail.requestFocus();
                    mEmail.selectAll();
                }else if(errorType == ERROR_INVALID_CREDENTIAL
                    || errorType == ERROR_WRONG_PASSWORD
                    || errorType == ERROR_WEAK_PASSWORD
                ){
                    mPassword.requestFocus();
                    mPassword.selectAll();
                }
            }
        });
        signInError.show();
        mSigningIn.setVisibility(View.INVISIBLE);
        mSigningInWithGoogle.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
