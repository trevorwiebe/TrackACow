package com.trevorwiebe.trackacow;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.dataLoaders.InsertDrugsGivenList;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.InsertSingleCow;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;

public class MedicateACowActivity extends AppCompatActivity implements QueryAllDrugs.OnAllDrugsLoaded {

    private static final String TAG = "MedicateACowActivity";

    private TextInputEditText mTagName;
    private TextInputEditText mNotes;
    private LinearLayout mDrugLayout;
    private ProgressBar mLoadDrugs;
    private TextView mNoDrugs;
    private Button mSaveCow;

    private ArrayList<DrugEntity> mDrugList = new ArrayList<>();
    private PenEntity mSelectedPen;
    private DatabaseReference mBaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicate_a_cow);

        mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mTagName = findViewById(R.id.tag_number);
        mNotes = findViewById(R.id.notes);
        mNoDrugs = findViewById(R.id.no_drugs_added);
        mDrugLayout = findViewById(R.id.drug_layout);
        mLoadDrugs = findViewById(R.id.medicate_loading_drugs);
        mSaveCow = findViewById(R.id.save_medicated_cow);

        mSelectedPen = getIntent().getParcelableExtra("penObject");

        if(Utility.haveNetworkConnection(this)) {
            DatabaseReference drugRef = mBaseRef.child(DrugEntity.DRUG_OBJECT);
            drugRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DrugEntity drugEntity = snapshot.getValue(DrugEntity.class);
                        if (drugEntity != null) {
                            addCheckBox(mDrugLayout, drugEntity);
                            mDrugList.add(drugEntity);
                        }
                    }
                    if (mDrugList.size() == 0) {
                        mNoDrugs.setVisibility(View.VISIBLE);
                    }
                    mLoadDrugs.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            new QueryAllDrugs(this).execute(this);
        }

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

                                    long time = System.currentTimeMillis();
                                    drugsGivenEntity.setDate(time);

                                    if(Utility.haveNetworkConnection(MedicateACowActivity.this)){
                                        DatabaseReference drugsGivenPushRef = drugsGivenRef.push();
                                        String drugsGivenKey = drugsGivenPushRef.getKey();
                                        drugsGivenEntity.setDrugId(drugsGivenKey);
                                        drugsGivenPushRef.setValue(drugsGivenEntity);
                                    }

                                    drugList.add(drugsGivenEntity);

                                }
                            }
                        }
                    }

                    if(drugList.size() == 0){
                        Snackbar.make(view, "You must select at least one drug before saving", Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    int tagNumber = Integer.parseInt(mTagName.getText().toString());
                    String notes = mNotes.getText().toString();

                    CowEntity cowObject = new CowEntity(true, cowId, tagNumber, System.currentTimeMillis(), notes, mSelectedPen.getPenId());

                    if(Utility.haveNetworkConnection(MedicateACowActivity.this)){
                        pushRef.setValue(cowObject);
                    }

                    new InsertSingleCow(cowObject).execute(MedicateACowActivity.this);
                    new InsertDrugsGivenList(drugList).execute(MedicateACowActivity.this);

                    Snackbar.make(view, "Save successfully", Snackbar.LENGTH_SHORT).show();

                    Utility.vibrate(MedicateACowActivity.this, 50);

                    mTagName.setText("");
                    mTagName.requestFocus();
                    mNotes.setText("");

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

    private CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            String drugId = compoundButton.getTag().toString();
            EditText editText = mDrugLayout.findViewWithTag(drugId + "_editText");
            if(isChecked){
                editText.setVisibility(View.VISIBLE);
            }else{
                editText.setVisibility(View.GONE);
            }
        }
    };

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
        checkBox.setOnCheckedChangeListener(checkBoxListener);
        checkBox.setLayoutParams(checkBoxParams);

        EditText editText = new EditText(this);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        editTextParams.setMargins(pixels24, 0, pixels24, pixels16);
        editText.setEms(5);
        editText.setGravity(Gravity.CENTER);
        editText.setTag(drugId + "_editText");
        editText.setText(defaultAmountStr);
        editText.setVisibility(View.GONE);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setLayoutParams(editTextParams);

        linearLayout.addView(checkBox);
        linearLayout.addView(editText);

    }
}
