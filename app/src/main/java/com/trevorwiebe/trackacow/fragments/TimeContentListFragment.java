package com.trevorwiebe.trackacow.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.TimeDrugRvAdapter;
import com.trevorwiebe.trackacow.objects.DrugReportsObject;

import java.sql.Time;
import java.util.ArrayList;

public class TimeContentListFragment extends Fragment {

    private static final String TAG = "TimeContentListFragment";

    public static TimeContentListFragment newInstance(int position, int timeType, long startTime, long endTime, ArrayList<DrugReportsObject> drugReportsObjects) {
        TimeContentListFragment timeContentListFragment = new TimeContentListFragment();

        Bundle timeDrugBundle = new Bundle();
        timeDrugBundle.putInt("position", position);
        timeDrugBundle.putInt("timeType", timeType);
        timeDrugBundle.putLong("startTime", startTime);
        timeDrugBundle.putLong("endTime", endTime);
        timeDrugBundle.putParcelableArrayList("drugList", drugReportsObjects);
        timeContentListFragment.setArguments(timeDrugBundle);

        return timeContentListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_time_drug_report_content, container, false);

        Bundle timeDrugBundle = getArguments();

        int position = 0;
        int timeType = 0;
        long startTime = 0;
        long endTime = 0;
        ArrayList<DrugReportsObject> drugReportsObjects = new ArrayList<>();

        if (timeDrugBundle != null) {
            position = timeDrugBundle.getInt("position");
            timeType = timeDrugBundle.getInt("timeType");
            startTime = timeDrugBundle.getLong("startTime");
            endTime = timeDrugBundle.getLong("endTime");
            drugReportsObjects = timeDrugBundle.getParcelableArrayList("drugList");
        }

        RecyclerView drugReportsRv = rootView.findViewById(R.id.time_drug_report_list_rv);
        drugReportsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        drugReportsRv.setHasFixedSize(true);

        TimeDrugRvAdapter timeDrugRvAdapter = new TimeDrugRvAdapter(drugReportsObjects, timeType);
        drugReportsRv.setAdapter(timeDrugRvAdapter);

        return rootView;
    }
}
