package com.trevorwiebe.trackacow;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.objects.CowObject;
import com.trevorwiebe.trackacow.objects.DrugObject;
import com.trevorwiebe.trackacow.objects.DrugsGivenObject;
import com.trevorwiebe.trackacow.utils.Utility;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class EditMedicatedCowActivity extends AppCompatActivity {

    private static final String TAG = "EditMedicatedCowActivit";

    private DatabaseReference mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private CowObject mCowObject;
    private boolean isAlive;
    private DatePickerDialog.OnDateSetListener mStartDatePicker;
    private Calendar mCalendar = Calendar.getInstance();
    private ArrayList<DrugObject> mDrugList = new ArrayList<>();

    private TextInputLayout mEditDateLayout;
    private TextInputEditText mEditTagNumber;
    private TextInputEditText mEditDate;
    private TextInputEditText mEditNotes;
    private LinearLayout mDrugLayout;
    private Button mUpdateBtn;
    private Button mDeleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medicated_cow);

        mCowObject = getIntent().getParcelableExtra("cow");

        isAlive = mCowObject.isAlive();

        String strTagNumber = Integer.toString(mCowObject.getCowNumber());
        setTitle("Cow " + strTagNumber);

        mUpdateBtn = findViewById(R.id.update_medicated_cow);
        mDeleteBtn = findViewById(R.id.delete_medicated_cow);
        mDrugLayout = findViewById(R.id.edit_drugs_layout);
        mEditDateLayout = findViewById(R.id.edit_date_layout);
        mEditTagNumber = findViewById(R.id.edit_tag_number);
        mEditDate = findViewById(R.id.edit_date);
        mEditNotes = findViewById(R.id.edit_notes);
        String tagNumber = Integer.toString(mCowObject.getCowNumber());
        String date = Utility.convertMillisToDate(mCowObject.getDate());
        String notes = mCowObject.getNotes();
        mEditTagNumber.setText(tagNumber);
        mEditDate.setText(date);
        mEditNotes.setText(notes);

        if(isAlive){
            mUpdateBtn.setText("Update medicated cow");
            mDeleteBtn.setText("Delete medicated cow");
            mEditDateLayout.setVisibility(View.GONE);
            DatabaseReference drugRef = mBaseRef.child(DrugObject.DRUG_OBJECT);
            drugRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        DrugObject drugObject = snapshot.getValue(DrugObject.class);
                        if(drugObject != null){
                            mDrugList.add(drugObject);
                        }
                    }
                    ArrayList<DrugsGivenObject> drugsGivenObjects = mCowObject.getmDrugList();
                    for(int t=0; t<drugsGivenObjects.size(); t++){

                        DrugsGivenObject drugsGivenObject = drugsGivenObjects.get(t);
                        int amountGiven = drugsGivenObject.getAmountGiven();
                        String drugId = drugsGivenObject.getDrugId();

                        final float scale = getResources().getDisplayMetrics().density;
                        int pixels16 = (int) (16 * scale + 0.5f);
                        int pixels8 = (int) (8 * scale + 0.5f);

                        Log.d(TAG, "onCreate: " + drugId);

                        DrugObject drugObject = findDrugObject(drugId);
                        String textToSet = Integer.toString(amountGiven) + " ccs of " + drugObject.getDrugName();

                        TextView textView = new TextView(EditMedicatedCowActivity.this);
                        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        textViewParams.setMargins(pixels16, pixels8, pixels16, pixels8);
                        textView.setTextColor(getResources().getColor(android.R.color.black));
                        textView.setLayoutParams(textViewParams);

                        textView.setText(textToSet);

                        mDrugLayout.addView(textView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{

            mUpdateBtn.setText("Update dead cow");
            mDeleteBtn.setText("Wait, This cow isn't dead");
            final float scale = getResources().getDisplayMetrics().density;
            int pixels16 = (int) (16 * scale + 0.5f);
            int pixels8 = (int) (8 * scale + 0.5f);

            TextView textView = new TextView(EditMedicatedCowActivity.this);
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            textViewParams.setMargins(pixels16, pixels8, pixels16, pixels8);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            textView.setLayoutParams(textViewParams);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setText("This cow is dead");
            textView.setTextColor(getResources().getColor(R.color.redText));

            mDrugLayout.addView(textView);
        }

        mEditDate.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                new DatePickerDialog(EditMedicatedCowActivity.this,
                        mStartDatePicker,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        mStartDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mEditDate.setText(Utility.convertMillisToDate(mCalendar.getTimeInMillis()));
            }
        };

        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBaseRef.child(CowObject.COW).child(mCowObject.getCowId()).removeValue();
                finish();
            }
        });

    }

    private DrugObject findDrugObject(String drugId){
        for(int r=0; r<mDrugList.size(); r++){
            DrugObject drugObject = mDrugList.get(r);
            if(drugObject.getDrugId().equals(drugId)){
                return drugObject;
            }
        }
        return null;
    }
}
