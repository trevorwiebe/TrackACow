package com.trevorwiebe.trackacow.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.activities.MedicatedCowsActivity;
import com.trevorwiebe.trackacow.adapters.PenRecyclerViewAdapter;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllPens;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.utils.ItemClickListener;
import com.trevorwiebe.trackacow.utils.SyncDatabase;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MedicateFragment extends Fragment implements
        SyncDatabase.OnDatabaseSynced,
        QueryAllPens.OnPensLoaded {

    private PenRecyclerViewAdapter mPenRecyclerViewAdapter;
    private ArrayList<PenEntity> mPenList = new ArrayList<>();

    private static final String TAG = "MedicateFragment";

    private TextView mNoPensTv;
    private RecyclerView mPenRv;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public MedicateFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.medicate_fragment, container, false);

        mSwipeRefreshLayout = rootView.findViewById(R.id.main_swipe_refresh_layout);
        mNoPensTv = rootView.findViewById(R.id.no_pens_tv);
        mPenRv = rootView.findViewById(R.id.main_rv);
        mPenRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mPenRecyclerViewAdapter = new PenRecyclerViewAdapter(mPenList, false, getContext());
        mPenRv.setAdapter(mPenRecyclerViewAdapter);

        mPenRv.addOnItemTouchListener(new ItemClickListener(getContext(), mPenRv, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent trackCowIntent = new Intent(getContext(), MedicatedCowsActivity.class);
                String penId = mPenList.get(position).getPenId();
                Utility.setPenId(getContext(), penId);
                trackCowIntent.putExtra("penEntityId", penId);
                startActivity(trackCowIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SyncDatabase(MedicateFragment.this, getContext()).beginSync();
            }
        });

        new QueryAllPens(MedicateFragment.this).execute(getContext());

        return rootView;
    }

    @Override
    public void onDatabaseSynced(int resultCode) {

    }

    @Override
    public void onPensLoaded(ArrayList<PenEntity> penEntitiesList) {
        mPenList = penEntitiesList;
        Log.d(TAG, "onPensLoaded: " + penEntitiesList);
        setPenRecyclerView();
    }

    private void setPenRecyclerView() {
        if (mPenList.size() == 0) {
            mNoPensTv.setVisibility(View.VISIBLE);
        } else {
            mNoPensTv.setVisibility(View.INVISIBLE);
        }
        Collections.sort(mPenList, new Comparator<PenEntity>() {
            @Override
            public int compare(PenEntity pen1, PenEntity pen2) {
                return pen1.getPenName().compareTo(pen2.getPenName());
            }
        });
        mPenRecyclerViewAdapter.swapData(mPenList);
        mPenRv.setVisibility(View.VISIBLE);
    }
}
