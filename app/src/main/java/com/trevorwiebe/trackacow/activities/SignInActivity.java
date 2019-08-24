package com.trevorwiebe.trackacow.activities;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.GoogleAuthProvider;
import com.trevorwiebe.trackacow.R;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 477;
    private static final int CREATE_ACCOUNT_CODE = 848;

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
                if (mEmail.length() == 0) {
                    mEmail.requestFocus();
                    mEmail.setError("Please enter your email");
                } else if (mPassword.length() == 0) {
                    mPassword.requestFocus();
                    mPassword.setError("Please enter your password");
                } else {

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

                                        String errorMessage = task.getException().getLocalizedMessage();

                                        showMessage("Error signing in", errorMessage);
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
                                                        Log.d(TAG, "Email sent.");
                                                        showMessage("Success!", "The link has been sent to " + emailAddress.getText().toString());
                                                    } else {
                                                        showMessage("Failure", task.getException().getLocalizedMessage());
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
                showMessage("Error signing in", errorMessage);
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
                            showMessage("Error signing in", errorMessage);
                        }
                    }
                });
    }

    private void showMessage(String title, String errorMessage) {
        AlertDialog.Builder signInError = new AlertDialog.Builder(SignInActivity.this);
        signInError.setTitle(title);
        signInError.setMessage(errorMessage);
        signInError.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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
