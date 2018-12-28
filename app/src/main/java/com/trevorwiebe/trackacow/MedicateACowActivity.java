package com.trevorwiebe.trackacow;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.trevorwiebe.trackacow.objects.CowDrugObject;
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
    private Button mSaveCow;

    private PenObject mSelectedPen;
    private DatabaseReference mBaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicate_a_cow);

        mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mTagName = findViewById(R.id.tag_number);
        mNotes = findViewById(R.id.notes);
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
                    }
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
                if(mTagName.length() == 0){
                    Snackbar.make(view, "Please set the tag number", Snackbar.LENGTH_LONG).show();
                }else if(5==6){

                }else{

                    ArrayList<CowDrugObject> drugList = new ArrayList<>();

                    for(int r=0; r<mDrugLayout.getChildCount(); r++){
                        CowDrugObject cowDrugObject = new CowDrugObject();
                        View checkBoxView = mDrugLayout.getChildAt(r);
                        if(checkBoxView instanceof CheckBox){
                            CheckBox checkBox = (CheckBox) checkBoxView;
                            cowDrugObject.setDrugId(checkBox.getTag().toString());
                            if(checkBox.isChecked()){
                                r++;
                                View  textView = mDrugLayout.getChildAt(r);

                                if(textView instanceof TextView){

                                    TextView textViewAmountGiven = (TextView) textView;
                                    int amountGiven = Integer.parseInt(textViewAmountGiven.getText().toString());
                                    cowDrugObject.setAmountGiven(amountGiven);

                                    drugList.add(cowDrugObject);

                                }
                            }
                            r++;
                        }
                    }

                    int tagNumber = Integer.parseInt(mTagName.getText().toString());
                    String notes = mNotes.getText().toString();

                    DatabaseReference pushRef = mBaseRef.child(CowObject.COW).push();
                    String id = pushRef.getKey();

                    CowObject cowObject = new CowObject(tagNumber, id, mSelectedPen.getPenId(), notes, System.currentTimeMillis(), drugList);

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
            String drugName = compoundButton.getTag().toString();
            Log.d(TAG, "onCheckedChanged: " + drugName);
            SeekBar seekBar = mDrugLayout.findViewWithTag(drugName + "_seekBar");
            TextView textView = mDrugLayout.findViewWithTag(drugName + "_textView");
            if(isChecked){
                seekBar.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
            }else{
                seekBar.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            String[] tagString = seekBar.getTag().toString().split("_");
            String baseId = tagString[0];
            String id = baseId + "_textView";
            TextView textView = mDrugLayout.findViewWithTag(id);
            String index = Integer.toString(i);
            textView.setText(index);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void addCheckBox(LinearLayout linearLayout, DrugObject drugObject){

        String drugName = drugObject.getDrugName();
        String drugId = drugObject.getDrugId();
        int minAmount = drugObject.getMinAmount();
        int maxAmount = drugObject.getMaxAmount();
        int defaultAmount = drugObject.getDefaultAmount();

        final float scale = getResources().getDisplayMetrics().density;
        int pixels36 = (int) (36 * scale + 0.5f);
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
        checkBox.setOnCheckedChangeListener(checkBoxListener);


        TextView textView = new TextView(this);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textViewParams.setMargins(pixels16, pixels8, pixels16, pixels8);
        textView.setLayoutParams(textViewParams);
        textView.setVisibility(View.GONE);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTag(drugId + "_textView");
        textView.setText(Integer.toString(defaultAmount));


        SeekBar seekBar = new SeekBar(this);
        seekBar.setTag(drugId + "_seekBar");
        LinearLayout.LayoutParams seekBarParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        seekBarParams.setMargins(pixels16, pixels8, pixels16, pixels16);
        seekBar.setLayoutParams(seekBarParams);
        seekBar.setVisibility(View.GONE);
        seekBar.setProgress(defaultAmount);
        seekBar.setMax(maxAmount);
        seekBar.setOnSeekBarChangeListener(seekBarListener);

        linearLayout.addView(checkBox);
        linearLayout.addView(textView);
        linearLayout.addView(seekBar);
    }
}
