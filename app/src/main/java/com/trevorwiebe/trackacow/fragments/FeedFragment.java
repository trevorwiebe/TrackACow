package com.trevorwiebe.trackacow.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.FeedPenViewPagerAdapter;
import com.trevorwiebe.trackacow.dataLoaders.main.pen.QueryAllPens;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;

public class FeedFragment extends Fragment implements QueryAllPens.OnPensLoaded {

    private static final String TAG = "FeedFragment";

    public FeedFragment() {
    }

    private ViewPager feedPenViewPager;
    private Context mContext;
    private FeedPenViewPagerAdapter penViewPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        feedPenViewPager = view.findViewById(R.id.feed_pen_view_pager);
        feedPenViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Utility.saveLastFeedPen(mContext, i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        TabLayout tabs = view.findViewById(R.id.feed_pen_tab_layout);
        tabs.setupWithViewPager(feedPenViewPager);

        new QueryAllPens(this).execute(getContext());

        return view;
    }

    @Override
    public void onPensLoaded(ArrayList<PenEntity> penEntitiesList) {
        penViewPagerAdapter = new FeedPenViewPagerAdapter(getFragmentManager(), penEntitiesList);
        feedPenViewPager.setAdapter(penViewPagerAdapter);
        int lastFeedPen = Utility.getLastFeedPen(mContext);
        feedPenViewPager.setCurrentItem(lastFeedPen, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
