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
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DayDrugReportFragment extends Fragment implements
        QueryAllDrugs.OnAllDrugsLoaded,
        QueryDrugsGivenByLotIdAndDateRange.OnDrugsGivenByLotIdAndDateRangeLoaded {

    private static final String TAG = "DayDrugReportFragment";
    private long mCurrentDate = 0;
    private String mLotId = "";
    private HashMap<String, String> mDrugKeyAndName = new HashMap<>();
    private Long mStartTime;
    private int mLastClicked = 0;

    private TextView mNoDrugsGivenTv;
    private RecyclerView mDayRv;
    private TimeDrugRvAdapter timeDrugRvAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_day_drug_report, container, false);

        final ImageButton dayGoBack = rootView.findViewById(R.id.day_go_back);
        final ImageButton dayGoForward = rootView.findViewById(R.id.day_go_forward);
        final TextView primaryText = rootView.findViewById(R.id.primary_text);

        mNoDrugsGivenTv = rootView.findViewById(R.id.no_drugs_given_report_tv);
        mDayRv = rootView.findViewById(R.id.day_drug_report_rv);
        mDayRv.setLayoutManager(new LinearLayoutManager(getContext()));
        timeDrugRvAdapter = new TimeDrugRvAdapter(null);
        mDayRv.setAdapter(timeDrugRvAdapter);

        mStartTime = 0L;

        Bundle drugReportsBundle = getArguments();
        if (drugReportsBundle != null) {
            mStartTime = drugReportsBundle.getLong("startTime");
            mLotId = drugReportsBundle.getString("lotId");
        }

        mCurrentDate = System.currentTimeMillis();
        String date = Utility.convertMillisToDate(mCurrentDate);
        primaryText.setText(date);

        new QueryAllDrugs(this).execute(getContext());

        dayGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar prevDayCalendar = Calendar.getInstance();
                prevDayCalendar.setTimeInMillis(mCurrentDate);
                prevDayCalendar.add(Calendar.DAY_OF_MONTH, -1);
                long prevDay = prevDayCalendar.getTimeInMillis();

                dayGoForward.setEnabled(true);

                if (prevDay <= mStartTime) {
                    dayGoBack.setEnabled(false);
                } else {
                    dayGoBack.setEnabled(true);
                }

                String date = Utility.convertMillisToDate(prevDay);
                primaryText.setText(date);

                new QueryDrugsGivenByLotIdAndDateRange(DayDrugReportFragment.this, mLotId, prevDay, mCurrentDate).execute(getContext());

                mCurrentDate = prevDay;
            }
        });

        dayGoForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar nextDayCalendar = Calendar.getInstance();
                nextDayCalendar.setTimeInMillis(mCurrentDate);
                nextDayCalendar.add(Calendar.DAY_OF_MONTH, 1);
                long nextDay = nextDayCalendar.getTimeInMillis();

                dayGoBack.setEnabled(true);

                if (nextDay >= System.currentTimeMillis()) {
                    dayGoForward.setEnabled(false);
                } else {
                    dayGoForward.setEnabled(true);
                }

                String date = Utility.convertMillisToDate(mCurrentDate);
                primaryText.setText(date);

                new QueryDrugsGivenByLotIdAndDateRange(DayDrugReportFragment.this, mLotId, mCurrentDate, nextDay).execute(getContext());

                mCurrentDate = nextDay;
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

        Calendar todayStart = Calendar.getInstance();
        Utility.clearTimes(todayStart);
        long todayStartMillis = todayStart.getTimeInMillis();
        mCurrentDate = todayStartMillis;

        new QueryDrugsGivenByLotIdAndDateRange(this, mLotId, todayStartMillis, System.currentTimeMillis()).execute(getContext());

    }

    @Override
    public void onDrugsGivenByLotIdAndDateRangeLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities) {

        ArrayList<DrugReportsObject> drugReportsObjects = new ArrayList<>();

        for (int o = 0; o < drugsGivenEntities.size(); o++) {
            DrugsGivenEntity drugGivenEntity = drugsGivenEntities.get(o);
            String drugId = drugGivenEntity.getDrugId();
            int amountGiven = drugGivenEntity.getAmountGiven();

            DrugReportsObject drugReportsObject = new DrugReportsObject(drugId, amountGiven);
            addDrugToList(drugReportsObject, drugReportsObjects);
        }

        for (int f = 0; f < drugReportsObjects.size(); f++) {
            DrugReportsObject drugReportsObject = drugReportsObjects.get(f);
            String drugId = drugReportsObject.getDrugId();
            String drugName = mDrugKeyAndName.get(drugId);
            drugReportsObject.setDrugId(drugName);
            drugReportsObjects.remove(f);
            drugReportsObjects.add(f, drugReportsObject);
        }

        if (drugReportsObjects.size() == 0) {
            mNoDrugsGivenTv.setVisibility(View.VISIBLE);
            mDayRv.setVisibility(View.GONE);
        } else {
            mNoDrugsGivenTv.setVisibility(View.GONE);
            mDayRv.setVisibility(View.VISIBLE);
            timeDrugRvAdapter.swapData(drugReportsObjects);
        }
    }

    private void addDrugToList(DrugReportsObject drugReportsObjectToAdd, ArrayList<DrugReportsObject> drugReportsObjects) {
        if (drugReportsObjects.size() == 0) {
            drugReportsObjects.add(drugReportsObjectToAdd);
        } else {
            for (int y = 0; y < drugReportsObjects.size(); y++) {
                String drugGivenIdToAdd = drugReportsObjectToAdd.getDrugId();

                DrugReportsObject drugReportsObject = drugReportsObjects.get(y);
                String drugGivenId = drugReportsObject.getDrugId();

                if (drugGivenId.equals(drugGivenIdToAdd)) {
                    int amountGivenToAdd = drugReportsObjectToAdd.getDrugAmount();
                    int currentAmount = drugReportsObject.getDrugAmount();
                    int total = amountGivenToAdd + currentAmount;
                    drugReportsObject.setDrugAmount(total);
                    drugReportsObjects.remove(y);
                    drugReportsObjects.add(y, drugReportsObject);
                    break;
                } else {
                    drugReportsObjects.add(drugReportsObjectToAdd);
                }
            }
        }
    }
}
