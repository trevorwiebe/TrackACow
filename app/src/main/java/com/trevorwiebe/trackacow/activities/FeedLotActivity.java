package com.trevorwiebe.trackacow.activities;

import android.arch.persistence.room.Query;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.trevorwiebe.trackacow.dataLoaders.InsertCallEntity;
import com.trevorwiebe.trackacow.dataLoaders.InsertFeedEntities;
import com.trevorwiebe.trackacow.dataLoaders.QueryCallByLotIdAndDate;
import com.trevorwiebe.trackacow.dataLoaders.QueryFeedByLotIdAndDate;
import com.trevorwiebe.trackacow.dataLoaders.QueryLotByLotId;
import com.trevorwiebe.trackacow.dataLoaders.UpdateCallById;
import com.trevorwiebe.trackacow.db.entities.CallEntity;
import com.trevorwiebe.trackacow.db.entities.FeedEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.Utility;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Locale;

public class FeedLotActivity extends AppCompatActivity implements
        QueryCallByLotIdAndDate.OnCallByLotIdAndDateLoaded,
        QueryLotByLotId.OnLotByLotIdLoaded,
        QueryFeedByLotIdAndDate.OnFeedByLotIdAndDateLoaded,
        DeleteFeedEntitiesByDateAndLotId.OnFeedEntitiesByDateAndLotIdDeleted {

    private static final String TAG = "FeedLotActivity";

    private TextInputEditText mCall;
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

        mCall = findViewById(R.id.feed_lot_call_et);
        mFeedAgainLayout = findViewById(R.id.feed_again_layout);
        mTotalFed = findViewById(R.id.pen_total_fed);
        mLeftToFeed = findViewById(R.id.pen_left_to_feed);
        mSave = findViewById(R.id.save_feed_lot_btn);

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
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCall.length() == 0) {
                    mCall.requestFocus();
                    mCall.setError("Please fill this blank");
                } else {

                    // code for updating the call entity
                    int call = Integer.parseInt(mCall.getText().toString());

                    String callKey;
                    CallEntity callEntity = new CallEntity(call, mDate, mLotId, "id");

                    if (mSelectedCallEntity == null) {
                        DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(CallEntity.CALL).push();
                        callKey = baseRef.getKey();
                        callEntity.setId(callKey);
                        new InsertCallEntity(callEntity).execute(FeedLotActivity.this);
                    } else {
                        callKey = mSelectedCallEntity.getId();
                        new UpdateCallById(call, callKey).execute(FeedLotActivity.this);
                    }

                    // first we delete the old feed entities
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
            int call = mSelectedCallEntity.getAmountFed();
            String callStr = Integer.toString(call);
            mCall.setText(callStr);
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
                    Log.d(TAG, "getFeedsFromLayout: " + amountFed);
                    feedIntList.add(amountFed);
                } catch (ParseException e) {
                    Log.e(TAG, "getFeedsFromLayout: ", e);
                }
            }
        }
        return feedIntList;
    }

    @Override
    public void onFeedEntitiesByDateAndLotIdDeleted() {
        // code for updating the feed entities
        ArrayList<Integer> feedsIntList = getFeedsFromLayout();
        ArrayList<FeedEntity> newFeedEntityList = new ArrayList<>();
        for (int n = 0; n < feedsIntList.size(); n++) {

            int amountFed = feedsIntList.get(n);
            if (mFeedEntities.size() > n) {
                FeedEntity feedEntity = mFeedEntities.get(n);
                feedEntity.setFeed(amountFed);
                newFeedEntityList.add(feedEntity);
            } else {
                DatabaseReference feedRef = Constants.BASE_REFERENCE.child(FeedEntity.FEED).push();
                String feedEntityId = feedRef.getKey();
                FeedEntity feedEntity = new FeedEntity(amountFed, mDate, feedEntityId, mLotId);
                newFeedEntityList.add(feedEntity);
            }
        }
        new InsertFeedEntities(newFeedEntityList).execute(FeedLotActivity.this);

        finish();
    }
}
