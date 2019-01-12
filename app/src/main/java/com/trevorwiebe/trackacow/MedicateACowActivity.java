package com.trevorwiebe.trackacow;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.objects.DrugsGivenObject;
import com.trevorwiebe.trackacow.objects.CowObject;
import com.trevorwiebe.trackacow.objects.DrugObject;
import com.trevorwiebe.trackacow.objects.PenObject;

import java.util.ArrayList;

public class MedicateACowActivity extends AppCompatActivity {

    private static final String TAG = "MedicateACowActivity";

    private TextInputEditText mTagName;
    private TextInputEditText mNotes;
    private LinearLayout mDrugLayout;
    private ProgressBar mLoadDrugs;
    private TextView mNoDrugs;
    private Button mSaveCow;

    private ArrayList<DrugObject> mDrugList = new ArrayList<>();
    private PenObject mSelectedPen;
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

        DatabaseReference drugRef = mBaseRef.child(DrugObject.DRUG_OBJECT);
        drugRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    DrugObject drugObject = snapshot.getValue(DrugObject.class);
                    if(drugObject != null){
                        addCheckBox(mDrugLayout, drugObject);
                        mDrugList.add(drugObject);
                    }
                }
                if(mDrugList.size() ==  0){
                    mNoDrugs.setVisibility(View.VISIBLE);
                }
                mLoadDrugs.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSaveCow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDrugList.size() == 0){
                    Snackbar.make(view, "Please add a drug first.", Snackbar.LENGTH_LONG).show();
                }else if(mTagName.length() == 0){
                    Snackbar.make(view, "Please set the tag number", Snackbar.LENGTH_LONG).show();
                }else{

                    ArrayList<DrugsGivenObject> drugList = new ArrayList<>();

                    for(int r=0; r<mDrugLayout.getChildCount(); r++){
                        DrugsGivenObject drugsGivenObject = new DrugsGivenObject();
                        View checkBoxView = mDrugLayout.getChildAt(r);
                        if(checkBoxView instanceof CheckBox){
                            CheckBox checkBox = (CheckBox) checkBoxView;
                            drugsGivenObject.setDrugId(checkBox.getTag().toString());
                            if(checkBox.isChecked()){
                                r++;
                                View  editText = mDrugLayout.getChildAt(r);

                                if(editText instanceof EditText){

                                    EditText textViewAmountGiven = (EditText) editText;
                                    int amountGiven = Integer.parseInt(textViewAmountGiven.getText().toString());
                                    drugsGivenObject.setAmountGiven(amountGiven);

                                    long time = System.currentTimeMillis();
                                    drugsGivenObject.setDate(time);

                                    drugList.add(drugsGivenObject);

                                }
                            }
                            r++;
                        }
                    }

                    int tagNumber = Integer.parseInt(mTagName.getText().toString());
                    String notes = mNotes.getText().toString();

                    DatabaseReference pushRef = mBaseRef.child(CowObject.COW).push();
                    String id = pushRef.getKey();

                    CowObject cowObject = new CowObject(tagNumber, id, mSelectedPen.getPenId(), notes, drugList);

                    pushRef.setValue(cowObject);

                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        });

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

    private void addCheckBox(LinearLayout linearLayout, DrugObject drugObject){

        String drugName = drugObject.getDrugName();
        String drugId = drugObject.getDrugId();
        int defaultAmount = drugObject.getDefaultAmount();
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
        editText.setLayoutParams(editTextParams);

        linearLayout.addView(checkBox);
        linearLayout.addView(editText);

    }
}
