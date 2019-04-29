package com.trevorwiebe.trackacow.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.trevorwiebe.trackacow.R;

public class SubscribeActivity extends AppCompatActivity {

    private static final String TAG = "SubscribeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        Button monthlySub = findViewById(R.id.monthly_subscription);
        monthlySub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String monthly_url = getResources().getString(R.string.monthly_subscribe_url);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(monthly_url));
                startActivity(browserIntent);
            }
        });

        Button annualSub = findViewById(R.id.annual_subscription);
        annualSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String annually_url = getResources().getString(R.string.annually_subscribe_url);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(annually_url));
                startActivity(browserIntent);
            }
        });
    }

}
