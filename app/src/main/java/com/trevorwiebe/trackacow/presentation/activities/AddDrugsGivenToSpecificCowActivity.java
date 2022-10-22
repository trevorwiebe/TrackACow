package com.trevorwiebe.trackacow.presentation.activities;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugsGivenEntity;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.drugsGiven.InsertDrugsGivenList;
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingDrugsGiven.InsertHoldingDrugsGivenList;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.drug.QueryAllDrugs;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.cow.QueryCowIdByCowId;
import com.trevorwiebe.trackacow.data.entities.CowEntity;
import com.trevorwiebe.trackacow.data.entities.DrugEntity;
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.domain.utils.Constants;
import com.trevorwiebe.trackacow.domain.utils.Utility;

import java.util.ArrayList;

public class AddDrugsGivenToSpecificCowActivity extends AppCompatActivity implements
        QueryAllDrugs.OnAllDrugsLoaded,
        QueryCowIdByCowId.OnCowByIdLoaded {


    private ArrayList<DrugEntity> mDrugList = new ArrayList<>();
    private DatabaseReference mBaseRef;
    private String mCowId;
    private CowEntity mCowEntity;

    private LinearLayout mDrugLayout;
    private TextView mNoDrugs;
    private Button mCancelButton;
    private Button mSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drugs_given_to_specific_cow);

        mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCowId = getIntent().getStringExtra("cowId");

        new QueryAllDrugs(this).execute(this);
        new QueryCowIdByCowId(AddDrugsGivenToSpecificCowActivity.this, mCowId).execute(AddDrugsGivenToSpecificCowActivity.this);

        mDrugLayout = findViewById(R.id.drug_layout);
        mNoDrugs = findViewById(R.id.no_drugs_added_to_specific_cow);
        mCancelButton = findViewById(R.id.add_drug_cancel_button);
        mSaveButton = findViewById(R.id.add_drug_save_button);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference drugsGivenRef = mBaseRef.child(Constants.DRUGS_GIVEN);

                ArrayList<DrugsGivenEntity> drugList = new ArrayList<>();

                for (int r = 0; r < mDrugLayout.getChildCount(); r++) {
                    DrugsGivenEntity drugsGivenEntity = new DrugsGivenEntity();
                    drugsGivenEntity.setDrugsGivenCowId(mCowEntity.getCowId());

                    View cardView = mDrugLayout.getChildAt(r);

                    if (cardView instanceof CardView) {

                        View linearLayout = ((CardView) cardView).getChildAt(0);

                        if (linearLayout instanceof LinearLayout) {

                            LinearLayout confirmedLinearLayout = (LinearLayout) linearLayout;

                            View checkBoxView = confirmedLinearLayout.getChildAt(0);
                            if (checkBoxView instanceof CheckBox) {

                                CheckBox checkBox = (CheckBox) checkBoxView;
                                String drugId = checkBox.getTag().toString().split("#")[0];
                                drugsGivenEntity.setDrugsGivenDrugId(drugId);

                                if (checkBox.isChecked()) {

                                    View editText = confirmedLinearLayout.getChildAt(2);

                                    if (editText instanceof EditText) {

                                        EditText textViewAmountGiven = (EditText) editText;
                                        int amountGiven = Integer.parseInt(textViewAmountGiven.getText().toString());
                                        drugsGivenEntity.setDrugsGivenAmountGiven(amountGiven);

                                        DatabaseReference drugsGivenPushRef = drugsGivenRef.push();
                                        String drugsGivenKey = drugsGivenPushRef.getKey();
                                        drugsGivenEntity.setDrugsGivenLotId(mCowEntity.getLotId());
                                        drugsGivenEntity.setDrugsGivenId(drugsGivenKey);
                                        drugsGivenEntity.setDrugsGivenDate(mCowEntity.getDate());

                                        drugList.add(drugsGivenEntity);

                                    }
                                }
                            }
                        }
                    }
                }

                if (drugList.size() == 0) {
                    Snackbar.make(v, "You must select a drug before saving", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (Utility.haveNetworkConnection(AddDrugsGivenToSpecificCowActivity.this)) {

                    for (int k = 0; k < drugList.size(); k++) {
                        DrugsGivenEntity drugsGivenEntity = drugList.get(k);
                        drugsGivenRef.child(drugsGivenEntity.getDrugsGivenId()).setValue(drugsGivenEntity);
                    }

                } else {

                    Utility.setNewDataToUpload(AddDrugsGivenToSpecificCowActivity.this, true);

                    // array list to hold the holdingDrugsGivenEntities so we can push them all at once to the local db
                    ArrayList<CacheDrugsGivenEntity> holdingDrugsGivenEntities = new ArrayList<>();

                    // iterate over the drugGivenEntityList
                    for (int q = 0; q < drugList.size(); q++) {
                        DrugsGivenEntity drugsGivenEntity = drugList.get(q);

                        CacheDrugsGivenEntity cacheDrugsGivenEntity = new CacheDrugsGivenEntity();
                        cacheDrugsGivenEntity.setAmountGiven(drugsGivenEntity.getDrugsGivenAmountGiven());
                        cacheDrugsGivenEntity.setCowId(drugsGivenEntity.getDrugsGivenCowId());
                        cacheDrugsGivenEntity.setDrugId(drugsGivenEntity.getDrugsGivenDrugId());
                        cacheDrugsGivenEntity.setDrugGivenId(drugsGivenEntity.getDrugsGivenId());
                        cacheDrugsGivenEntity.setWhatHappened(Constants.INSERT_UPDATE);
                        cacheDrugsGivenEntity.setLotId(drugsGivenEntity.getDrugsGivenLotId());

                        holdingDrugsGivenEntities.add(cacheDrugsGivenEntity);
                    }

                    new InsertHoldingDrugsGivenList(holdingDrugsGivenEntities).execute(AddDrugsGivenToSpecificCowActivity.this);

                }

                new InsertDrugsGivenList(drugList).execute(AddDrugsGivenToSpecificCowActivity.this);

                setResult(RESULT_OK);
                finish();

            }
        });

    }

    @Override
    public void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities) {
        mDrugList = drugEntities;
        for (int x = 0; x < mDrugList.size(); x++) {
            DrugEntity drugEntity = drugEntities.get(x);
            addCheckBox(mDrugLayout, drugEntity);
        }
        if (drugEntities.size() == 0) {
            mNoDrugs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCowByIdLoaded(CowEntity cowEntity) {
        mCowEntity = cowEntity;
        String tagNumber = Integer.toString(mCowEntity.getTagNumber());
        setTitle("Medicate cow " + tagNumber);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void addCheckBox(LinearLayout linearLayout, DrugEntity drugEntity) {

        String drugName = drugEntity.getDrugName();
        String drugId = drugEntity.getDrugCloudDatabaseId();
        int defaultAmount = drugEntity.getDefaultAmount();
        String defaultAmountStr = Integer.toString(defaultAmount);

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
        cardView.setTag(drugId + "#cardView");
        cardView.setOnClickListener(cardViewClickListener);

        LinearLayout containerLayout = new LinearLayout(this);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        containerLayout.setTag(drugId + "#linearLayout");
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
        checkBox.setTag(drugId + "#checkBox");
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
        editText.setTag(drugId + "#editText");
        editText.setText(defaultAmountStr);
        editText.setSelectAllOnFocus(true);
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
            String drugId = cardViewTag.split("#")[0];

            LinearLayout linearLayout = v.findViewWithTag(drugId + "#linearLayout");
            CheckBox checkBox = linearLayout.findViewWithTag(drugId + "#checkBox");
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
}
