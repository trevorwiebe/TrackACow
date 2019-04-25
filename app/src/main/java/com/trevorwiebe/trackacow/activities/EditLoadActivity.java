package com.trevorwiebe.trackacow.activities;

import android.app.DatePickerDialog;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingLoad;
import com.trevorwiebe.trackacow.dataLoaders.QueryLoadsByLoadId;
import com.trevorwiebe.trackacow.dataLoaders.UpdateLoadEntity;
import com.trevorwiebe.trackacow.db.entities.LoadEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLoadEntity;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.Utility;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

import javax.xml.transform.Result;

public class EditLoadActivity extends AppCompatActivity implements
        QueryLoadsByLoadId.OnLoadsByLoadIdLoaded {

    private static final String TAG = "EditLoadActivity";

    private Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener mDatePicker;
    private NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
    private LoadEntity mSelectedLoadEntity;

    private TextInputEditText mHeadCount;
    private TextInputEditText mDate;
    private TextInputEditText mMemo;
    private Button mUpdateBtn;
    private Button mDeleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_load);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mHeadCount = findViewById(R.id.edit_load_head_count);
        mDate = findViewById(R.id.edit_load_date);
        mMemo = findViewById(R.id.edit_load_memo);
        mUpdateBtn = findViewById(R.id.edit_load_of_cattle);
        mDeleteBtn = findViewById(R.id.delete_load_of_cattle);

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditLoadActivity.this,
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

        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mHeadCount.length() == 0 || mDate.length() == 0) {
                    Snackbar.make(v, "Please fill the blanks", Snackbar.LENGTH_LONG).show();
                } else {

                    int headCount = Integer.parseInt(mHeadCount.getText().toString());
                    long date = mCalendar.getTimeInMillis();
                    String memo = mMemo.getText().toString();

                    mSelectedLoadEntity.setNumberOfHead(headCount);
                    mSelectedLoadEntity.setDate(date);
                    mSelectedLoadEntity.setDescription(memo);

                    if (Utility.haveNetworkConnection(EditLoadActivity.this)) {
                        Constants.BASE_REFERENCE.child(LoadEntity.LOAD).child(mSelectedLoadEntity.getLoadId()).setValue(mSelectedLoadEntity);
                    } else {
                        HoldingLoadEntity holdingLoadEntity = new HoldingLoadEntity(mSelectedLoadEntity, Constants.INSERT_UPDATE);
                        new InsertHoldingLoad(holdingLoadEntity).execute(EditLoadActivity.this);
                    }

                    new UpdateLoadEntity(mSelectedLoadEntity).execute(EditLoadActivity.this);

                    setResult(RESULT_OK);
                    finish();

                }
            }
        });

        String loadId = getIntent().getStringExtra("loadId");

        new QueryLoadsByLoadId(loadId, this).execute(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return false;
    }

    @Override
    public void onLoadsByLoadIdLoaded(LoadEntity loadEntity) {

        mSelectedLoadEntity = loadEntity;

        long longDate = loadEntity.getDate();

        String headCountStr = numberFormat.format(loadEntity.getNumberOfHead());
        String date = Utility.convertMillisToDate(longDate);
        String memo = loadEntity.getDescription();

        mCalendar.setTimeInMillis(longDate);

        mHeadCount.setText(headCountStr);
        mDate.setText(date);
        mMemo.setText(memo);

    }
}
