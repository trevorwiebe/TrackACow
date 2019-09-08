package com.trevorwiebe.trackacow.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.DrugReportAdapter;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.QueryDrugsGivenByLotIds;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.fragments.DayDrugReportFragment;
import com.trevorwiebe.trackacow.fragments.MonthDrugReportFragment;
import com.trevorwiebe.trackacow.fragments.WeekDrugReportFragment;
import com.trevorwiebe.trackacow.objects.DrugReportsObject;
import com.trevorwiebe.trackacow.utils.CustomDrugReportViewPager;
import com.trevorwiebe.trackacow.utils.Utility;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class DrugsGivenReportActivity extends AppCompatActivity implements
        QueryAllDrugs.OnAllDrugsLoaded,
        QueryDrugsGivenByLotIds.OnDrugsGivenByLotIdLoaded {

    private static final String TAG = "DrugsGivenReportActivit";

    private DrugReportAdapter mDrugReportAdapter;
    private TabLayout mTimeTabLayout;
    private CustomDrugReportViewPager mViewPager;

    private HashMap<String, String> mDrugNameAndKeys = new HashMap<>();
    private String mLotId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugs_given_report);

        mTimeTabLayout = findViewById(R.id.time_tab_layout);
        mViewPager = findViewById(R.id.time_view_pager);

        new QueryAllDrugs(this).execute(this);
    }

    @Override
    public void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities) {

        for (int r = 0; r < drugEntities.size(); r++) {
            DrugEntity drugEntity = drugEntities.get(r);
            String drugName = drugEntity.getDrugName();
            String drugId = drugEntity.getDrugId();
            mDrugNameAndKeys.put(drugId, drugName);
        }

        mLotId = getIntent().getStringExtra("lotId");

        ArrayList<String> lotIds = new ArrayList<>();
        lotIds.add(mLotId);
        new QueryDrugsGivenByLotIds(this, lotIds).execute(this);

    }

    @Override
    public void onDrugsGivenByLotIdLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities) {

        Collections.sort(drugsGivenEntities, new Comparator<DrugsGivenEntity>() {
            @Override
            public int compare(DrugsGivenEntity drugGivenEntity1, DrugsGivenEntity drugGivenEntity2) {
                return Long.compare(drugGivenEntity1.getDate(), drugGivenEntity2.getDate());
            }
        });

        long startDate = drugsGivenEntities.get(0).getDate();

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(startDate);
        Utility.clearTimes(startCalendar);

        long absoluteStartTime = startCalendar.getTimeInMillis();

        Bundle drugBundle = new Bundle();
        drugBundle.putLong("startTime", absoluteStartTime);
        drugBundle.putString("lotId", mLotId);

        DayDrugReportFragment dayDrugReportFragment = new DayDrugReportFragment();
        dayDrugReportFragment.setArguments(drugBundle);

        WeekDrugReportFragment weekDrugReportFragment = new WeekDrugReportFragment();
        weekDrugReportFragment.setArguments(drugBundle);

        MonthDrugReportFragment monthDrugReportFragment = new MonthDrugReportFragment();
        monthDrugReportFragment.setArguments(drugBundle);

        mDrugReportAdapter = new DrugReportAdapter(getSupportFragmentManager());
        mDrugReportAdapter.addFragment(dayDrugReportFragment, "Day");
        mDrugReportAdapter.addFragment(weekDrugReportFragment, "Week");
        mDrugReportAdapter.addFragment(monthDrugReportFragment, "Month");

        mViewPager.setAdapter(mDrugReportAdapter);
        mViewPager.setPagingEnabled(false);
        mTimeTabLayout.setupWithViewPager(mViewPager);

    }
}
