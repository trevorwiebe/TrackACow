package com.trevorwiebe.trackacow.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_subscription);

        mAccountType = findViewById(R.id.account_type);
        mRenewalDate = findViewById(R.id.renewal_date);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        new QueryUserByUid(uid, this).execute(this);

    }

    @Override
    public void onUserByUidLoaded(UserEntity userEntity) {

        int accountType = userEntity.getAccountType();
        String accountTypeStr = "";
        switch (accountType) {
            case UserEntity.FREE_TRIAL:
                accountTypeStr = "One month free trial";
                break;
            case UserEntity.FOREVER_FREE_USER:
                accountTypeStr = "Forever free user";
                break;
            case UserEntity.MONTHLY_SUBSCRIPTION:
                accountTypeStr = "Monthly subscription";
                break;
            case UserEntity.ANNUAL_SUBSCRIPTION:
                accountTypeStr = "Annual subscription";
                break;
            case UserEntity.GRACE_PERIOD:
                accountTypeStr = "Grace period";
                break;
            case UserEntity.HOLD:
                accountTypeStr = "Hold";
                break;
            case UserEntity.CANCELED:
                accountTypeStr = "Canceled";
                break;
        }
        mAccountType.setText(accountTypeStr);

        if (accountType == UserEntity.FOREVER_FREE_USER) {
            mRenewalDate.setText("--/--/----");
        } else {
            String date = Utility.convertMillisToFriendlyDate(userEntity.getRenewalDate());
            mRenewalDate.setText(date);
        }
    }
}
