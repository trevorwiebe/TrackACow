package com.trevorwiebe.trackacow.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.TimeDrugReportPagerAdapter;
import com.trevorwiebe.trackacow.objects.DrugReportsObject;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.CustomDrugReportViewPager;

import java.util.ArrayList;

public class DayDrugReportFragment extends Fragment {

    private static final String TAG = "DayDrugReportFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_day_drug_report, container, false);

        ImageButton dayGoBack = rootView.findViewById(R.id.day_go_back);
        ImageButton dayGoForward = rootView.findViewById(R.id.day_go_forward);
        TextView primaryText = rootView.findViewById(R.id.primary_text);
        TextView secondaryText = rootView.findViewById(R.id.secondary_text);
        CustomDrugReportViewPager dayViewPager = rootView.findViewById(R.id.day_view_pager);

        long startTime = 0;
        long endTime = 0;
        long days = 0;
        ArrayList<DrugReportsObject> drugReportsObjects = new ArrayList<>();

        Bundle drugReportsBundle = getArguments();
        if (drugReportsBundle != null) {
            startTime = drugReportsBundle.getLong("startTime");
            endTime = drugReportsBundle.getLong("endTime");
            days = drugReportsBundle.getLong("days");
            drugReportsObjects = drugReportsBundle.getParcelableArrayList("drugReportsList");
        }

        TimeDrugReportPagerAdapter timeDrugReportPagerAdapter = new TimeDrugReportPagerAdapter(getFragmentManager(), Constants.DAY_DRUG_REPORT, startTime, endTime, drugReportsObjects, days);
        dayViewPager.setAdapter(timeDrugReportPagerAdapter);

        return rootView;
    }
}
