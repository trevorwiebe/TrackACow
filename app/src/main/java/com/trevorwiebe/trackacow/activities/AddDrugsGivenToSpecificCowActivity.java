package com.trevorwiebe.trackacow.activities;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.trevorwiebe.trackacow.dataLoaders.InsertDrugsGivenList;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingDrugsGivenList;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.QueryCowIdByCowId;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugsGivenEntity;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.Utility;

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

                DatabaseReference drugsGivenRef = mBaseRef.child(DrugsGivenEntity.DRUGS_GIVEN);

                ArrayList<DrugsGivenEntity> drugList = new ArrayList<>();

                for (int r = 0; r < mDrugLayout.getChildCount(); r++) {
                    DrugsGivenEntity drugsGivenEntity = new DrugsGivenEntity();
                    drugsGivenEntity.setCowId(mCowId);

                    View linearLayout = mDrugLayout.getChildAt(r);

                    if (linearLayout instanceof LinearLayout) {

                        LinearLayout confirmedLinearLayout = (LinearLayout) linearLayout;

                        View checkBoxView = confirmedLinearLayout.getChildAt(0);
                        if (checkBoxView instanceof CheckBox) {

                            CheckBox checkBox = (CheckBox) checkBoxView;
                            drugsGivenEntity.setDrugId(checkBox.getTag().toString());
                            if (checkBox.isChecked()) {

                                View editText = confirmedLinearLayout.getChildAt(1);

                                if (editText instanceof EditText) {

                                    EditText textViewAmountGiven = (EditText) editText;
                                    int amountGiven = Integer.parseInt(textViewAmountGiven.getText().toString());
                                    drugsGivenEntity.setAmountGiven(amountGiven);

                                    DatabaseReference drugsGivenPushRef = drugsGivenRef.push();
                                    String drugsGivenKey = drugsGivenPushRef.getKey();
                                    drugsGivenEntity.setLotId(mCowEntity.getLotId());
                                    drugsGivenEntity.setDrugGivenId(drugsGivenKey);

                                    if (Utility.haveNetworkConnection(AddDrugsGivenToSpecificCowActivity.this)) {
                                        drugsGivenPushRef.setValue(drugsGivenEntity);
                                    }

                                    drugList.add(drugsGivenEntity);

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
                        drugsGivenRef.child(drugsGivenEntity.getDrugGivenId()).setValue(drugsGivenEntity);
                    }

                } else {

                    Utility.setNewDataToUpload(AddDrugsGivenToSpecificCowActivity.this, true);

                    // array list to hold the holdingDrugsGivenEntities so we can push them all at once to the local db
                    ArrayList<HoldingDrugsGivenEntity> holdingDrugsGivenEntities = new ArrayList<>();

                    // iterate over the drugGivenEntityList
                    for (int q = 0; q < drugList.size(); q++) {
                        DrugsGivenEntity drugsGivenEntity = drugList.get(q);

                        HoldingDrugsGivenEntity holdingDrugsGivenEntity = new HoldingDrugsGivenEntity();
                        holdingDrugsGivenEntity.setAmountGiven(drugsGivenEntity.getAmountGiven());
                        holdingDrugsGivenEntity.setCowId(drugsGivenEntity.getCowId());
                        holdingDrugsGivenEntity.setDrugId(drugsGivenEntity.getDrugId());
                        holdingDrugsGivenEntity.setDrugGivenId(drugsGivenEntity.getDrugGivenId());
                        holdingDrugsGivenEntity.setWhatHappened(Constants.INSERT_UPDATE);
                        holdingDrugsGivenEntity.setLotId(drugsGivenEntity.getLotId());

                        holdingDrugsGivenEntities.add(holdingDrugsGivenEntity);
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
        setTitle("Add drugs given to cow " + tagNumber);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void addCheckBox(LinearLayout linearLayout, DrugEntity drugEntity) {
        String drugName = drugEntity.getDrugName();
        String drugId = drugEntity.getDrugId();
        int defaultAmount = drugEntity.getDefaultAmount();
        String defaultAmountStr = Integer.toString(defaultAmount);

        final float scale = getResources().getDisplayMetrics().density;
        int pixels24 = (int) (24 * scale + 0.5f);
        int pixels16 = (int) (16 * scale + 0.5f);
        int pixels8 = (int) (8 * scale + 0.5f);


        LinearLayout containerLayout = new LinearLayout(this);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        containerLayout.setOrientation(LinearLayout.HORIZONTAL);
        containerLayout.setLayoutParams(containerParams);

        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(drugName);
        checkBox.setTag(drugId);
        LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        checkBoxParams.setMargins(pixels16, pixels24, pixels16, pixels8);
        checkBox.setLayoutParams(checkBoxParams);

        EditText editText = new EditText(this);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        editTextParams.setMargins(pixels16, pixels24, pixels16, pixels8);
        editText.setEms(4);
        editText.setGravity(Gravity.CENTER);
        editText.setTag(drugId + "_editText");
        editText.setText(defaultAmountStr);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setLayoutParams(editTextParams);

        containerLayout.addView(checkBox);
        containerLayout.addView(editText);

        linearLayout.addView(containerLayout);
    }

    private CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            String drugId = compoundButton.getTag().toString();
            EditText editText = mDrugLayout.findViewWithTag(drugId + "_editText");
            if (isChecked) {
                editText.setVisibility(View.VISIBLE);
            } else {
                editText.setVisibility(View.GONE);
            }
        }
    };
}
