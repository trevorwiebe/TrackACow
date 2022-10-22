package com.trevorwiebe.trackacow.presentation.activities;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugsGivenEntity;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.drugsGiven.InsertDrugsGivenList;
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingCow.InsertHoldingCow;
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingDrugsGiven.InsertHoldingDrugsGivenList;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.drugsGiven.QueryDrugsGivenByCowIdList;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.QueryLotsByPenId;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.cow.QueryMedicatedCowsByLotIds;
import com.trevorwiebe.trackacow.data.entities.CowEntity;
import com.trevorwiebe.trackacow.data.entities.DrugEntity;
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.data.entities.LotEntity;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.drug.QueryAllDrugs;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.cow.InsertSingleCow;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheCowEntity;
import com.trevorwiebe.trackacow.domain.utils.Constants;
import com.trevorwiebe.trackacow.domain.utils.Utility;

import java.util.ArrayList;

public class MedicateACowActivity extends AppCompatActivity implements
        QueryAllDrugs.OnAllDrugsLoaded,
        QueryMedicatedCowsByLotIds.OnCowsByLotIdLoaded,
        QueryDrugsGivenByCowIdList.OnDrugsGivenByCowIdListLoaded,
        QueryLotsByPenId.OnLotsByPenIdLoaded {

    private static final String TAG = "MedicateACowActivity";

    private ScrollView mMainScrollView;
    private TextInputEditText mTagName;
    private TextInputEditText mNotes;
    private LinearLayout mDrugLayout;
    private LinearLayout mMoreDrugsGivenLayout;
    private CardView mDrugsGivenCardView;
    private ProgressBar mLoadDrugs;
    private TextView mNoDrugs;
    private Button mSaveCow;
    private TextView mMedicateACowMessage;
//    private Button mViewMedications;
    private Button mAddMemoBtn;
    private TextInputLayout mNotesLayout;

    private ArrayList<DrugEntity> mDrugList = new ArrayList<>();
    private ArrayList<CowEntity> mCowEntities = new ArrayList<>();
    private boolean mIsSearchForCowDead = false;
    private LotEntity mSelectedLot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicate_a_cow);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMainScrollView = findViewById(R.id.main_scroll_view);
        mTagName = findViewById(R.id.tag_number);
        mNotes = findViewById(R.id.notes);
        mNoDrugs = findViewById(R.id.no_drugs_added);
        mDrugsGivenCardView = findViewById(R.id.drugs_given_card_view);
        mMoreDrugsGivenLayout = findViewById(R.id.more_drugs_given_layout);
        mDrugLayout = findViewById(R.id.drug_layout);
        mLoadDrugs = findViewById(R.id.medicate_loading_drugs);
        mSaveCow = findViewById(R.id.save_medicated_cow);
        mMedicateACowMessage = findViewById(R.id.medicate_a_cow_message_center);
