package com.trevorwiebe.trackacow;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class SignInChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_chooser);

        RelativeLayout layout = findViewById(R.id.sign_in_chooser_background);

        final int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackgroundDrawable(ContextCompat.getDrawable(this, R.mipmap.sign_in_back_ground));
        } else {
            layout.setBackground(ContextCompat.getDrawable(this, R.mipmap.sign_in_back_ground));
        }

    }
}
