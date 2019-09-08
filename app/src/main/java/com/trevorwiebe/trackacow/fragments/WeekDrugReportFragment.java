package com.trevorwiebe.trackacow.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.TimeDrugRvAdapter;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.QueryDrugsGivenByLotIdAndDateRange;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.objects.DrugReportsObject;
import com.trevorwiebe.trackacow.utils.Utility;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class WeekDrugReportFragment extends Fragment implements
        QueryAllDrugs.OnAllDrugsLoaded,
        QueryDrugsGivenByLotIdAndDateRange.OnDrugsGivenByLotIdAndDateRangeLoaded {

    private static final String TAG = "WeekDrugReportFragment";

    private String mLotId = "";
    private HashMap<String, String> mDrugKeyAndName = new HashMap<>();
    private Long mStartTime;
    private LocalDateTime mStartDateTime;
    private LocalDateTime mEndDateTime;

    private TextView mNoDrugsGivenTv;
    private RecyclerView mDayRv;
    private TimeDrugRvAdapter timeDrugRvAdapter;
    private TextView mPrimaryText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drug_report, container, false);

        final ImageButton weekGoBack = rootView.findViewById(R.id.go_back);
        final ImageButton weekGoForward = rootView.findViewById(R.id.go_forward);
        mPrimaryText = rootView.findViewById(R.id.primary_text);

        mNoDrugsGivenTv = rootView.findViewById(R.id.no_drugs_given_report_tv);
        mDayRv = rootView.findViewById(R.id.drug_report_rv);
        mDayRv.setLayoutManager(new LinearLayoutManager(getContext()));
        timeDrugRvAdapter = new TimeDrugRvAdapter(null);
        mDayRv.setAdapter(timeDrugRvAdapter);

        mStartTime = 0L;

        Bundle drugReportsBundle = getArguments();
        if (drugReportsBundle != null) {
            mStartTime = drugReportsBundle.getLong("startTime");
            mLotId = drugReportsBundle.getString("lotId");
        }

        new QueryAllDrugs(this).execute(getContext());

        weekGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mStartDateTime = mStartDateTime.minusWeeks(1);
                mEndDateTime = mEndDateTime.minusWeeks(1);

                long sundayLong = mStartDateTime.toDate().getTime();
                long saturdayLong = mEndDateTime.toDate().getTime();

                String sundayStr = Utility.convertMillisToDate(sundayLong);
                String saturdayStr = Utility.convertMillisToDate(saturdayLong);

                String dateString = sundayStr + " - " + saturdayStr;

                mPrimaryText.setText(dateString);

                new QueryDrugsGivenByLotIdAndDateRange(WeekDrugReportFragment.this, mLotId, sundayLong, saturdayLong).execute(getContext());

            }
        });
        weekGoForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mStartDateTime = mStartDateTime.plusWeeks(1);
                mEndDateTime = mEndDateTime.plusWeeks(1);

                long sundayLong = mStartDateTime.toDate().getTime();
                long saturdayLong = mEndDateTime.toDate().getTime();

                String sundayStr = Utility.convertMillisToDate(sundayLong);
                String saturdayStr = Utility.convertMillisToDate(saturdayLong);

                String dateString = sundayStr + " - " + saturdayStr;

                mPrimaryText.setText(dateString);

                new QueryDrugsGivenByLotIdAndDateRange(WeekDrugReportFragment.this, mLotId, sundayLong, saturdayLong).execute(getContext());

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
        mStartDateTime = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withDayOfWeek(DateTimeConstants.SUNDAY);
        mEndDateTime = now.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withDayOfWeek(DateTimeConstants.SATURDAY).plusWeeks(1);

        long sundayLong = mStartDateTime.toDate().getTime();
        long saturdayLong = mEndDateTime.toDate().getTime();

        String sundayStr = Utility.convertMillisToDate(sundayLong);
        String saturdayStr = Utility.convertMillisToDate(saturdayLong);

        String dateString = sundayStr + " - " + saturdayStr;

        mPrimaryText.setText(dateString);

        new QueryDrugsGivenByLotIdAndDateRange(this, mLotId, sundayLong, saturdayLong).execute(getContext());

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
