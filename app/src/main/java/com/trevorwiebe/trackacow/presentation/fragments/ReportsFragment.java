package com.trevorwiebe.trackacow.presentation.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.presentation.activities.LotReportActivity;
import com.trevorwiebe.trackacow.domain.adapters.ReportsRecyclerViewAdapter;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.QueryLots;
import com.trevorwiebe.trackacow.data.db.entities.LotEntity;
import com.trevorwiebe.trackacow.domain.utils.Constants;
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener;

import java.util.ArrayList;

public class ReportsFragment extends Fragment implements QueryLots.OnLotsLoaded {

    private static final String TAG = "ReportsFragment";

    public ReportsFragment() {
    }

    private ReportsRecyclerViewAdapter mReportsRvAdapter;
    private TextView mNoReportsTv;

    private ArrayList<LotEntity> mLotList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_reports, container, false);

        mNoReportsTv = rootView.findViewById(R.id.empty_reports_tv);

        RecyclerView reportsRv = rootView.findViewById(R.id.reports_rv);
        reportsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mReportsRvAdapter = new ReportsRecyclerViewAdapter();
        reportsRv.setAdapter(mReportsRvAdapter);

        reportsRv.addOnItemTouchListener(new ItemClickListener(getContext(), reportsRv, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent reportsIntent = new Intent(getContext(), LotReportActivity.class);
                String lotId = mLotList.get(position).getLotId();
                reportsIntent.putExtra("lotId", lotId);
                reportsIntent.putExtra("reportType", Constants.LOT);
                startActivity(reportsIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new QueryLots(ReportsFragment.this).execute(context);
    }

    @Override
    public void onLotsLoaded(ArrayList<LotEntity> lotEntities) {
        mLotList = lotEntities;
        if (mLotList.size() == 0) {
            mNoReportsTv.setVisibility(View.VISIBLE);
        } else {
            mNoReportsTv.setVisibility(View.INVISIBLE);
        }
        mReportsRvAdapter.swapLotData(mLotList);
    }
}
