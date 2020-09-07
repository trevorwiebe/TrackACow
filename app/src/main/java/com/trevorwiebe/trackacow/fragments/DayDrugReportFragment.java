package com.trevorwiebe.trackacow.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.activities.MedicatedCowsActivity;
import com.trevorwiebe.trackacow.adapters.TimeDrugRvAdapter;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.QueryDrugsGivenByLotIdAndDateRange;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.utils.Utility;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DayDrugReportFragment extends Fragment implements
        QueryAllDrugs.OnAllDrugsLoaded,
        QueryDrugsGivenByLotIdAndDateRange.OnDrugsGivenByLotIdAndDateRangeLoaded {

    private static final String TAG = "DayDrugReportFragment";
    private String mLotId = "";
    private HashMap<String, String> mDrugKeyAndName = new HashMap<>();
    private Long mStartTime;
    private LocalDateTime mStartDateTime;
    private LocalDateTime mEndDateTime;
    private DatePickerDialog.OnDateSetListener mDatePicker;
    private Calendar mCalendar = Calendar.getInstance();
    private Long mMillisInADay;

    private TextView mNoDrugsGivenTv;
    private RecyclerView mDayRv;
    private TimeDrugRvAdapter timeDrugRvAdapter;
    private TextView mSelectedDateBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drug_report, container, false);

        final ImageButton dayGoBack = rootView.findViewById(R.id.go_back);
        final ImageButton dayGoForward = rootView.findViewById(R.id.go_forward);
        mSelectedDateBtn = rootView.findViewById(R.id.selected_date_btn);

        mNoDrugsGivenTv = rootView.findViewById(R.id.no_drugs_given_report_tv);
        mDayRv = rootView.findViewById(R.id.drug_report_rv);
        mDayRv.setLayoutManager(new LinearLayoutManager(getContext()));
        timeDrugRvAdapter = new TimeDrugRvAdapter(null);
        mDayRv.setAdapter(timeDrugRvAdapter);

        mStartTime = 0L;
        mMillisInADay = TimeUnit.DAYS.toMillis(1);

        mCalendar.set(Calendar.HOUR, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);

        Bundle drugReportsBundle = getArguments();
        if (drugReportsBundle != null) {
            mStartTime = drugReportsBundle.getLong("startTime");
            mLotId = drugReportsBundle.getString("lotId");
        }

        mSelectedDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(getContext(),
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
                mSelectedDateBtn.setText(Utility.convertMillisToDate(mCalendar.getTimeInMillis()));

                long selectedStartDate = mCalendar.getTimeInMillis();
                long selectedEndDate = selectedStartDate + mMillisInADay;

                new QueryDrugsGivenByLotIdAndDateRange(DayDrugReportFragment.this, mLotId, selectedStartDate, selectedEndDate).execute(getContext());
            }
        };

        new QueryAllDrugs(this).execute(getContext());

        dayGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartDateTime = mStartDateTime.minusDays(1);
                mEndDateTime = mEndDateTime.minusDays(1);

                long startLong = mStartDateTime.toDate().getTime();
                long endLong = mEndDateTime.toDate().getTime();

                String startLongStr = Utility.convertMillisToDate(startLong);

                mSelectedDateBtn.setText(startLongStr);

                new QueryDrugsGivenByLotIdAndDateRange(DayDrugReportFragment.this, mLotId, startLong, endLong).execute(getContext());
            }
        });

        dayGoForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartDateTime = mStartDateTime.plusDays(1);
                mEndDateTime = mEndDateTime.plusDays(1);

                long startLong = mStartDateTime.toDate().getTime();
                long endLong = mEndDateTime.toDate().getTime();

                String sundayStr = Utility.convertMillisToDate(startLong);

                mSelectedDateBtn.setText(sundayStr);

                new QueryDrugsGivenByLotIdAndDateRange(DayDrugReportFragment.this, mLotId, startLong, endLong).execute(getContext());
            }
        });

        return rootView;
    }

    @Override
    public void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities) {

        for (int a = 0; a < drugEntities.size(); a++) {
            DrugEntity drugEntity = drugEntities.get(a);
            String drugId = drugEntity.getDrugId();
            String drugName = drugEntity.getDrugName();
            mDrugKeyAndName.put(drugId, drugName);
        }

        LocalDateTime now = LocalDateTime.now();
        mStartDateTime = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
        mEndDateTime = now.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);

        long startLong = mStartDateTime.toDate().getTime();
        long endLong = mEndDateTime.toDate().getTime();

        String sundayStr = Utility.convertMillisToDate(startLong);

        mSelectedDateBtn.setText(sundayStr);

        new QueryDrugsGivenByLotIdAndDateRange(this, mLotId, startLong, endLong).execute(getContext());

    }

    @Override
    public void onDrugsGivenByLotIdAndDateRangeLoaded(ArrayList<DrugsGivenEntity> drugsGivenList) {

        ArrayList<DrugsGivenEntity> drugsGivenListCondensed = new ArrayList<>();

        for (int e = 0; e < drugsGivenList.size(); e++) {
            DrugsGivenEntity drugsGivenEntity = drugsGivenList.get(e);
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
}
