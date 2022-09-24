package com.trevorwiebe.trackacow.presentation.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.presentation.activities.FeedLotActivity;
import com.trevorwiebe.trackacow.domain.adapters.FeedPenRecyclerViewAdapter;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.call.QueryCallsByLotId;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.feed.QueryFeedsByLotId;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.lot.QueryLotsByPenId;
import com.trevorwiebe.trackacow.data.db.entities.CallEntity;
import com.trevorwiebe.trackacow.data.db.entities.FeedEntity;
import com.trevorwiebe.trackacow.data.db.entities.LotEntity;
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class PenFeedFragment extends Fragment implements
        QueryLotsByPenId.OnLotsByPenIdLoaded,
        QueryCallsByLotId.OnCallsByLotIdReturned,
        QueryFeedsByLotId.OnFeedsByLotIdReturned {

    String mPenId;

    private static final String TAG = "PenFeedFragment";

    private TextView mEmptyPen;
    private RecyclerView mFeedPenRv;
    private FeedPenRecyclerViewAdapter feedPenRecyclerViewAdapter;

    private LotEntity mSelectedLotEntity;
    private ArrayList<Long> mDatesList = new ArrayList<>();

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
        feedPenRecyclerViewAdapter = new FeedPenRecyclerViewAdapter();
        mFeedPenRv.setAdapter(feedPenRecyclerViewAdapter);

        mFeedPenRv.addOnItemTouchListener(new ItemClickListener(getContext(), mFeedPenRv, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                long date = mDatesList.get(position);
                String lotId = mSelectedLotEntity.getLotId();

                Intent feedLotIntent = new Intent(getActivity(), FeedLotActivity.class);
                feedLotIntent.putExtra("date", date);
                feedLotIntent.putExtra("lotId", lotId);
                startActivity(feedLotIntent);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        new QueryLotsByPenId(mPenId, this).execute(getContext());

        return rootView;
    }

    @Override
    public void onLotsByPenIdLoaded(ArrayList<LotEntity> lotEntities) {
        if (lotEntities.size() == 0) {
            mEmptyPen.setVisibility(View.VISIBLE);
        } else {
            mEmptyPen.setVisibility(View.INVISIBLE);

            mSelectedLotEntity = lotEntities.get(0);

            String lotId = mSelectedLotEntity.getLotId();

            new QueryCallsByLotId(lotId, PenFeedFragment.this).execute(getContext());
            new QueryFeedsByLotId(lotId, PenFeedFragment.this).execute(getContext());

        }
    }

    @Override
    public void onCallsByLotIdReturned(ArrayList<CallEntity> callEntities) {

        long dateStarted = mSelectedLotEntity.getDate();
        mDatesList = getDaysList(dateStarted);
        Collections.reverse(mDatesList);

        feedPenRecyclerViewAdapter.setDateList(mDatesList);
        feedPenRecyclerViewAdapter.setCallList(callEntities);
    }

    private ArrayList<Long> getDaysList(long roughDateStarted) {
        ArrayList<Long> days = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(roughDateStarted);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long dateStarted = c.getTimeInMillis();

        days.add(dateStarted);

        long oneDay = TimeUnit.DAYS.toMillis(1);
        long currentTime = System.currentTimeMillis();

        while (dateStarted + oneDay < currentTime) {
            dateStarted = dateStarted + oneDay;
            days.add(dateStarted);
        }

        return days;
    }

    @Override
    public void onFeedsByLotIdReturned(ArrayList<FeedEntity> feedEntities) {
        Log.d(TAG, "onFeedsByLotIdReturned: " + feedEntities.toString());
        feedPenRecyclerViewAdapter.setFeedList(feedEntities);
    }
}
