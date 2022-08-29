package com.trevorwiebe.trackacow.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.TimeDrugRvAdapter;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.QueryArchivedLotsByLotId;
import com.trevorwiebe.trackacow.dataLoaders.QueryDrugsGivenByLotIdAndDateRange;
import com.trevorwiebe.trackacow.dataLoaders.QueryLotByLotId;
import com.trevorwiebe.trackacow.db.entities.ArchivedLotEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.Utility;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DrugsGivenReportActivity extends AppCompatActivity implements
        QueryAllDrugs.OnAllDrugsLoaded,
        QueryDrugsGivenByLotIdAndDateRange.OnDrugsGivenByLotIdAndDateRangeLoaded,
        QueryLotByLotId.OnLotByLotIdLoaded,
        QueryArchivedLotsByLotId.OnArchivedLotLoaded{

    private static final String TAG = "DrugsGivenReportActivit";

    private String mLotId;
    private Integer mReportType;
    private HashMap<String, String> mDrugKeyAndName = new HashMap<>();

    private Calendar mStartCalendar = Calendar.getInstance();
    private Calendar mEndCalendar = Calendar.getInstance();

    private DatePickerDialog.OnDateSetListener mStartDatePicker;
    private DatePickerDialog.OnDateSetListener mEndDatePicker;

    private TextView mNoDrugsGivenTv;
    private RecyclerView mDayRv;
    private TimeDrugRvAdapter timeDrugRvAdapter;
    private Button mStartDateButton;
    private Button mEndDateButton;
    private Button mQuickYesterday;
    private Button mQuickMonth;
    private Button mQuickAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugs_given_report);

        mStartDateButton = findViewById(R.id.start_date_btn);
        mEndDateButton = findViewById(R.id.end_date_btn);
        mQuickYesterday = findViewById(R.id.quick_yesterday);
        mQuickMonth = findViewById(R.id.quick_month);
        mQuickAll = findViewById(R.id.quick_all);

        mNoDrugsGivenTv = findViewById(R.id.no_drugs_given_report_tv);
        mDayRv = findViewById(R.id.drug_report_rv);
        mDayRv.setLayoutManager(new LinearLayoutManager(this));
        timeDrugRvAdapter = new TimeDrugRvAdapter(null);
        mDayRv.setAdapter(timeDrugRvAdapter);

        Intent intent = getIntent();
        mLotId = intent.getStringExtra("lotId");
        mReportType = intent.getIntExtra("reportType", -1);

        if(savedInstanceState != null){

            long startDate = savedInstanceState.getLong("startLong");
            long endDate = savedInstanceState.getLong("endLong");

            mStartCalendar.setTimeInMillis(startDate);
            mEndCalendar.setTimeInMillis(endDate);

        }else {

            long millisInADay = TimeUnit.DAYS.toMillis(1);

            long tomorrow_milliseconds = mStartCalendar.getTimeInMillis() + millisInADay;

            mStartCalendar.set(Calendar.HOUR, 0);
            mStartCalendar.set(Calendar.MINUTE, 0);
            mStartCalendar.set(Calendar.SECOND, 0);
            mStartCalendar.set(Calendar.MILLISECOND, 0);

            mEndCalendar.setTimeInMillis(tomorrow_milliseconds);
            mEndCalendar.set(Calendar.HOUR, 0);
            mEndCalendar.set(Calendar.MINUTE, 0);
            mEndCalendar.set(Calendar.SECOND, 0);
            mEndCalendar.set(Calendar.MILLISECOND, 0);

        }

        mStartDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(DrugsGivenReportActivity.this,
                        mStartDatePicker,
                        mStartCalendar.get(Calendar.YEAR),
                        mStartCalendar.get(Calendar.MONTH),
                        mStartCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        mStartDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mStartCalendar.set(Calendar.YEAR, year);
                mStartCalendar.set(Calendar.MONTH, month);
                mStartCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mStartDateButton.setText(Utility.convertMillisToDate(mStartCalendar.getTimeInMillis()));

                long selectedStartDate = mStartCalendar.getTimeInMillis();
                long selectedEndDate = mEndCalendar.getTimeInMillis();

                new QueryDrugsGivenByLotIdAndDateRange(DrugsGivenReportActivity.this, mLotId, selectedStartDate, selectedEndDate).execute(DrugsGivenReportActivity.this);
            }
        };

        mEndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(DrugsGivenReportActivity.this,
                        mEndDatePicker,
                        mEndCalendar.get(Calendar.YEAR),
                        mEndCalendar.get(Calendar.MONTH),
                        mEndCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        mEndDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mEndCalendar.set(Calendar.YEAR, year);
                mEndCalendar.set(Calendar.MONTH, month);
                mEndCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mEndDateButton.setText(Utility.convertMillisToDate(mEndCalendar.getTimeInMillis()));

                long selectedStartDate = mStartCalendar.getTimeInMillis();
                long selectedEndDate = mEndCalendar.getTimeInMillis();

                new QueryDrugsGivenByLotIdAndDateRange(DrugsGivenReportActivity.this, mLotId, selectedStartDate, selectedEndDate).execute(DrugsGivenReportActivity.this);
            }
        };

        mQuickAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mReportType == Constants.LOT) {
                    new QueryLotByLotId(mLotId, DrugsGivenReportActivity.this).execute(DrugsGivenReportActivity.this);
                }else{
                    new QueryArchivedLotsByLotId(mLotId, DrugsGivenReportActivity.this).execute(DrugsGivenReportActivity.this);
                }

                mQuickYesterday.setBackground(getDrawable(R.drawable.white_sign_in_btn_accent_border));
                mQuickYesterday.setTextColor(getResources().getColor(R.color.colorAccent));

                mQuickAll.setBackground(getDrawable(R.drawable.accent_sign_in_btn));
                mQuickAll.setTextColor(getResources().getColor(android.R.color.white));

                mQuickMonth.setBackground(getDrawable(R.drawable.white_sign_in_btn_accent_border));
                mQuickMonth.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

        mQuickMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mStartCalendar.setTimeInMillis(System.currentTimeMillis());
                mStartCalendar.add(Calendar.MONTH, -1);
                mEndCalendar.setTimeInMillis(System.currentTimeMillis());

                long startLong = mStartCalendar.getTimeInMillis();
                long endLong = mEndCalendar.getTimeInMillis();

                String friendlyStartDate = Utility.convertMillisToDate(startLong);
                String friendlyEndDate = Utility.convertMillisToDate(endLong);

                mStartDateButton.setText(friendlyStartDate);
                mEndDateButton.setText(friendlyEndDate);

                new QueryDrugsGivenByLotIdAndDateRange(DrugsGivenReportActivity.this,
                        mLotId, startLong, endLong)
                        .execute(DrugsGivenReportActivity.this);

                mQuickYesterday.setBackground(getDrawable(R.drawable.white_sign_in_btn_accent_border));
                mQuickYesterday.setTextColor(getResources().getColor(R.color.colorAccent));

                mQuickMonth.setBackground(getDrawable(R.drawable.accent_sign_in_btn));
                mQuickMonth.setTextColor(getResources().getColor(android.R.color.white));

                mQuickAll.setBackground(getDrawable(R.drawable.white_sign_in_btn_accent_border));
                mQuickAll.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

        mQuickYesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long millisInADay = 86400000;

                long today = System.currentTimeMillis();
                long yesterday = today - millisInADay;

                mStartCalendar.setTimeInMillis(yesterday);
                mStartCalendar.set(Calendar.HOUR, 0);
                mStartCalendar.set(Calendar.MINUTE, 0);
                mStartCalendar.set(Calendar.SECOND, 0);

                mEndCalendar.setTimeInMillis(System.currentTimeMillis());

                long startLong = mStartCalendar.getTimeInMillis();
                long endLong = mEndCalendar.getTimeInMillis();

                String friendlyStartDate = Utility.convertMillisToDate(startLong);
                String friendlyEndDate = Utility.convertMillisToDate(endLong);

                mStartDateButton.setText(friendlyStartDate);
                mEndDateButton.setText(friendlyEndDate);

                new QueryDrugsGivenByLotIdAndDateRange(DrugsGivenReportActivity.this,
                        mLotId, startLong, endLong)
                        .execute(DrugsGivenReportActivity.this);

                mQuickYesterday.setBackground(getDrawable(R.drawable.accent_sign_in_btn));
                mQuickYesterday.setTextColor(getResources().getColor(android.R.color.white));

                mQuickMonth.setBackground(getDrawable(R.drawable.white_sign_in_btn_accent_border));
                mQuickMonth.setTextColor(getResources().getColor(R.color.colorAccent));

                mQuickAll.setBackground(getDrawable(R.drawable.white_sign_in_btn_accent_border));
                mQuickAll.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

        new QueryAllDrugs(this).execute(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong("startLong", mStartCalendar.getTimeInMillis());
        outState.putLong("endLong", mEndCalendar.getTimeInMillis());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    public void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities) {

        for (int a = 0; a < drugEntities.size(); a++) {
            DrugEntity drugEntity = drugEntities.get(a);
            String drugId = drugEntity.getDrugId();
            String drugName = drugEntity.getDrugName();
            mDrugKeyAndName.put(drugId, drugName);
        }

        long startLong = mStartCalendar.getTimeInMillis();
        long endLong = mEndCalendar.getTimeInMillis();

        String friendlyStartDate = Utility.convertMillisToDate(startLong);
        String friendlyEndDate = Utility.convertMillisToDate(endLong);

        mStartDateButton.setText(friendlyStartDate);
        mEndDateButton.setText(friendlyEndDate);

        new QueryDrugsGivenByLotIdAndDateRange(this, mLotId, startLong, endLong).execute(this);

    }

    @Override
    public void onDrugsGivenByLotIdAndDateRangeLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        ArrayList<DrugsGivenEntity> drugsGivenListCondensed = new ArrayList<>();

        for (int e = 0; e < drugsGivenEntities.size(); e++) {
            DrugsGivenEntity drugsGivenEntity = drugsGivenEntities.get(e);
            String drugId = drugsGivenEntity.getDrugId();
            int amountGiven = drugsGivenEntity.getAmountGiven();
            if (findAndUpdateDrugReports(drugId, amountGiven, drugsGivenListCondensed) == 0) {
                drugsGivenListCondensed.add(drugsGivenEntity);
            }
        }

        for (int f = 0; f < drugsGivenListCondensed.size(); f++) {
            DrugsGivenEntity drugGivenList = drugsGivenListCondensed.get(f);
            String drugId = drugGivenList.getDrugId();
            String drugName = mDrugKeyAndName.get(drugId);
            drugGivenList.setDrugId(drugName);
            drugsGivenListCondensed.remove(f);
            drugsGivenListCondensed.add(f, drugGivenList);
        }

        if (drugsGivenListCondensed.size() == 0) {
            mNoDrugsGivenTv.setVisibility(View.VISIBLE);
            mDayRv.setVisibility(View.GONE);
        } else {
            mNoDrugsGivenTv.setVisibility(View.GONE);
            mDayRv.setVisibility(View.VISIBLE);
            timeDrugRvAdapter.swapData(drugsGivenListCondensed);
        }
    }

    private int findAndUpdateDrugReports(String drugId, int amountGiven, ArrayList<DrugsGivenEntity> drugReportsObjects) {
        for (int r = 0; r < drugReportsObjects.size(); r++) {
            DrugsGivenEntity drugReportsObject = drugReportsObjects.get(r);
            if (drugReportsObject.getDrugId().endsWith(drugId)) {
                int currentAmount = drugReportsObject.getAmountGiven();
                int amountToUpdateTo = currentAmount + amountGiven;
                drugReportsObject.setAmountGiven(amountToUpdateTo);
                drugReportsObjects.remove(r);
                drugReportsObjects.add(r, drugReportsObject);
                return 1;
            }
        }
        return 0;
    }

    @Override
    public void onLotByLotIdLoaded(LotEntity lotEntity) {
        long start_date = lotEntity.getDate();
        long end_date = System.currentTimeMillis();

        String friendlyStartDate = Utility.convertMillisToDate(start_date);
        String friendlyEndDate = Utility.convertMillisToDate(end_date);

        mStartCalendar.setTimeInMillis(start_date);
        mEndCalendar.setTimeInMillis(end_date);

        mStartDateButton.setText(friendlyStartDate);
        mEndDateButton.setText(friendlyEndDate);

        new QueryDrugsGivenByLotIdAndDateRange(this, mLotId, start_date, end_date).execute(this);
    }

    @Override
    public void onArchivedLotLoaded(ArchivedLotEntity archivedLotEntity) {
        long start_date = archivedLotEntity.getDateStarted();
        long end_date = archivedLotEntity.getDateEnded();

        String friendlyStartDate = Utility.convertMillisToDate(start_date);
        String friendlyEndDate = Utility.convertMillisToDate(end_date);

        mStartCalendar.setTimeInMillis(start_date);
        mEndCalendar.setTimeInMillis(end_date);

        mStartDateButton.setText(friendlyStartDate);
        mEndDateButton.setText(friendlyEndDate);

        new QueryDrugsGivenByLotIdAndDateRange(this, mLotId, start_date, end_date).execute(this);
    }
}
