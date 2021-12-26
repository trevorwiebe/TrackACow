package com.trevorwiebe.trackacow.activities;

import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.DeleteFeedEntitiesByDateAndLotId;
import com.trevorwiebe.trackacow.dataLoaders.DeleteFeedEntitiesById;
import com.trevorwiebe.trackacow.dataLoaders.InsertCallEntity;
import com.trevorwiebe.trackacow.dataLoaders.InsertFeedEntities;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingCall;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingFeedEntity;
import com.trevorwiebe.trackacow.dataLoaders.QueryCallByLotIdAndDate;
import com.trevorwiebe.trackacow.dataLoaders.QueryFeedByLotIdAndDate;
import com.trevorwiebe.trackacow.dataLoaders.QueryLotByLotId;
import com.trevorwiebe.trackacow.dataLoaders.UpdateCallById;
import com.trevorwiebe.trackacow.db.entities.CallEntity;
import com.trevorwiebe.trackacow.db.entities.FeedEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingCallEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingFeedEntity;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.Utility;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

public class FeedLotActivity extends AppCompatActivity implements
        QueryCallByLotIdAndDate.OnCallByLotIdAndDateLoaded,
        QueryLotByLotId.OnLotByLotIdLoaded,
        QueryFeedByLotIdAndDate.OnFeedByLotIdAndDateLoaded,
        DeleteFeedEntitiesByDateAndLotId.OnFeedEntitiesByDateAndLotIdDeleted {

    private static final String TAG = "FeedLotActivity";

    private TextInputEditText mCallET;
    private LinearLayout mFeedAgainLayout;
    private TextView mTotalFed;
    private TextView mLeftToFeed;
    private Button mSave;

    private CallEntity mSelectedCallEntity;
    private int mFeedAgainNumber = 0;
    private boolean isLoadingMore;
    private long mDate;
    private String mLotId;
    private boolean mShouldAddNewFeedEditText = false;
    private TextWatcher mFeedTextWatcher;
    private NumberFormat numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault());
    private ArrayList<FeedEntity> mFeedEntities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_lot);

        Intent intent = getIntent();

        mDate = intent.getLongExtra("date", 0);
        mLotId = intent.getStringExtra("lotId");

        mCallET = findViewById(R.id.feed_lot_call_et);
        mFeedAgainLayout = findViewById(R.id.feed_again_layout);
        mTotalFed = findViewById(R.id.pen_total_fed);
        mLeftToFeed = findViewById(R.id.pen_left_to_feed);
        mSave = findViewById(R.id.save_feed_lot_btn);

        mCallET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateReports();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mFeedTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    mShouldAddNewFeedEditText = shouldAddAnotherEditText();
                    if (mShouldAddNewFeedEditText) {
                        addNewFeedEditText(null);
                    }
                }
                updateReports();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallET.length() == 0) {
                    mCallET.requestFocus();
                    mCallET.setError("Please fill this blank");
                } else {

                    // code for updating the call entity
                    int callAmount = Integer.parseInt(mCallET.getText().toString());

                    DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(CallEntity.CALL);
                    DatabaseReference pushRef = baseRef.push();

                    String callKey = pushRef.getKey();
                    CallEntity callEntity = new CallEntity(callAmount, mDate, mLotId, callKey);

                    if (Utility.haveNetworkConnection(FeedLotActivity.this)) {
                        if (mSelectedCallEntity == null) {
                            // push new callEntity to cloud if null
                            pushRef.setValue(callEntity);
                        } else {
                            // push updated callEntity to cloud
                            mSelectedCallEntity.setCallAmount(callAmount);
                            baseRef.child(mSelectedCallEntity.getId()).setValue(mSelectedCallEntity);
                        }
                    } else {

                        Utility.setNewDataToUpload(FeedLotActivity.this, true);

                        HoldingCallEntity holdingCallEntity;

                        if(mSelectedCallEntity == null){
                            holdingCallEntity = new HoldingCallEntity(callEntity, Constants.INSERT_UPDATE);
                            new InsertHoldingCall(holdingCallEntity).execute(FeedLotActivity.this);
                        }else{
                            mSelectedCallEntity.setCallAmount(callAmount);
                            holdingCallEntity = new HoldingCallEntity(mSelectedCallEntity, Constants.INSERT_UPDATE);
                            new InsertHoldingCall(holdingCallEntity).execute(FeedLotActivity.this);
                        }
                    }

                    if (mSelectedCallEntity == null) {
                        new InsertCallEntity(callEntity).execute(FeedLotActivity.this);
                    } else {
                        callKey = mSelectedCallEntity.getId();
                        new UpdateCallById(callAmount, callKey).execute(FeedLotActivity.this);
                    }

                    // first we delete the all the local old feed entities; See onFeedEntitiesByDateAndLotIdDeleted to view the adding of feed entities
                    new DeleteFeedEntitiesByDateAndLotId(mDate, mLotId, FeedLotActivity.this).execute(FeedLotActivity.this);
                }

            }
        });

        new QueryCallByLotIdAndDate(mDate, mLotId, this).execute(this);
        new QueryFeedByLotIdAndDate(mDate, mLotId, this).execute(this);
        new QueryLotByLotId(mLotId, this).execute(this);
    }

    @Override
    public void onCallByLotIdAndDateLoaded(CallEntity callEntity) {
        mSelectedCallEntity = callEntity;
        if (callEntity == null) {
            mSave.setText("Save");
        } else {
            mSave.setText("Update");
            int call = mSelectedCallEntity.getCallAmount();
            String callStr = Integer.toString(call);
            mCallET.setText(callStr);
        }
    }

    @Override
    public void onFeedByLotIdAndDateLoaded(ArrayList<FeedEntity> feedEntities) {
        mFeedEntities = feedEntities;
        isLoadingMore = true;
        for (int o = 0; o < mFeedEntities.size(); o++) {
            FeedEntity feedEntity = mFeedEntities.get(o);
            int amountFed = feedEntity.getFeed();
            String amountFedStr = numberFormatter.format(amountFed);
            addNewFeedEditText(amountFedStr);
        }
        addNewFeedEditText(null);
        isLoadingMore = false;
    }

    @Override
    public void onLotByLotIdLoaded(LotEntity lotEntity) {
        String lotName = lotEntity.getLotName();
        String friendlyDate = Utility.convertMillisToFriendlyDate(mDate);
        setTitle(lotName + ": " + friendlyDate);
    }

    @Override
    public void onFeedEntitiesByDateAndLotIdDeleted() {
        // code for updating the feed entities
        ArrayList<Integer> feedsIntList = getFeedsFromLayout();
        ArrayList<FeedEntity> newFeedEntityList = new ArrayList<>();

        // iterate through the new feed amounts
        for (int n = 0; n < feedsIntList.size(); n++) {

            // get feed reference
            DatabaseReference feedRef = Constants.BASE_REFERENCE.child(FeedEntity.FEED);
            String feedEntityId = feedRef.push().getKey();

            // initialize feed entity
            FeedEntity feedEntity;
            int amountFed = feedsIntList.get(n);

            if(mFeedEntities.size() > n) {
                feedEntity = mFeedEntities.get(n);
                feedEntity.setFeed(amountFed);
            }else{
                feedEntity = new FeedEntity(amountFed, mDate, feedEntityId, mLotId);
            }

            if(Utility.haveNetworkConnection(FeedLotActivity.this)){
                // update cloud
                feedRef.child(feedEntity.getId()).setValue(feedEntity);
            }else{
                // if cloud not available, add to holding feed entity to upload later
                Utility.setNewDataToUpload(FeedLotActivity.this, true);
                HoldingFeedEntity holdingFeedEntity = new HoldingFeedEntity(feedEntity, Constants.INSERT_UPDATE);
                new InsertHoldingFeedEntity(holdingFeedEntity).execute(FeedLotActivity.this);
            }

            // add feedEntity to array to push to local database
            newFeedEntityList.add(feedEntity);
        }

        // insert new feed entities into local database
        new InsertFeedEntities(newFeedEntityList).execute(FeedLotActivity.this);

        // check to see if any feedEntities have been deleted
        if(mFeedEntities.size() > newFeedEntityList.size()){

            // yes, some have been deleted from the UI, let's go ahead and delete them from the database

            // get database reference
            DatabaseReference feedRef = Constants.BASE_REFERENCE.child(FeedEntity.FEED);

            for(int g=0; g<mFeedEntities.size(); g++){
                FeedEntity feedEntityToDelete = mFeedEntities.get(g);
                if(!newFeedEntityList.contains(feedEntityToDelete)){
                    String feedEntityIdToDelete = feedEntityToDelete.getId();

                    if(Utility.haveNetworkConnection(FeedLotActivity.this)){
                        // delete from cloud if available
                        feedRef.child(feedEntityIdToDelete).removeValue();
                    }else{
                        // add to holding database to delete when cloud is available
                        HoldingFeedEntity holdingFeedEntity = new HoldingFeedEntity(feedEntityToDelete, Constants.DELETE);
                        new InsertHoldingFeedEntity(holdingFeedEntity).execute(FeedLotActivity.this);
                    }

                    // delete locally
                    new DeleteFeedEntitiesById(feedEntityIdToDelete).execute(FeedLotActivity.this);

                }
            }
        }

        // close activity after all work is done
        finish();
    }

    private void addNewFeedEditText(@Nullable String text) {

        mFeedAgainNumber = mFeedAgainNumber + 1;

        final float scale = getResources().getDisplayMetrics().density;
        int pixels24 = (int) (24 * scale + 0.5f);
        int pixels16 = (int) (16 * scale + 0.5f);
        int pixels8 = (int) (8 * scale + 0.5f);

        LinearLayout linearLayout = new LinearLayout(FeedLotActivity.this);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        linearLayout.setId(mFeedAgainNumber);
        linearLayout.setLayoutParams(linearLayoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextInputLayout textInputLayout = new TextInputLayout(FeedLotActivity.this);
        LinearLayout.LayoutParams textInputLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textInputLayout.setLayoutParams(textInputLayoutParams);

        TextInputEditText textInputEditText = new TextInputEditText(FeedLotActivity.this);
        LinearLayout.LayoutParams textInputEditTextParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textInputEditTextParams.setMargins(pixels16, 0, pixels16, pixels8);
        textInputEditText.setLayoutParams(textInputEditTextParams);
        if (text != null) {
            textInputEditText.setText(text);
        }
        textInputEditText.setEms(6);
        textInputEditText.addTextChangedListener(mFeedTextWatcher);
        textInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        textInputEditText.setTextSize(16f);
        textInputEditText.setHint("Feed");

        textInputLayout.addView(textInputEditText);

        ImageButton deleteButton = new ImageButton(FeedLotActivity.this);

        LinearLayout.LayoutParams deleteBtnParams = new LinearLayout.LayoutParams(
                pixels24,
                pixels24
        );
        deleteButton.setPadding(pixels8, pixels8, pixels8, pixels8);
        deleteBtnParams.setMargins(0, pixels24, pixels16, pixels8);
        deleteButton.setBackground(getResources().getDrawable(R.drawable.ic_delete_black_24dp));
        deleteButton.setId(mFeedAgainNumber);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int viewToDelete = view.getId();

                for (int i = 0; i < mFeedAgainLayout.getChildCount(); i++) {
                    View v = mFeedAgainLayout.getChildAt(i);

                    int tagToCompare = v.getId();
                    if (tagToCompare == viewToDelete) {
                        i = i - 1;
                        mFeedAgainNumber = mFeedAgainNumber - 1;
                        mFeedAgainLayout.removeView(v);
                    }
                }

                updateReports();
            }
        });
        deleteButton.setLayoutParams(deleteBtnParams);

        linearLayout.addView(textInputLayout);
        linearLayout.addView(deleteButton);
        mFeedAgainLayout.addView(linearLayout);
    }

    private boolean shouldAddAnotherEditText() {
        if (isLoadingMore) return false;
        for (int i = 0; i < mFeedAgainLayout.getChildCount(); i++) {
            View v = mFeedAgainLayout.getChildAt(i);
            LinearLayout linearLayout = (LinearLayout) v;
            View textLayout = linearLayout.getChildAt(0);
            TextInputLayout textInputLayout = (TextInputLayout) textLayout;
            String text = textInputLayout.getEditText().getText().toString();
            if (text.length() == 0) return false;
        }
        return true;
    }

    private ArrayList<Integer> getFeedsFromLayout() {
        ArrayList<Integer> feedIntList = new ArrayList<>();
        for (int i = 0; i < mFeedAgainLayout.getChildCount(); i++) {
            View v = mFeedAgainLayout.getChildAt(i);
            LinearLayout linearLayout = (LinearLayout) v;
            View textLayout = linearLayout.getChildAt(0);
            TextInputLayout textInputLayout = (TextInputLayout) textLayout;
            String text = textInputLayout.getEditText().getText().toString();
            if (text.length() != 0) {
                try {
                    int amountFed = numberFormatter.parse(text).intValue();
                    feedIntList.add(amountFed);
                } catch (ParseException e) {
                    Log.e(TAG, "getFeedsFromLayout: ", e);
                }
            }
        }
        return feedIntList;
    }

    private void updateReports() {
        ArrayList<Integer> feedList = getFeedsFromLayout();
        int sum = 0;
        for (Integer i : feedList) sum += i;

        String totalFedStr = numberFormatter.format(sum);
        mTotalFed.setText(totalFedStr);

        String callStr;
        if (mCallET.length() == 0) {
            callStr = "0";
        } else {
            callStr = mCallET.getText().toString();
        }
        int call;
        try {
            call = numberFormatter.parse(callStr).intValue();
        } catch (ParseException e) {
            Log.e(TAG, "updateReports: ", e);
            call = 0;
        }
        int leftToFeed = call - sum;
        String leftToFeedStr = numberFormatter.format(leftToFeed);
        mLeftToFeed.setText(leftToFeedStr);
    }
}
