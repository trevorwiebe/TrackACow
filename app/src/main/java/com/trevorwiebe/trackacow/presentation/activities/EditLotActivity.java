package com.trevorwiebe.trackacow.presentation.activities;

import android.app.DatePickerDialog;
import android.content.Intent;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingLot.InsertHoldingLot;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.QueryLotByLotId;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.UpdateLot;
import com.trevorwiebe.trackacow.data.local.entities.LotEntity;
import com.trevorwiebe.trackacow.data.local.holdingUpdateEntities.HoldingLotEntity;
import com.trevorwiebe.trackacow.domain.utils.Constants;
import com.trevorwiebe.trackacow.domain.utils.Utility;

import java.util.Calendar;

public class EditLotActivity extends AppCompatActivity implements
        QueryLotByLotId.OnLotByLotIdLoaded {

    private LotEntity mSelectedLot;
    private Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener mDatePicker;

    private TextInputEditText mLotDescription;
    private TextInputEditText mCustomerName;
    private TextInputEditText mDate;
    private TextInputEditText mNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lot);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String lotId = getIntent().getStringExtra("lotId");

        new QueryLotByLotId(lotId, this).execute(this);

        mLotDescription = findViewById(R.id.edit_lot_description);
        mCustomerName = findViewById(R.id.edit_customer_name);
        mDate = findViewById(R.id.edit_lot_date);
        mNotes = findViewById(R.id.edit_notes);
        Button updateBtn = findViewById(R.id.update_pen_btn);
        Button cancel = findViewById(R.id.cancel_update_pen_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditLotActivity.this,
                        mDatePicker,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        mDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mDate.setText(Utility.convertMillisToDate(mCalendar.getTimeInMillis()));
            }
        };

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomerName.length() == 0) {
                    Snackbar.make(v, "Please fill the blanks", Snackbar.LENGTH_LONG).show();
                }else{

                    String lotDescription = mLotDescription.getText().toString();
                    String customerName = mCustomerName.getText().toString();
                    long date = mCalendar.getTimeInMillis();
                    String notes = mNotes.getText().toString();

                    mSelectedLot.setLotName(lotDescription);
                    mSelectedLot.setCustomerName(customerName);
                    mSelectedLot.setDate(date);
                    mSelectedLot.setNotes(notes);

                    if (Utility.haveNetworkConnection(EditLotActivity.this)) {
                        DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        baseRef.child(LotEntity.LOT).child(mSelectedLot.getLotId()).setValue(mSelectedLot);
                    }else{

                        Utility.setNewDataToUpload(EditLotActivity.this, true);

                        HoldingLotEntity holdingLotEntity = new HoldingLotEntity(mSelectedLot, Constants.INSERT_UPDATE);

                        new InsertHoldingLot(holdingLotEntity).execute(EditLotActivity.this);
                    }

                    new UpdateLot(mSelectedLot).execute(EditLotActivity.this);

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    public void onLotByLotIdLoaded(LotEntity lotEntity) {
        mSelectedLot = lotEntity;

        long longDate = mSelectedLot.getDate();
        mCalendar.setTimeInMillis(longDate);

        String date = Utility.convertMillisToDate(longDate);

        mLotDescription.setText(mSelectedLot.getLotName());
        mCustomerName.setText(mSelectedLot.getCustomerName());
        mDate.setText(date);
        mNotes.setText(mSelectedLot.getNotes());

    }

}
