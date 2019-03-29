package com.trevorwiebe.trackacow.fragments;

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
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.FeedPenRecyclerViewAdapter;
import com.trevorwiebe.trackacow.dataLoaders.QueryLotsByPenId;
import com.trevorwiebe.trackacow.db.entities.LotEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class PenFeedFragment extends Fragment implements QueryLotsByPenId.OnLotsByPenIdLoaded {

    String mPenId;

    private static final String TAG = "PenFeedFragment";

    private TextView mEmptyPen;
    private RecyclerView mFeedPenRv;

    public static PenFeedFragment newInstance(String penId) {
        Bundle args = new Bundle();
        args.putString("fragment_pen_id", penId);
        PenFeedFragment fragment = new PenFeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPenId = getArguments().getString("fragment_pen_id");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pen_feed, container, false);

        mEmptyPen = rootView.findViewById(R.id.feed_pen_empty);
        mFeedPenRv = rootView.findViewById(R.id.feed_pen_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        mFeedPenRv.setLayoutManager(linearLayoutManager);

        new QueryLotsByPenId(mPenId, this).execute(getContext());

        return rootView;
    }

    @Override
    public void onLotsByPenIdLoaded(ArrayList<LotEntity> lotEntities) {
        if (lotEntities.size() == 0) {
            mEmptyPen.setVisibility(View.VISIBLE);
        } else {
            mEmptyPen.setVisibility(View.INVISIBLE);

            LotEntity lotEntity = lotEntities.get(0);

            long dateStarted = lotEntity.getDate();
            ArrayList<Long> datesList = getDaysList(dateStarted);
            Collections.reverse(datesList);

            mFeedPenRv.setAdapter(new FeedPenRecyclerViewAdapter(datesList));
        }

    }

    private ArrayList<Long> getDaysList(long dateStarted) {
        ArrayList<Long> days = new ArrayList<>();

        days.add(dateStarted);

        long oneDay = TimeUnit.DAYS.toMillis(1);
        long currentTime = System.currentTimeMillis();

        while (dateStarted + oneDay < currentTime) {
            dateStarted = dateStarted + oneDay;
            days.add(dateStarted);
        }

        return days;
    }
}
