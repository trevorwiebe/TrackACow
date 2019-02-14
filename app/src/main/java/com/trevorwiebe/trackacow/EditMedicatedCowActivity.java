package com.trevorwiebe.trackacow;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.dataLoaders.DeleteCow;
import com.trevorwiebe.trackacow.dataLoaders.DeleteDrugsGivenByCowId;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingCow;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.QueryDrugsGivenByCowId;
import com.trevorwiebe.trackacow.dataLoaders.UpdateCow;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingCowEntity;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;

public class EditMedicatedCowActivity extends AppCompatActivity implements
        QueryAllDrugs.OnAllDrugsLoaded,
        QueryDrugsGivenByCowId.OnDrugsGivenByCowIdLoaded{

    private static final String TAG = "EditMedicatedCowActivit";

    private DatabaseReference mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private CowEntity mCowEntity;
    private int isAlive;
    private DatePickerDialog.OnDateSetListener mStartDatePicker;
    private Calendar mCalendar = Calendar.getInstance();
    private ArrayList<DrugEntity> mDrugList = new ArrayList<>();

    private CardView mCowIsDead;
    private TextInputLayout mEditDateLayout;
    private TextInputEditText mEditTagNumber;
    private TextInputEditText mEditDate;
    private TextInputEditText mEditNotes;
    private LinearLayout mDrugsGiven;
    private Button mUpdateBtn;
    private Button mDeleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medicated_cow);

        mCowEntity = getIntent().getParcelableExtra("cow");

        isAlive = mCowEntity.isAlive();

        String strTagNumber = Integer.toString(mCowEntity.getTagNumber());
        setTitle("Cow " + strTagNumber);

        mCowIsDead = findViewById(R.id.cow_is_dead_card);
        mUpdateBtn = findViewById(R.id.update_medicated_cow);
        mDeleteBtn = findViewById(R.id.delete_medicated_cow);
        mEditDateLayout = findViewById(R.id.edit_date_layout);
        mEditTagNumber = findViewById(R.id.edit_tag_number);
        mDrugsGiven = findViewById(R.id.drug_given_layout);
        mEditDate = findViewById(R.id.edit_date);
        mEditNotes = findViewById(R.id.edit_notes);
        String tagNumber = Integer.toString(mCowEntity.getTagNumber());
        String date = Utility.convertMillisToDate(mCowEntity.getDate());
        String notes = mCowEntity.getNotes();
        mEditTagNumber.setText(tagNumber);
        mEditDate.setText(date);
        mEditNotes.setText(notes);

        if(isAlive == 1){
            mCowIsDead.setVisibility(View.GONE);
            mEditDateLayout.setVisibility(View.GONE);
            new QueryAllDrugs(this).execute(this);
        }else{
            mCowIsDead.setVisibility(View.VISIBLE);
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

                int tagNumber = Integer.parseInt(mEditTagNumber.getText().toString());
                String notes = mEditNotes.getText().toString();

                mCowEntity.setTagNumber(tagNumber);
                mCowEntity.setNotes(notes);

                if(Utility.haveNetworkConnection(EditMedicatedCowActivity.this)) {
                    mBaseRef.child(CowEntity.COW).child(mCowEntity.getCowId()).setValue(mCowEntity);
                }else{
                    Utility.setNewDataToUpload(EditMedicatedCowActivity.this, true);

                    HoldingCowEntity holdingCowEntity = new HoldingCowEntity();
                    holdingCowEntity.setWhatHappened(Utility.INSERT_UPDATE);
                    holdingCowEntity.setTagNumber(mCowEntity.getTagNumber());
                    holdingCowEntity.setPenId(mCowEntity.getPenId());
                    holdingCowEntity.setNotes(mCowEntity.getNotes());
                    holdingCowEntity.setIsAlive(mCowEntity.isAlive());
                    holdingCowEntity.setDate(mCowEntity.getDate());
                    holdingCowEntity.setCowId(mCowEntity.getCowId());

                    new InsertHoldingCow(holdingCowEntity).execute(EditMedicatedCowActivity.this);

                }

                new UpdateCow(mCowEntity.getCowId(), mCowEntity.getTagNumber(), mCowEntity.getNotes()).execute(EditMedicatedCowActivity.this);

                finish();
            }
        });

        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utility.haveNetworkConnection(EditMedicatedCowActivity.this)) {
                    mBaseRef.child(CowEntity.COW).child(mCowEntity.getCowId()).removeValue();
                    // TODO: 2/9/2019 delete all the drugs given to this cow too
                }else{
                    Utility.setNewDataToUpload(EditMedicatedCowActivity.this, true);

                    HoldingCowEntity holdingCowEntity = new HoldingCowEntity();
                    holdingCowEntity.setWhatHappened(Utility.DELETE);
                    holdingCowEntity.setTagNumber(mCowEntity.getTagNumber());
                    holdingCowEntity.setPenId(mCowEntity.getPenId());
                    holdingCowEntity.setNotes(mCowEntity.getNotes());
                    holdingCowEntity.setIsAlive(mCowEntity.isAlive());
                    holdingCowEntity.setDate(mCowEntity.getDate());
                    holdingCowEntity.setCowId(mCowEntity.getCowId());

                    new InsertHoldingCow(holdingCowEntity).execute(EditMedicatedCowActivity.this);

                    // TODO: 2/9/2019 mark all the drugs given to this cow to be deleted
                }

                new DeleteCow(mCowEntity).execute(EditMedicatedCowActivity.this);
                new DeleteDrugsGivenByCowId(mCowEntity.getCowId()).execute(EditMedicatedCowActivity.this);

                finish();
            }
        });

    }

    @Override
    public void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities) {
        mDrugList = drugEntities;
        new QueryDrugsGivenByCowId(this, mCowEntity.getCowId()).execute(this);
    }

    @Override
    public void onDrugsLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        setDrugGivenLayout(drugsGivenEntities);
    }


    private void setDrugGivenLayout(ArrayList<DrugsGivenEntity> drugsGivenEntities){
        String holdingDate = "";
        for (int t = 0; t < drugsGivenEntities.size(); t++) {
            DrugsGivenEntity drugsGivenEntity = drugsGivenEntities.get(t);
            int amountGiven = drugsGivenEntity.getAmountGiven();
            String drugId = drugsGivenEntity.getDrugId();
            long date = drugsGivenEntity.getDate();

            final float scale = getResources().getDisplayMetrics().density;
            int pixels24 = (int) (24 * scale + 0.5f);
            int pixels16 = (int) (16 * scale + 0.5f);
            int pixels8 = (int) (8 * scale + 0.5f);

            String nextDate = Utility.convertMillisToDate(date);
            if(!nextDate.equals(holdingDate)){
                TextView textView = new TextView(EditMedicatedCowActivity.this);
                LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                textViewParams.setMargins(pixels16, pixels24, pixels16, 0);
                textView.setTextColor(getResources().getColor(android.R.color.black));
                textView.setLayoutParams(textViewParams);
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setText("On: " + nextDate);
                mDrugsGiven.addView(textView);
            }

            holdingDate = nextDate;

            DrugEntity drugEntity = Utility.findDrugEntity(drugId, mDrugList);
            String textToSet = Integer.toString(amountGiven) + " ccs of " + drugEntity.getDrugName();

            TextView textView = new TextView(EditMedicatedCowActivity.this);
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            textViewParams.setMargins(pixels16, pixels8, pixels16, 0);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            textView.setLayoutParams(textViewParams);

            textView.setText(textToSet);

            mDrugsGiven.addView(textView);
        }
    }
}