//        mViewMedications = findViewById(R.id.view_medications_given_btn);
        mAddMemoBtn = findViewById(R.id.add_notes_btn);
        mNotesLayout = findViewById(R.id.notes_layout);

        String penId = getIntent().getStringExtra("penId");

        new QueryLotsByPenId(penId, this).execute(MedicateACowActivity.this);

        new QueryAllDrugs(this).execute(this);

        mSaveCow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCow();
            }
        });

        mAddMemoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddMemoBtn.setVisibility(View.GONE);
                mNotesLayout.setVisibility(View.VISIBLE);
            }
        });

        mTagName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    int tagNumber = Integer.parseInt(s.toString());
                    ArrayList<CowEntity> cowEntities = getCowsTreated(tagNumber);
                    if (cowEntities.size() != 0) {
                        ArrayList<String> cowIds = new ArrayList<>();
                        for(int f=0; f<cowEntities.size(); f++){
                            CowEntity cowEntity = cowEntities.get(f);
                            cowIds.add(cowEntity.getCowId());
                            if (cowEntity.isAlive() != 1) {
                                mIsSearchForCowDead = true;
                            }
                        }
                        new QueryDrugsGivenByCowIdList(MedicateACowActivity.this, cowIds).execute(MedicateACowActivity.this);
                    } else {
                        mDrugsGivenCardView.setVisibility(View.GONE);
                        mMoreDrugsGivenLayout.removeAllViews();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        mViewMedications.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mMoreDrugsGivenLayout.getVisibility() == View.VISIBLE) {
//                    mMoreDrugsGivenLayout.setVisibility(View.GONE);
//                    mViewMedications.setText("View");
//                } else {
//                    mMoreDrugsGivenLayout.setVisibility(View.VISIBLE);
//                    mViewMedications.setText("Hide");
//                }
//            }
//        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    public void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities) {
        mDrugList = drugEntities;
        Log.d(TAG, "onAllDrugsLoaded: " + mDrugList.size());
        for(int x=0; x<mDrugList.size(); x++){
            DrugEntity drugEntity = drugEntities.get(x);
            Log.d(TAG, "onAllDrugsLoaded: " + x);
            addCheckBox(mDrugLayout, drugEntity, false);
        }
        if (drugEntities.size() == 0) {
            mNoDrugs.setVisibility(View.VISIBLE);
        }
        mLoadDrugs.setVisibility(View.GONE);
    }

    private void addCheckBox(LinearLayout linearLayout, DrugEntity drugEntity, boolean isLastCheckbox) {

        String drugName = drugEntity.getDrugName();
        String drugId = drugEntity.getDrugCloudDatabaseId();
        int defaultAmount = drugEntity.getDefaultAmount();
        String defaultAmountStr = Integer.toString(defaultAmount);

        Log.d(TAG, "addCheckBox: " + drugId);

        final float scale = getResources().getDisplayMetrics().density;
        int pixels24 = (int) (24 * scale + 0.5f);
        int pixels16 = (int) (16 * scale + 0.5f);
        int pixels8 = (int) (8 * scale + 0.5f);

        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardViewParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardViewParams.setMargins(pixels8, pixels8, pixels8, pixels8);
        cardView.setLayoutParams(cardViewParams);
        cardView.setTag(drugId + "&cardView");
        cardView.setOnClickListener(cardViewClickListener);

        LinearLayout containerLayout = new LinearLayout(this);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        containerLayout.setTag(drugId + "&linearLayout");
        containerLayout.setOrientation(LinearLayout.HORIZONTAL);
        containerLayout.setLayoutParams(containerParams);

        cardView.addView(containerLayout);

        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(drugName);
        checkBox.setTag(drugId);
        LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        checkBoxParams.setMargins(pixels24, pixels8, pixels24, pixels8);
        checkBox.setTag(drugId + "&checkBox");
        checkBox.setOnCheckedChangeListener(checkedChangeListener);
        checkBox.setLayoutParams(checkBoxParams);

        View view = new View(this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(
                0,
                0,
                1
        );
        view.setLayoutParams(viewParams);

        EditText editText = new EditText(this);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        editTextParams.setMargins(pixels24, pixels8, pixels24, pixels8);
        editText.setEms(4);
        editText.setGravity(Gravity.CENTER);
        editText.setTag(drugId + "&editText");
        editText.setText(defaultAmountStr);
        editText.setSelectAllOnFocus(true);
        editText.setOnEditorActionListener(doneListener);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setLayoutParams(editTextParams);

        containerLayout.addView(checkBox);
        containerLayout.addView(view);
        containerLayout.addView(editText);

        linearLayout.addView(cardView);
    }

    View.OnClickListener cardViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String cardViewTag = v.getTag().toString();
            String drugId = cardViewTag.split("&")[0];

            LinearLayout linearLayout = v.findViewWithTag(drugId + "&linearLayout");
            CheckBox checkBox = linearLayout.findViewWithTag(drugId + "&checkBox");
            if (checkBox.isChecked()) {
                checkBox.setChecked(false);
                v.setBackgroundColor(getResources().getColor(android.R.color.white));
            } else {
                checkBox.setChecked(true);
                v.setBackgroundColor(getResources().getColor(R.color.colorAccentVeryLight));
            }
        }
    };

    CheckBox.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            LinearLayout linearLayout = (LinearLayout) buttonView.getParent();
            CardView cardView = (CardView) linearLayout.getParent();
            if (isChecked) {
                cardView.setBackgroundColor(getResources().getColor(R.color.colorAccentVeryLight));
            } else {
                cardView.setBackgroundColor(getResources().getColor(android.R.color.white));
            }
        }
    };

    TextView.OnEditorActionListener doneListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                saveCow();
            }
            return false;
        }
    };

    private void saveCow() {
        if (mDrugList.size() == 0) {
            Snackbar.make(mTagName, "Please add a drug first.", Snackbar.LENGTH_LONG).show();
        } else if (mTagName.length() == 0) {
            mMainScrollView.fullScroll(ScrollView.FOCUS_UP);
            mTagName.requestFocus();
            mTagName.setError("Please fill this blank.");
        } else {
            DatabaseReference drugsGivenRef = Constants.BASE_REFERENCE.child(Constants.DRUGS_GIVEN);

            DatabaseReference pushRef = Constants.BASE_REFERENCE.child(Constants.COW).push();
            String cowId = pushRef.getKey();

            ArrayList<DrugsGivenEntity> drugList = new ArrayList<>();

            for (int r = 0; r < mDrugLayout.getChildCount(); r++) {
                DrugsGivenEntity drugsGivenEntity = new DrugsGivenEntity();
                drugsGivenEntity.setDrugsGivenCowId(cowId);

                View cardView = mDrugLayout.getChildAt(r);

                if (cardView instanceof CardView) {

                    View linearLayout = ((CardView) cardView).getChildAt(0);

                    if (linearLayout instanceof LinearLayout) {

                        LinearLayout confirmedLinearLayout = (LinearLayout) linearLayout;

                        View checkBoxView = confirmedLinearLayout.getChildAt(0);
                        if (checkBoxView instanceof CheckBox) {

                            CheckBox checkBox = (CheckBox) checkBoxView;
                            String drugId = checkBox.getTag().toString().split("&")[0];
                            drugsGivenEntity.setDrugsGivenDrugId(drugId);

                            if (checkBox.isChecked()) {

                                View editText = confirmedLinearLayout.getChildAt(2);

                                if (editText instanceof EditText) {

                                    EditText textViewAmountGiven = (EditText) editText;
                                    int amountGiven = Integer.parseInt(textViewAmountGiven.getText().toString());
                                    drugsGivenEntity.setDrugsGivenAmountGiven(amountGiven);

                                    DatabaseReference drugsGivenPushRef = drugsGivenRef.push();
                                    String drugsGivenKey = drugsGivenPushRef.getKey();
                                    drugsGivenEntity.setDrugsGivenLotId(mSelectedLot.getLotCloudDatabaseId());
                                    drugsGivenEntity.setDrugsGivenId(drugsGivenKey);
                                    drugsGivenEntity.setDrugsGivenDate(System.currentTimeMillis());

                                    drugList.add(drugsGivenEntity);

                                }
                            }
                        }
                    }
                }
            }

            int tagNumber = Integer.parseInt(mTagName.getText().toString());
            String notes = mNotes.getText().toString();

            CowEntity cowEntity = new CowEntity(0, 0, cowId, tagNumber, System.currentTimeMillis(), notes, mSelectedLot.getLotCloudDatabaseId());

            mCowEntities.add(cowEntity);

            if (Utility.haveNetworkConnection(MedicateACowActivity.this)) {
                pushRef.setValue(cowEntity);

                for (int k = 0; k < drugList.size(); k++) {
                    DrugsGivenEntity drugsGivenEntity = drugList.get(k);
                    drugsGivenRef.child(drugsGivenEntity.getDrugsGivenId()).setValue(drugsGivenEntity);
                }

            } else {

                Utility.setNewDataToUpload(MedicateACowActivity.this, true);
                CacheCowEntity cacheCowEntity = new CacheCowEntity(
                        0,
                        cowEntity.isAlive(),
                        cowEntity.getCowId(),
                        cowEntity.getTagNumber(),
                        cowEntity.getDate(),
                        cowEntity.getNotes(),
                        cowEntity.getLotId(),
                        Constants.INSERT_UPDATE
                );
                new InsertHoldingCow(cacheCowEntity).execute(MedicateACowActivity.this);

                // array list to hold the holdingDrugsGivenEntities so we can push them all at once to the local db
                ArrayList<CacheDrugsGivenEntity> holdingDrugsGivenEntities = new ArrayList<>();

                // iterate over the drugGivenEntityList
                for (int q = 0; q < drugList.size(); q++) {
                    DrugsGivenEntity drugsGivenEntity = drugList.get(q);
                    CacheDrugsGivenEntity cacheDrugsGivenEntity = new CacheDrugsGivenEntity(
                            0,
                            drugsGivenEntity.getDrugsGivenId(),
                            drugsGivenEntity.getDrugsGivenDrugId(),
                            drugsGivenEntity.getDrugsGivenAmountGiven(),
                            drugsGivenEntity.getDrugsGivenCowId(),
                            drugsGivenEntity.getDrugsGivenLotId(),
                            drugsGivenEntity.getDrugsGivenDate(),
                            Constants.INSERT_UPDATE
                    );
                    holdingDrugsGivenEntities.add(cacheDrugsGivenEntity);
                }

                new InsertHoldingDrugsGivenList(holdingDrugsGivenEntities).execute(MedicateACowActivity.this);

            }

            new InsertSingleCow(cowEntity).execute(MedicateACowActivity.this);
            new InsertDrugsGivenList(drugList).execute(MedicateACowActivity.this);

            mTagName.setText("");
            mNotes.setText("");

            mDrugsGivenCardView.setVisibility(View.GONE);

            for (int r = 0; r < mDrugLayout.getChildCount(); r++) {
                View cardView = mDrugLayout.getChildAt(r);
                if (cardView instanceof CardView) {
                    View linearLayout = ((CardView) cardView).getChildAt(0);
                    if (linearLayout instanceof LinearLayout) {
                        LinearLayout linearLayout1 = (LinearLayout) linearLayout;
                        View checkBoxView = linearLayout1.getChildAt(0);
                        if (checkBoxView instanceof CheckBox) {
                            CheckBox checkBox = (CheckBox) checkBoxView;
                            checkBox.setChecked(false);
                        }
                    }
                }
            }

            mMainScrollView.fullScroll(ScrollView.FOCUS_UP);
            mTagName.requestFocus();
            mNotesLayout.setVisibility(View.GONE);
            mAddMemoBtn.setVisibility(View.VISIBLE);

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @Override
    public void onCowsByLotIdLoaded(ArrayList<CowEntity> cowObjectList) {
        mCowEntities = cowObjectList;
    }

    @Override
    public void onDrugsGivenByCowIdListLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        if(drugsGivenEntities.size() != 0){
            mDrugsGivenCardView.setVisibility(View.VISIBLE);
//            mViewMedications.setVisibility(View.VISIBLE);
            if(mIsSearchForCowDead){
                mMedicateACowMessage.setText("This cow is dead and has been medicated");
                mDrugsGivenCardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }else{
                mMedicateACowMessage.setText("This cow has been medicated");
                mDrugsGivenCardView.setCardBackgroundColor(getResources().getColor(R.color.greenText));
            }
            final float scale = getResources().getDisplayMetrics().density;
            int pixels16 = (int) (16 * scale + 0.5f);
            int pixels8 = (int) (8 * scale + 0.5f);
            int pixels4 = (int) (4 * scale + 0.5f);
            for (int r = 0; r < drugsGivenEntities.size(); r++) {
                DrugsGivenEntity drugsGivenEntity = drugsGivenEntities.get(r);
                DrugEntity drugEntity = Utility.findDrugEntity(drugsGivenEntity.getDrugsGivenDrugId(), mDrugList);

                String amountGivenStr = Integer.toString(drugsGivenEntity.getDrugsGivenAmountGiven());
                String drugName;
                String date = Utility.convertMillisToDate(drugsGivenEntity.getDrugsGivenDate());
                if (drugEntity == null) {
                    drugName = "[drug_unavailable]";
                } else {
                    drugName = drugEntity.getDrugName();
                }

                String textToSet = amountGivenStr + " units of " + drugName + " on " +date;
                TextView textView = new TextView(MedicateACowActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(pixels16, 0, pixels16, pixels4);
                textView.setLayoutParams(params);
                textView.setTextColor(getResources().getColor(android.R.color.white));
                textView.setText(textToSet);

                mMoreDrugsGivenLayout.addView(textView);
            }
        }else{
            if(mIsSearchForCowDead){
                mDrugsGivenCardView.setVisibility(View.VISIBLE);
                mMedicateACowMessage.setText("This cow is dead");
                mDrugsGivenCardView.setCardBackgroundColor(getResources().getColor(R.color.redText));
//                mViewMedications.setVisibility(View.INVISIBLE);
            }else{
                mDrugsGivenCardView.setVisibility(View.GONE);
            }
        }
        mIsSearchForCowDead = false;
    }

    private ArrayList<CowEntity> getCowsTreated(int tagNumber){
        ArrayList<CowEntity> cowEntities = new ArrayList<>();
        for(int e=0; e<mCowEntities.size(); e++){
            CowEntity cowEntity = mCowEntities.get(e);
            if(cowEntity.getTagNumber() == tagNumber){
                cowEntities.add(cowEntity);
            }
        }
        return cowEntities;
    }

    @Override
    public void onLotsByPenIdLoaded(ArrayList<LotEntity> lotEntities) {
        mSelectedLot = lotEntities.get(0);
        ArrayList<String> lotIds = new ArrayList<>();
        for (int x = 0; x < lotEntities.size(); x++) {
            LotEntity lotEntity = lotEntities.get(x);
            String lotId = lotEntity.getLotCloudDatabaseId();
            lotIds.add(lotId);
        }
        new QueryMedicatedCowsByLotIds(this, lotIds).execute(this);

    }
}
