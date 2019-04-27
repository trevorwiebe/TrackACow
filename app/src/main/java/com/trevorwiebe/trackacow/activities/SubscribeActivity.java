package com.trevorwiebe.trackacow.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.subscription.BillingManager;
import com.trevorwiebe.trackacow.subscription.BillingProvider;

public class SubscribeActivity extends AppCompatActivity implements BillingProvider {

    private static final String TAG = "SubscribeActivity";

    private BillingManager mBillingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        mBillingManager = new BillingManager(this);
    }

    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }
}
