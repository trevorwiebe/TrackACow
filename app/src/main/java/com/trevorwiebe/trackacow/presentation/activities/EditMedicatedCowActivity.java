package com.trevorwiebe.trackacow.presentation.activities;

import android.app.DatePickerDialog;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.cardview.widget.CardView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheCowEntity;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugsGivenEntity;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.cow.DeleteCow;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.drugsGiven.DeleteDrugsGivenByCowId;
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingCow.InsertHoldingCow;
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingDrugsGiven.InsertHoldingDrugsGivenList;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.drug.QueryAllDrugs;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.cow.QueryCowIdByCowId;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.drugsGiven.QueryDrugsGivenByCowId;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.cow.UpdateCow;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.drugsGiven.UpdateDrugsGivenDateByCowId;
import com.trevorwiebe.trackacow.data.entities.CowEntity;
import com.trevorwiebe.trackacow.data.entities.DrugEntity;
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.domain.utils.Constants;
import com.trevorwiebe.trackacow.domain.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.ListIterator;

public class EditMedicatedCowActivity extends AppCompatActivity implements
        QueryAllDrugs.OnAllDrugsLoaded,
        QueryDrugsGivenByCowId.OnDrugsGivenByCowIdLoaded,
        QueryCowIdByCowId.OnCowByIdLoaded,
        UpdateCow.OnCowUpdated,
        UpdateDrugsGivenDateByCowId.OnDrugsGivenByCowIdUpdated {

    private static final String TAG = "EditMedicatedCowActivit";

    private CowEntity mCowEntity;
    private int isAlive;
    private DatePickerDialog.OnDateSetListener mStartDatePicker;
    private Calendar mCalendar = Calendar.getInstance();
    private ArrayList<DrugEntity> mDrugList = new ArrayList<>();
    private ArrayList<DrugsGivenEntity> mDrugsGivenList = new ArrayList<>();
    private long mOldDate;
    private long mNewDate;

    private CardView mCowIsDead;
    private TextInputEditText mEditTagNumber;
    private TextInputEditText mEditDate;
    private TextInputEditText mEditNotes;
    private LinearLayout mDrugLayout;
    private LinearLayout mDrugsGiven;
    private Button mEditDrugsGiven;
    private Button mUpdateBtn;
    private Button mDeleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medicated_cow);

        mCowIsDead = findViewById(R.id.cow_is_dead_card);
        mUpdateBtn = findViewById(R.id.update_medicated_cow);
        mDeleteBtn = findViewById(R.id.delete_medicated_cow);
        mEditDrugsGiven = findViewById(R.id.edit_drugs_given);
        mEditTagNumber = findViewById(R.id.edit_tag_number);
        mDrugLayout = findViewById(R.id.drugs_given_layout);
        mDrugsGiven = findViewById(R.id.drug_given_layout);
        mEditDate = findViewById(R.id.edit_date);
        mEditNotes = findViewById(R.id.edit_notes);

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

        mEditDrugsGiven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editDrugsIntent = new Intent(EditMedicatedCowActivity.this, EditDrugsGivenActivity.class);
                editDrugsIntent.putExtra("cowId", mCowEntity.getCowId());
                startActivity(editDrugsIntent);
            }
        });

        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int tagNumber = Integer.parseInt(mEditTagNumber.getText().toString());
                mNewDate = mCalendar.getTimeInMillis();
                String notes = mEditNotes.getText().toString();

                mCowEntity.setTagNumber(tagNumber);
                mCowEntity.setDate(mNewDate);
                mCowEntity.setNotes(notes);

                if(Utility.haveNetworkConnection(EditMedicatedCowActivity.this)) {
                    Constants.BASE_REFERENCE.child(Constants.COW).child(mCowEntity.getCowId()).setValue(mCowEntity);
                }else{
                    Utility.setNewDataToUpload(EditMedicatedCowActivity.this, true);

                    CacheCowEntity cacheCowEntity = new CacheCowEntity();
                    cacheCowEntity.setWhatHappened(Constants.INSERT_UPDATE);
                    cacheCowEntity.setTagNumber(mCowEntity.getTagNumber());
                    cacheCowEntity.setLotId(mCowEntity.getLotId());
                    cacheCowEntity.setNotes(mCowEntity.getNotes());
//                    cacheCowEntity.isAlive(mCowEntity.isAlive());
                    cacheCowEntity.setDate(mCalendar.getTimeInMillis());
                    cacheCowEntity.setCowId(mCowEntity.getCowId());

                    new InsertHoldingCow(cacheCowEntity).execute(EditMedicatedCowActivity.this);

                }

                new UpdateCow(EditMedicatedCowActivity.this, mCowEntity.getCowId(), mCalendar.getTimeInMillis(), mCowEntity.getTagNumber(), mCowEntity.getNotes()).execute(EditMedicatedCowActivity.this);
            }
        });

        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utility.haveNetworkConnection(EditMedicatedCowActivity.this)) {
                    String cowId = mCowEntity.getCowId();
                    Constants.BASE_REFERENCE.child(Constants.COW).child(cowId).removeValue();
                    Query deleteDrugsGivenQuery = Constants.BASE_REFERENCE.child(Constants.DRUGS_GIVEN).orderByChild("cowId").equalTo(cowId);
                    deleteDrugsGivenQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    Utility.setNewDataToUpload(EditMedicatedCowActivity.this, true);

                    CacheCowEntity cacheCowEntity = new CacheCowEntity();
                    cacheCowEntity.setWhatHappened(Constants.DELETE);
                    cacheCowEntity.setTagNumber(mCowEntity.getTagNumber());
                    cacheCowEntity.setLotId(mCowEntity.getLotId());
                    cacheCowEntity.setNotes(mCowEntity.getNotes());
//                    cacheCowEntity.setIsAlive(mCowEntity.isAlive());
                    cacheCowEntity.setDate(mCowEntity.getDate());
                    cacheCowEntity.setCowId(mCowEntity.getCowId());

                    new InsertHoldingCow(cacheCowEntity).execute(EditMedicatedCowActivity.this);

                    ListIterator iterator = mDrugsGivenList.listIterator();
                    ArrayList<CacheDrugsGivenEntity> holdingDrugsGivenEntities = new ArrayList<>();
                    while (iterator.hasNext()) {
                        DrugsGivenEntity drugsGivenEntity = (DrugsGivenEntity) iterator.next();
                        CacheDrugsGivenEntity cacheDrugsGivenEntity = new CacheDrugsGivenEntity(
                                0,
                                drugsGivenEntity.getDrugsGivenId(),
                                drugsGivenEntity.getDrugsGivenDrugId(),
                                drugsGivenEntity.getDrugsGivenAmountGiven(),
                                drugsGivenEntity.getDrugsGivenCowId(),
                                drugsGivenEntity.getDrugsGivenLotId(),
                                drugsGivenEntity.getDrugsGivenDate(),
                                Constants.DELETE
                        );
                        holdingDrugsGivenEntities.add(cacheDrugsGivenEntity);
                    }
                    new InsertHoldingDrugsGivenList(holdingDrugsGivenEntities).execute(EditMedicatedCowActivity.this);

                }

                new DeleteCow(mCowEntity).execute(EditMedicatedCowActivity.this);
                new DeleteDrugsGivenByCowId(mCowEntity.getCowId()).execute(EditMedicatedCowActivity.this);

                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        String cowId;

        if (getIntent().getStringExtra("cowEntityId") == null) {
            cowId = Utility.getCowId(this);
        }else{
            cowId = getIntent().getStringExtra("cowEntityId");
        }

        new QueryCowIdByCowId(this, cowId).execute(this);
    }

    @Override
    public void onCowByIdLoaded(CowEntity cowEntity) {
        mCowEntity = cowEntity;

        isAlive = mCowEntity.isAlive();

        String strTagNumber = Integer.toString(mCowEntity.getTagNumber());
        setTitle("Cow " + strTagNumber);
        mCalendar.setTimeInMillis(mCowEntity.getDate());

        mDrugsGiven.removeAllViews();
        if (isAlive == 1) {
            mCowIsDead.setVisibility(View.GONE);
            new QueryAllDrugs(this).execute(this);
        } else {
            mCowIsDead.setVisibility(View.VISIBLE);
            mDrugLayout.setVisibility(View.GONE);
        }

        mOldDate = mCowEntity.getDate();

        String tagNumber = Integer.toString(mCowEntity.getTagNumber());
        String date = Utility.convertMillisToDate(mCowEntity.getDate());
        String notes = mCowEntity.getNotes();
        mEditTagNumber.setText(tagNumber);
        mEditDate.setText(date);
        mEditNotes.setText(notes);
    }

    @Override
    public void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities) {
        mDrugList = drugEntities;
        new QueryDrugsGivenByCowId(this, 1, mCowEntity.getCowId()).execute(this);
    }

    @Override
    public void onDrugsLoadedByCowId(ArrayList<DrugsGivenEntity> drugsGivenEntities, int id) {
        if (id == 1) {
            mDrugsGivenList = drugsGivenEntities;
            setDrugGivenLayout(mDrugsGivenList);
        } else if (id == 2) {
            ArrayList<CacheDrugsGivenEntity> holdingDrugsGivenEntities = new ArrayList<>();
            for (int x = 0; x < drugsGivenEntities.size(); x++) {
                DrugsGivenEntity drugsGivenEntity = drugsGivenEntities.get(x);
                drugsGivenEntity.setDrugsGivenDate(mNewDate);

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
            new InsertHoldingDrugsGivenList(holdingDrugsGivenEntities).execute(EditMedicatedCowActivity.this);
        }
    }

    private void setDrugGivenLayout(ArrayList<DrugsGivenEntity> drugsGivenEntities){

        final float scale = getResources().getDisplayMetrics().density;
        int pixels16 = (int) (16 * scale + 0.5f);
        int pixels8 = (int) (8 * scale + 0.5f);

        if(drugsGivenEntities.size() == 0){
            TextView textView = new TextView(EditMedicatedCowActivity.this);
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            textViewParams.setMargins(pixels16, pixels8, pixels16, 0);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            textView.setLayoutParams(textViewParams);

            textView.setText("No drugs given");

            mDrugsGiven.addView(textView);
        }else {
            for (int t = 0; t < drugsGivenEntities.size(); t++) {
                DrugsGivenEntity drugsGivenEntity = drugsGivenEntities.get(t);
                int amountGiven = drugsGivenEntity.getDrugsGivenAmountGiven();
                String drugId = drugsGivenEntity.getDrugsGivenDrugId();

                DrugEntity drugEntity = Utility.findDrugEntity(drugId, mDrugList);
                String drugName;
                if (drugEntity != null) {
                    drugName = drugEntity.getDrugName();
                } else {
                    drugName = "[drug_unavailable]";
                }

                String textToSet = amountGiven + " units of " + drugName;

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

    @Override
    public void onDrugsGivenByCowIdUpdated() {
        finish();
    }

    @Override
    public void onCowUpdated() {
        if (mOldDate != mNewDate) {
            if (Utility.haveNetworkConnection(EditMedicatedCowActivity.this)) {

                Query drugsToUpdateQuery = Constants.BASE_REFERENCE.child("drugsGiven").orderByChild("cowId").equalTo(mCowEntity.getCowId());
                drugsToUpdateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DrugsGivenEntity drugsGivenEntity = snapshot.getValue(DrugsGivenEntity.class);
                            if (drugsGivenEntity != null) {
                                drugsGivenEntity.setDrugsGivenDate(mNewDate);
                                Constants.BASE_REFERENCE.child("drugsGiven").child(drugsGivenEntity.getDrugsGivenId()).setValue(drugsGivenEntity);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            } else {

                new QueryDrugsGivenByCowId(EditMedicatedCowActivity.this, 2, mCowEntity.getCowId()).execute(EditMedicatedCowActivity.this);
            }

            new UpdateDrugsGivenDateByCowId(EditMedicatedCowActivity.this, mCowEntity.getCowId(), mNewDate).execute(EditMedicatedCowActivity.this);

        } else {
            finish();
        }
    }
}
