package com.trevorwiebe.trackacow.adapters;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.trevorwiebe.trackacow.fragments.TimeContentListFragment;
import com.trevorwiebe.trackacow.objects.DrugReportsObject;

import java.util.ArrayList;


public class TimeDrugReportPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "TimeDrugReportPagerAdap";

    private int timeType;
    private long startTime;
    private long endTime;
    private ArrayList<DrugReportsObject> drugReportsObjects;
    private long numberOfPages;

    public TimeDrugReportPagerAdapter(FragmentManager fm, int timeType, long startTime, long endTime, ArrayList<DrugReportsObject> drugReportsObjects, long numberOfPages) {
        super(fm);
        this.timeType = timeType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.drugReportsObjects = drugReportsObjects;
        this.numberOfPages = numberOfPages;
    }

    @Override
    public Fragment getItem(int position) {
        return TimeContentListFragment.newInstance(position, timeType, startTime, endTime, drugReportsObjects);
    }

    @Override
    public int getCount() {
        return (int) numberOfPages;
    }

}