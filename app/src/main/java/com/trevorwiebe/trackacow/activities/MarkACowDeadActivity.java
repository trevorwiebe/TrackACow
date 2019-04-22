package com.trevorwiebe.trackacow.activities;

import android.app.DatePickerDialog;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingCow;
import com.trevorwiebe.trackacow.dataLoaders.InsertSingleCow;
import com.trevorwiebe.trackacow.dataLoaders.QueryDeadCowsByLotIds;
import com.trevorwiebe.trackacow.dataLoaders.QueryLotsByPenId;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingCowEntity;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;

public class MarkACowDeadActivity extends AppCompatActivity implements
        QueryDeadCowsByLotIds.OnDeadCowsLoaded,
        QueryLotsByPenId.OnLotsByPenIdLoaded {

    private CardView mDeadCowCard;
    private TextInputEditText mTagNumber;
    private TextInputEditText mDate;
    private TextInputEditText mNotes;

    private DatePickerDialog.OnDateSetListener mStartDatePicker;
    private Calendar mCalendar = Calendar.getInstance();
    private LotEntity mSelectedLot;
    private DatabaseReference mBaseRef;
    private ArrayList<CowEntity> mDeadCowList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_a_cow_dead);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String penId = getIntent().getStringExtra("penId");

        new QueryLotsByPenId(penId, this).execute(this);

        mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mDeadCowCard = findViewById(R.id.cow_is_dead_already_card);
        mTagNumber = findViewById(R.id.deads_tag_number);
        mDate = findViewById(R.id.deads_date);
        mNotes = findViewById(R.id.deads_notes);

        mDate.setText(Utility.convertMillisToDate(mCalendar.getTimeInMillis()));
        mDate.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                new DatePickerDialog(MarkACowDeadActivity.this,
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
                mDate.setText(Utility.convertMillisToDate(mCalendar.getTimeInMillis()));
            }
        };

        mTagNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    int tagNumber = Integer.parseInt(s.toString());
                    CowEntity cowEntity = findCowEntity(tagNumber);
                    if (cowEntity != null) {
                        mDeadCowCard.setVisibility(View.VISIBLE);
                    } else {
                        mDeadCowCard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    public void markAsDead(View view) {
        if(mTagNumber.length() == 0 || mDate.length() == 0 ){
            Snackbar.make(view, "Please fill the blanks", Snackbar.LENGTH_LONG).show();
            return;
        }
        int tagNumber = Integer.parseInt(mTagNumber.getText().toString());
        String notes = mNotes.getText().toString();

        DatabaseReference pushRef = mBaseRef.child(CowEntity.COW).push();

        CowEntity cowEntity = new CowEntity(0, pushRef.getKey(), tagNumber, mCalendar.getTimeInMillis(), notes, mSelectedLot.getLotId());

        if(Utility.haveNetworkConnection(this)){
            pushRef.setValue(cowEntity);
        }else{

            Utility.setNewDataToUpload(MarkACowDeadActivity.this, true);

            HoldingCowEntity holdingCowEntity = new HoldingCowEntity();
            holdingCowEntity.setWhatHappened(Constants.INSERT_UPDATE);
            holdingCowEntity.setTagNumber(cowEntity.getTagNumber());
            holdingCowEntity.setLotId(cowEntity.getLotId());
            holdingCowEntity.setNotes(cowEntity.getNotes());
            holdingCowEntity.setIsAlive(cowEntity.getIsAlive());
            holdingCowEntity.setDate(cowEntity.getDate());
            holdingCowEntity.setCowId(cowEntity.getCowId());

            new InsertHoldingCow(holdingCowEntity).execute(MarkACowDeadActivity.this);

        }

        new InsertSingleCow(cowEntity).execute(this);

        mDeadCowList.add(cowEntity);

        mTagNumber.setText("");
        mNotes.setText("");
        mCalendar = Calendar.getInstance();
        mDate.setText(Utility.convertMillisToDate(mCalendar.getTimeInMillis()));
        mTagNumber.requestFocus();
    }

    @Override
    public void onDeadCowsLoaded(ArrayList<CowEntity> cowEntities) {
        mDeadCowList = cowEntities;
    }

    private CowEntity findCowEntity(int tagNumber) {
        for (int v = 0; v < mDeadCowList.size(); v++) {
            CowEntity cowEntity = mDeadCowList.get(v);
            if (cowEntity.getTagNumber() == tagNumber) {
                return cowEntity;
            }
        }
        return null;
    }

    @Override
    public void onLotsByPenIdLoaded(ArrayList<LotEntity> lotEntities) {
        mSelectedLot = lotEntities.get(0);
        ArrayList<String> lotIds = new ArrayList<>();
        for (int e = 0; e < lotEntities.size(); e++) {
            LotEntity lotEntity = lotEntities.get(e);
            String lotId = lotEntity.getLotId();
            lotIds.add(lotId);
        }

        new QueryDeadCowsByLotIds(MarkACowDeadActivity.this, lotIds).execute(MarkACowDeadActivity.this);
    }
}
