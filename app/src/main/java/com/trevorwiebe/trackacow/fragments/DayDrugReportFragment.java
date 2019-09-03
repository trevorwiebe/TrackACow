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
import androidx.recyclerview.widget.RecyclerView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.DayDrugRvAdapter;
import com.trevorwiebe.trackacow.utils.CustomDrugReportViewPager;

public class DayDrugReportFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_day_drug_report, container, false);

        ImageButton dayGoBack = rootView.findViewById(R.id.day_go_back);
        ImageButton dayGoForward = rootView.findViewById(R.id.day_go_forward);
        TextView primaryText = rootView.findViewById(R.id.primary_text);
        TextView secondaryText = rootView.findViewById(R.id.secondary_text);
        RecyclerView rv = rootView.findViewById(R.id.day_drug_report_rv);
        DayDrugRvAdapter dayDrugRvAdapter = new DayDrugRvAdapter();

        return rootView;
    }
}
