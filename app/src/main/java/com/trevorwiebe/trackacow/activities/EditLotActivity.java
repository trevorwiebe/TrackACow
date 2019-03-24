package com.trevorwiebe.trackacow.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingLot;
import com.trevorwiebe.trackacow.dataLoaders.QueryLotByLotId;
import com.trevorwiebe.trackacow.dataLoaders.UpdateLot;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLotEntity;
import com.trevorwiebe.trackacow.utils.Utility;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditLotActivity extends AppCompatActivity implements
        QueryLotByLotId.OnLotByLotIdLoaded {

    private LotEntity mSelectedLot;
    private NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
    private Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener mDatePicker;

    private TextInputEditText mLotDescription;
    private TextInputEditText mCustomerName;
    private TextInputEditText mTotalHead;
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
        mTotalHead = findViewById(R.id.edit_total_head);
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
                if(mCustomerName.length() == 0 || mTotalHead.length() == 0){
                    Snackbar.make(v, "Please fill the blanks", Snackbar.LENGTH_LONG).show();
                }else{

                    String lotDescription = mLotDescription.getText().toString();
                    String customerName = mCustomerName.getText().toString();
                    int totalHead = Integer.parseInt(mTotalHead.getText().toString());
                    long date = mCalendar.getTimeInMillis();
                    String notes = mNotes.getText().toString();

                    mSelectedLot.setLotName(lotDescription);
                    mSelectedLot.setCustomerName(customerName);
                    mSelectedLot.setTotalHead(totalHead);
                    mSelectedLot.setDate(date);
                    mSelectedLot.setNotes(notes);

                    if (Utility.haveNetworkConnection(EditLotActivity.this)) {
                        DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        baseRef.child(LotEntity.LOT).child(mSelectedLot.getLotId()).setValue(mSelectedLot);
                    }else{

                        Utility.setNewDataToUpload(EditLotActivity.this, true);

                        HoldingLotEntity holdingLotEntity = new HoldingLotEntity(mSelectedLot, Utility.INSERT_UPDATE);

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

        String date = Utility.convertMillisToDate(mSelectedLot.getDate());

        mLotDescription.setText(mSelectedLot.getLotName());
        mCustomerName.setText(mSelectedLot.getCustomerName());
        mTotalHead.setText(formatter.format(mSelectedLot.getTotalHead()));
        mDate.setText(date);
        mNotes.setText(mSelectedLot.getNotes());

    }

}
