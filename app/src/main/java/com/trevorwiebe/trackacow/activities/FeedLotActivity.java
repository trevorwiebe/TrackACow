package com.trevorwiebe.trackacow.activities;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.InsertCallEntity;
import com.trevorwiebe.trackacow.dataLoaders.QueryCallByLotIdAndDate;
import com.trevorwiebe.trackacow.dataLoaders.UpdateCallById;
import com.trevorwiebe.trackacow.db.entities.CallEntity;

public class FeedLotActivity extends AppCompatActivity implements QueryCallByLotIdAndDate.OnCallByLotIdAndDateLoaded {

    private TextInputEditText mCall;
    private Button mSave;

    private CallEntity mSelectedCallEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_lot);

        Intent intent = getIntent();

        final long date = intent.getLongExtra("date", 0);
        final String lotId = intent.getStringExtra("lotId");

        mCall = findViewById(R.id.feed_lot_call_et);
        mSave = findViewById(R.id.save_feed_lot_btn);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCall.length() == 0) {
                    mCall.requestFocus();
                    mCall.setError("Please fill this blank");
                } else {

                    int call = Integer.parseInt(mCall.getText().toString());

                    String callKey;
                    CallEntity callEntity = new CallEntity(call, date, lotId, "id");

                    if (mSelectedCallEntity == null) {
                        DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(CallEntity.CALL).push();
                        callKey = baseRef.getKey();
                        callEntity.setId(callKey);
                        new InsertCallEntity(callEntity).execute(FeedLotActivity.this);
                    } else {
                        callKey = mSelectedCallEntity.getId();
                        new UpdateCallById(call, callKey).execute(FeedLotActivity.this);
                    }

                    mCall.setText("");
                }

            }
        });

        new QueryCallByLotIdAndDate(date, lotId, this).execute(this);
    }

    @Override
    public void onCallByLotIdAndDateLoaded(CallEntity callEntity) {
        mSelectedCallEntity = callEntity;
        if (callEntity == null) {
            mSave.setText("Save and Next Pen");
        } else {
            mSave.setText("Update and Next Pen");
            int call = mSelectedCallEntity.getAmountFed();
            String callStr = Integer.toString(call);
            mCall.setText(callStr);
        }
    }
}
