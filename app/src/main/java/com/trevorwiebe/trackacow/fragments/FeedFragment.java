package com.trevorwiebe.trackacow.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.FeedPenViewPagerAdapter;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllPens;
import com.trevorwiebe.trackacow.db.entities.PenEntity;

import java.util.ArrayList;

public class FeedFragment extends Fragment implements QueryAllPens.OnPensLoaded {

    public FeedFragment() {
    }

    ViewPager feedPenViewPager;
    FeedPenViewPagerAdapter penViewPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        feedPenViewPager = view.findViewById(R.id.feed_pen_view_pager);
        TabLayout tabs = view.findViewById(R.id.feed_pen_tab_layout);
        tabs.setupWithViewPager(feedPenViewPager);

        new QueryAllPens(this).execute(getContext());

        return view;
    }

    @Override
    public void onPensLoaded(ArrayList<PenEntity> penEntitiesList) {
        penViewPagerAdapter = new FeedPenViewPagerAdapter(getFragmentManager(), penEntitiesList);
        feedPenViewPager.setAdapter(penViewPagerAdapter);
    }
}
