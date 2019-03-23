package com.trevorwiebe.trackacow.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.activities.LotReportActivity;
import com.trevorwiebe.trackacow.adapters.ReportsRecyclerViewAdapter;
import com.trevorwiebe.trackacow.dataLoaders.QueryLots;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.utils.ItemClickListener;

import java.util.ArrayList;

public class ReportsFragment extends Fragment implements QueryLots.OnLotsLoaded {

    private static final String TAG = "ReportsFragment";

    public ReportsFragment() {
    }

    private ReportsRecyclerViewAdapter mReportsRvAdapter;

    private ArrayList<LotEntity> mLotList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_reports, container, false);

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
        mReportsRvAdapter.swapLotData(mLotList);
    }
}
