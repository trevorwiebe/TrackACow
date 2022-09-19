package com.trevorwiebe.trackacow.presentation.activities;

import android.app.DatePickerDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingCow.InsertHoldingCow;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.cow.InsertSingleCow;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.cow.QueryDeadCowsByLotIds;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.QueryLotsByPenId;
import com.trevorwiebe.trackacow.data.db.entities.CowEntity;
import com.trevorwiebe.trackacow.data.db.entities.LotEntity;
import com.trevorwiebe.trackacow.data.db.holdingUpdateEntities.HoldingCowEntity;
import com.trevorwiebe.trackacow.domain.utils.Constants;
import com.trevorwiebe.trackacow.domain.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;

public class MarkACowDeadActivity extends AppCompatActivity implements
        QueryDeadCowsByLotIds.OnDeadCowsLoaded,
        QueryLotsByPenId.OnLotsByPenIdLoaded {

    private CardView mDeadCowCard;
    private TextInputEditText mTagNumber;
    private TextInputEditText mDate;
    private TextInputEditText mNotes;
    private TextInputLayout mAddNotesLayout;
    private Button mAddNotes;

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
        mAddNotesLayout = findViewById(R.id.dead_notes_layout);
        mAddNotes = findViewById(R.id.dead_add_notes_btn);

        mTagNumber.setOnEditorActionListener(doneListener);
        mNotes.setOnEditorActionListener(doneListener);

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

        mAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddNotesLayout.setVisibility(View.VISIBLE);
                mAddNotes.setVisibility(View.GONE);
            }
        });
    }

    TextView.OnEditorActionListener doneListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                markAsDead(null);
            }
            return false;
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    public void markAsDead(View view) {
        if(mTagNumber.length() == 0 || mDate.length() == 0 ){
            mTagNumber.requestFocus();
            mTagNumber.setError("Please fill the blank");
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
            HoldingCowEntity holdingCowEntity = new HoldingCowEntity(cowEntity, Constants.INSERT_UPDATE);
            new InsertHoldingCow(holdingCowEntity).execute(MarkACowDeadActivity.this);
        }

        new InsertSingleCow(cowEntity).execute(this);

        mDeadCowList.add(cowEntity);

        mAddNotes.setVisibility(View.VISIBLE);
        mAddNotesLayout.setVisibility(View.GONE);

        mTagNumber.setText("");
        mNotes.setText("");
        mCalendar = Calendar.getInstance();
        mDate.setText(Utility.convertMillisToDate(mCalendar.getTimeInMillis()));
        mTagNumber.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
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
