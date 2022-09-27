package com.trevorwiebe.trackacow.presentation.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.load.DeleteLoadByLoadId;
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingLoad.InsertHoldingLoad;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.load.QueryLoadsByLoadId;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.load.UpdateLoadEntity;
import com.trevorwiebe.trackacow.data.entities.LoadEntity;
import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingLoadEntity;
import com.trevorwiebe.trackacow.domain.utils.Constants;
import com.trevorwiebe.trackacow.domain.utils.Utility;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

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

                    int headCount = 0;
                    try {
                        headCount = numberFormat.parse(mHeadCount.getText().toString()).intValue();
                    } catch (ParseException e) {
                        Log.e(TAG, "onClick: ", e);
                    }

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

        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder deleteCattle = new AlertDialog.Builder(EditLoadActivity.this);
                deleteCattle.setTitle("Are you sure?");
                deleteCattle.setMessage("You are about to delete a group of cattle.  You can not undo this action.");
                deleteCattle.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Utility.haveNetworkConnection(EditLoadActivity.this)) {
                            Constants.BASE_REFERENCE.child(LoadEntity.LOAD).child(mSelectedLoadEntity.getLoadId()).removeValue();
                        } else {
                            HoldingLoadEntity holdingLoadEntity = new HoldingLoadEntity(mSelectedLoadEntity, Constants.DELETE);
                            new InsertHoldingLoad(holdingLoadEntity).execute(EditLoadActivity.this);
                        }
                        new DeleteLoadByLoadId(mSelectedLoadEntity.getLoadId()).execute(EditLoadActivity.this);

                        setResult(RESULT_OK);
                        finish();
                    }
                });
                deleteCattle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                deleteCattle.show();
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
