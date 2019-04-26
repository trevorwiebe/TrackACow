package com.trevorwiebe.trackacow.activities;

import android.app.DatePickerDialog;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.google.firebase.database.DatabaseReference;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingLoad;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingLot;
import com.trevorwiebe.trackacow.dataLoaders.InsertLoadEntity;
import com.trevorwiebe.trackacow.dataLoaders.InsertLotEntity;
import com.trevorwiebe.trackacow.db.entities.LoadEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLoadEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLotEntity;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.Calendar;

public class AddLoadOfCattleActivity extends AppCompatActivity {

    private TextInputEditText mHeadCount;
    private TextInputEditText mDate;
    private TextInputEditText mMemo;
    private Button mSaveButton;

    private Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener mDatePicker;
    private String mLotId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_load_of_cattle);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLotId = getIntent().getStringExtra("lotId");

        mHeadCount = findViewById(R.id.load_head_count);
        mDate = findViewById(R.id.load_date);
        mMemo = findViewById(R.id.load_memo);
        mSaveButton = findViewById(R.id.save_load_of_cattle);

        String date = Utility.convertMillisToDate(System.currentTimeMillis());
        mDate.setText(date);

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddLoadOfCattleActivity.this,
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

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mHeadCount.length() == 0 || mDate.length() == 0) {
                    Snackbar.make(v, "Please fill the blanks", Snackbar.LENGTH_LONG).show();
                } else {
                    DatabaseReference loadPushRef = Constants.BASE_REFERENCE.child(LoadEntity.LOAD).push();

                    int totalHead = Integer.parseInt(mHeadCount.getText().toString());

                    mCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    mCalendar.set(Calendar.MINUTE, 0);
                    mCalendar.set(Calendar.SECOND, 0);
                    mCalendar.set(Calendar.MILLISECOND, 0);

                    long longDate = mCalendar.getTimeInMillis();
                    String loadDescription = mMemo.getText().toString();
                    String loadId = loadPushRef.getKey();

                    LoadEntity loadEntity = new LoadEntity(totalHead, longDate, loadDescription, mLotId, loadId);

                    if (Utility.haveNetworkConnection(AddLoadOfCattleActivity.this)) {
                        loadPushRef.setValue(loadEntity);
                    } else {

                        Utility.setNewDataToUpload(AddLoadOfCattleActivity.this, true);

                        HoldingLoadEntity holdingLoadEntity = new HoldingLoadEntity(loadEntity, Constants.INSERT_UPDATE);
                        new InsertHoldingLoad(holdingLoadEntity).execute(AddLoadOfCattleActivity.this);
                    }

                    new InsertLoadEntity(loadEntity).execute(AddLoadOfCattleActivity.this);

                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return false;
    }
}
