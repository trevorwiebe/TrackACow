package com.trevorwiebe.trackacow.activities;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.InsertDrugsGivenList;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingCow;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingDrugsGivenList;
import com.trevorwiebe.trackacow.dataLoaders.QueryDrugsGivenByCowIdList;
import com.trevorwiebe.trackacow.dataLoaders.QueryLotsByPenId;
import com.trevorwiebe.trackacow.dataLoaders.QueryMedicatedCowsByLotIds;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.InsertSingleCow;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingCowEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugsGivenEntity;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;

public class MedicateACowActivity extends AppCompatActivity implements
        QueryAllDrugs.OnAllDrugsLoaded,
        QueryMedicatedCowsByLotIds.OnCowsByLotIdLoaded,
        QueryDrugsGivenByCowIdList.OnDrugsGivenByCowIdListLoaded,
        QueryLotsByPenId.OnLotsByPenIdLoaded {

    private static final String TAG = "MedicateACowActivity";

    private TextInputEditText mTagName;
    private TextInputEditText mNotes;
    private LinearLayout mDrugLayout;
    private LinearLayout mMoreDrugsGivenLayout;
    private CardView mDrugsGivenCardView;
    private ProgressBar mLoadDrugs;
    private TextView mNoDrugs;
    private Button mSaveCow;
    private TextView mMedicateACowMessage;
    private Button mViewMedications;

    private ArrayList<DrugEntity> mDrugList = new ArrayList<>();
    private ArrayList<CowEntity> mCowEntities = new ArrayList<>();
    private boolean mIsSearchForCowDead = false;
    private LotEntity mSelectedLot;
    private DatabaseReference mBaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicate_a_cow);

        mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mTagName = findViewById(R.id.tag_number);
        mNotes = findViewById(R.id.notes);
        mNoDrugs = findViewById(R.id.no_drugs_added);
        mDrugsGivenCardView = findViewById(R.id.drugs_given_card_view);
        mMoreDrugsGivenLayout = findViewById(R.id.more_drugs_given_layout);
        mDrugLayout = findViewById(R.id.drug_layout);
        mLoadDrugs = findViewById(R.id.medicate_loading_drugs);
        mSaveCow = findViewById(R.id.save_medicated_cow);
        mMedicateACowMessage = findViewById(R.id.medicate_a_cow_message_center);
        mViewMedications = findViewById(R.id.view_medications_given_btn);

        String penId = getIntent().getStringExtra("penId");

        new QueryLotsByPenId(penId, this).execute(MedicateACowActivity.this);

        new QueryAllDrugs(this).execute(this);

        mSaveCow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDrugList.size() == 0){
                    Snackbar.make(view, "Please add a drug first.", Snackbar.LENGTH_LONG).show();
                }else if(mTagName.length() == 0){
                    Snackbar.make(view, "Please set the tag number", Snackbar.LENGTH_LONG).show();
                }else{
                    DatabaseReference drugsGivenRef = mBaseRef.child(DrugsGivenEntity.DRUGS_GIVEN);

                    DatabaseReference pushRef = mBaseRef.child(CowEntity.COW).push();
                    String cowId = pushRef.getKey();

                    ArrayList<DrugsGivenEntity> drugList = new ArrayList<>();

                    for(int r=0; r<mDrugLayout.getChildCount(); r++){
                        DrugsGivenEntity drugsGivenEntity = new DrugsGivenEntity();
                        drugsGivenEntity.setCowId(cowId);
                        View checkBoxView = mDrugLayout.getChildAt(r);
                        if(checkBoxView instanceof CheckBox){
                            CheckBox checkBox = (CheckBox) checkBoxView;
                            drugsGivenEntity.setDrugId(checkBox.getTag().toString());
                            if(checkBox.isChecked()){
                                r++;
                                View  editText = mDrugLayout.getChildAt(r);

                                if(editText instanceof EditText){

                                    EditText textViewAmountGiven = (EditText) editText;
                                    int amountGiven = Integer.parseInt(textViewAmountGiven.getText().toString());
                                    drugsGivenEntity.setAmountGiven(amountGiven);

                                    DatabaseReference drugsGivenPushRef = drugsGivenRef.push();
                                    String drugsGivenKey = drugsGivenPushRef.getKey();
                                    drugsGivenEntity.setLotId(mSelectedLot.getLotId());
                                    drugsGivenEntity.setDrugGivenId(drugsGivenKey);

                                    if(Utility.haveNetworkConnection(MedicateACowActivity.this)){
                                        drugsGivenPushRef.setValue(drugsGivenEntity);
                                    }

                                    drugList.add(drugsGivenEntity);

                                }
                            }
                        }
                    }

                    int tagNumber = Integer.parseInt(mTagName.getText().toString());
                    String notes = mNotes.getText().toString();

                    CowEntity cowEntity = new CowEntity(1, cowId, tagNumber, System.currentTimeMillis(), notes, mSelectedLot.getLotId());

                    mCowEntities.add(cowEntity);

                    if(Utility.haveNetworkConnection(MedicateACowActivity.this)){
                        pushRef.setValue(cowEntity);

                        for(int k=0; k<drugList.size(); k++){
                            DrugsGivenEntity drugsGivenEntity = drugList.get(k);
                            mBaseRef.child(DrugsGivenEntity.DRUGS_GIVEN).child(drugsGivenEntity.getDrugGivenId()).setValue(drugsGivenEntity);
                        }

                    }else{

                        Utility.setNewDataToUpload(MedicateACowActivity.this, true);

                        HoldingCowEntity holdingCowEntity = new HoldingCowEntity();
                        holdingCowEntity.setCowId(cowEntity.getCowId());
                        holdingCowEntity.setDate(cowEntity.getDate());
                        holdingCowEntity.setIsAlive(cowEntity.isAlive());
                        holdingCowEntity.setNotes(cowEntity.getNotes());
                        holdingCowEntity.setLotId(cowEntity.getLotId());
                        holdingCowEntity.setTagNumber(cowEntity.getTagNumber());
                        holdingCowEntity.setWhatHappened(Utility.INSERT_UPDATE);

                        new InsertHoldingCow(holdingCowEntity).execute(MedicateACowActivity.this);

                        // array list to hold the holdingDrugsGivenEntities so we can push them all at once to the local db
                        ArrayList<HoldingDrugsGivenEntity> holdingDrugsGivenEntities = new ArrayList<>();

                        // iterate over the drugGivenEntityList
                        for(int q=0; q<drugList.size(); q++){
                            DrugsGivenEntity drugsGivenEntity = drugList.get(q);

                            HoldingDrugsGivenEntity holdingDrugsGivenEntity = new HoldingDrugsGivenEntity();
                            holdingDrugsGivenEntity.setAmountGiven(drugsGivenEntity.getAmountGiven());
                            holdingDrugsGivenEntity.setCowId(drugsGivenEntity.getCowId());
                            holdingDrugsGivenEntity.setDrugId(drugsGivenEntity.getDrugId());
                            holdingDrugsGivenEntity.setDrugGivenId(drugsGivenEntity.getDrugGivenId());
                            holdingDrugsGivenEntity.setWhatHappened(Utility.INSERT_UPDATE);
                            holdingDrugsGivenEntity.setLotId(mSelectedLot.getLotId());

                            holdingDrugsGivenEntities.add(holdingDrugsGivenEntity);
                        }

                        new InsertHoldingDrugsGivenList(holdingDrugsGivenEntities).execute(MedicateACowActivity.this);

                    }

                    new InsertSingleCow(cowEntity).execute(MedicateACowActivity.this);
                    new InsertDrugsGivenList(drugList).execute(MedicateACowActivity.this);
                    
                    mTagName.setText("");
                    mTagName.requestFocus();
                    mNotes.setText("");

                    mDrugsGivenCardView.setVisibility(View.GONE);

                    for(int r=0; r<mDrugLayout.getChildCount(); r++){
                        View checkBoxView = mDrugLayout.getChildAt(r);
                        if(checkBoxView instanceof CheckBox){
                            CheckBox checkBox = (CheckBox) checkBoxView;
                            checkBox.setChecked(false);
                            if(checkBox.isChecked()){
                                r++;
                                View  editText = mDrugLayout.getChildAt(r);

                                if(editText instanceof EditText){

                                    EditText textViewAmountGiven = (EditText) editText;
                                    textViewAmountGiven.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                    ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                }
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
                            if(cowEntity.isAlive() != 1){
                                mIsSearchForCowDead = true;
                            }
                        }
                        new QueryDrugsGivenByCowIdList(MedicateACowActivity.this, cowIds).execute(MedicateACowActivity.this);
                    } else {
                        mDrugsGivenCardView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mViewMedications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoreDrugsGivenLayout.getVisibility() == View.VISIBLE) {
                    mMoreDrugsGivenLayout.setVisibility(View.GONE);
                    mViewMedications.setText("View");
                } else {
                    mMoreDrugsGivenLayout.setVisibility(View.VISIBLE);
                    mViewMedications.setText("Hide");
                }
            }
        });
    }

    @Override
    public void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities) {
        mDrugList = drugEntities;
        for(int x=0; x<mDrugList.size(); x++){
            DrugEntity drugEntity = drugEntities.get(x);
            addCheckBox(mDrugLayout, drugEntity);
        }
        if (drugEntities.size() == 0) {
            mNoDrugs.setVisibility(View.VISIBLE);
        }
        mLoadDrugs.setVisibility(View.GONE);
    }

    private void addCheckBox(LinearLayout linearLayout, DrugEntity drugEntity){

        String drugName = drugEntity.getDrugName();
        String drugId = drugEntity.getDrugId();
        int defaultAmount = drugEntity.getDefaultAmount();
        String defaultAmountStr = Integer.toString(defaultAmount);

        final float scale = getResources().getDisplayMetrics().density;
        int pixels24 = (int) (24 * scale + 0.5f);
        int pixels16 = (int) (16 * scale + 0.5f);
        int pixels8 = (int) (8 * scale + 0.5f);

        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(drugName);
        checkBox.setTag(drugId);
        LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        checkBoxParams.setMargins(pixels16, pixels24, pixels16, pixels8);
        checkBox.setLayoutParams(checkBoxParams);

        EditText editText = new EditText(this);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        editTextParams.setMargins(pixels24, 0, pixels24, pixels16);
        editText.setEms(4);
        editText.setGravity(Gravity.CENTER);
        editText.setTag(drugId + "_editText");
        editText.setText(defaultAmountStr);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setLayoutParams(editTextParams);

        linearLayout.addView(checkBox);
        linearLayout.addView(editText);
    }

    @Override
    public void onCowsByLotIdLoaded(ArrayList<CowEntity> cowObjectList) {
        mCowEntities = cowObjectList;
    }

    @Override
    public void onDrugsGivenByCowIdListLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        if(drugsGivenEntities.size() != 0){
            mDrugsGivenCardView.setVisibility(View.VISIBLE);
            mViewMedications.setVisibility(View.VISIBLE);
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
            for (int r = 0; r < drugsGivenEntities.size(); r++) {
                DrugsGivenEntity drugsGivenEntity = drugsGivenEntities.get(r);
                DrugEntity drugEntity = Utility.findDrugEntity(drugsGivenEntity.getDrugId(), mDrugList);

                String amountGivenStr = Integer.toString(drugsGivenEntity.amountGiven);
                String drugName;
                if (drugEntity == null) {
                    drugName = "[drug_unavailable]";
                } else {
                    drugName = drugEntity.getDrugName();
                }

                String textToSet = amountGivenStr + "ccs of " + drugName;
                TextView textView = new TextView(MedicateACowActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(pixels16, 0, pixels16, pixels8);
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
                mViewMedications.setVisibility(View.INVISIBLE);
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
            String lotId = lotEntity.getLotId();
            lotIds.add(lotId);
        }
        new QueryMedicatedCowsByLotIds(this, lotIds).execute(this);

    }
}
