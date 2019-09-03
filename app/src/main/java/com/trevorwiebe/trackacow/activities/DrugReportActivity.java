package com.trevorwiebe.trackacow.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.DrugReportAdapter;
import com.trevorwiebe.trackacow.fragments.DayDrugReportFragment;
import com.trevorwiebe.trackacow.fragments.MonthDrugReportFragment;
import com.trevorwiebe.trackacow.fragments.WeekDrugReportFragment;
import com.trevorwiebe.trackacow.utils.CustomDrugReportViewPager;

public class DrugReportActivity extends AppCompatActivity {

    private DrugReportAdapter mDrugReportAdapter;
    private TabLayout mTimeTabLayout;
    private CustomDrugReportViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_report);

        mTimeTabLayout = findViewById(R.id.time_tab_layout);
        mViewPager = findViewById(R.id.time_view_pager);

        mDrugReportAdapter = new DrugReportAdapter(getSupportFragmentManager());
        mDrugReportAdapter.addFragment(new DayDrugReportFragment(), "Day");
        mDrugReportAdapter.addFragment(new WeekDrugReportFragment(), "Week");
        mDrugReportAdapter.addFragment(new MonthDrugReportFragment(), "Month");

        mViewPager.setAdapter(mDrugReportAdapter);
        mViewPager.setPagingEnabled(false);
        mTimeTabLayout.setupWithViewPager(mViewPager);

    }
}
