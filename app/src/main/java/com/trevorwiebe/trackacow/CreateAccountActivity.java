package com.trevorwiebe.trackacow;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private TextInputEditText mName;
    private TextInputEditText mEmail;
    private TextInputEditText mPassword;
    private ProgressBar mCreatingAccount;
    private Button mCreateAccountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        mName = findViewById(R.id.create_account_name);
        mEmail = findViewById(R.id.create_account_email);
        mPassword = findViewById(R.id.create_account_password);
        mCreatingAccount = findViewById(R.id.creating_account);
        mCreateAccountBtn = findViewById(R.id.create_account_btn);

        mCreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mName.length() == 0){
                    mName.requestFocus();
                    mName.setError("Please fill in name");
                }else if(mEmail.length() == 0){
                    mEmail.requestFocus();
                    mEmail.setError("Please fill in email");
                }else if(mPassword.length() == 0){
                    mPassword.requestFocus();
                    mPassword.setError("Please fill in password");
                }else{

                    mCreateAccountBtn.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
                    mCreatingAccount.setVisibility(View.VISIBLE);
                    final String name = mName.getText().toString();
                    String email = mEmail.getText().toString();
                    String password = mPassword.getText().toString();

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){

                                        FirebaseUser user = task.getResult().getUser();

                                        if(user != null) {
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(name)
                                                    .build();

                                            user.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Intent data = new Intent();
                                                                setResult(RESULT_OK, data);
                                                                finish();
                                                            }else{
                                                                String errorMessage = task.getException().getLocalizedMessage();
                                                                showMessage("Could not set name to account", errorMessage);
                                                            }
                                                        }
                                                    });
                                        }else{
                                            showMessage("User was null", "");
                                        }
                                    }else{
                                        String errorMessage = task.getException().getLocalizedMessage();
                                        showMessage("There was an error", errorMessage);
                                    }
                                }
                            });

                }
            }
        });


    }

    private void showMessage(String title, String errorMessage){
        AlertDialog.Builder signInError = new AlertDialog.Builder(CreateAccountActivity.this);
        signInError.setTitle(title);
        signInError.setMessage(errorMessage);
        signInError.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        signInError.show();

        mCreateAccountBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        mCreatingAccount.setVisibility(View.INVISIBLE);
    }
}
