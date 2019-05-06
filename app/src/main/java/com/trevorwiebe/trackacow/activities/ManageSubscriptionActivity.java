package com.trevorwiebe.trackacow.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.QueryUserByUid;
import com.trevorwiebe.trackacow.db.entities.UserEntity;
import com.trevorwiebe.trackacow.utils.Utility;

public class ManageSubscriptionActivity extends AppCompatActivity implements
        QueryUserByUid.OnUserByUidLoaded {

    private TextView mAccountType;
    private TextView mRenewalDate;
    private Button mEditSubscription;
    private Button mCancelSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_subscription);

        mAccountType = findViewById(R.id.account_type);
        mRenewalDate = findViewById(R.id.renewal_date);
        mEditSubscription = findViewById(R.id.edit_subscription);
        mCancelSubscription = findViewById(R.id.cancel_subscription);

        mEditSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent subscribeActivity = new Intent(ManageSubscriptionActivity.this, SubscribeActivity.class);
                startActivity(subscribeActivity);
            }
        });

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        new QueryUserByUid(uid, this).execute(this);

    }

    @Override
    public void onUserByUidLoaded(UserEntity userEntity) {

        int accountType = userEntity.getAccountType();
        String accountTypeStr = "";
        switch (accountType) {
            case UserEntity.FREE_TRIAL:
                accountTypeStr = "Free Trial";
                break;
            case UserEntity.FOREVER_FREE_USER:
                accountTypeStr = "Forever Free User";
                break;
            case UserEntity.MONTHLY_SUBSCRIPTION:
                accountTypeStr = "Monthly Subscription";
                break;
            case UserEntity.ANNUAL_SUBSCRIPTION:
                accountTypeStr = "Annual Subscription";
                break;
            default:
                accountTypeStr = "Unknown Account Type";
                break;
        }
        mAccountType.setText(accountTypeStr);

        if (accountType == UserEntity.FOREVER_FREE_USER) {
            mRenewalDate.setText("--/--/----");
            mEditSubscription.setVisibility(View.GONE);
            mCancelSubscription.setVisibility(View.GONE);
        } else {
            String date = Utility.convertMillisToFriendlyDate(userEntity.getRenewalDate());
            mRenewalDate.setText(date);
        }
    }
}
