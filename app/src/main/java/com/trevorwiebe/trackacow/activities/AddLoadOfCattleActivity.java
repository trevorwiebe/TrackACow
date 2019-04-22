package com.trevorwiebe.trackacow.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.trevorwiebe.trackacow.R;

public class AddLoadOfCattleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_load_of_cattle);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
