package com.trevorwiebe.trackacow.domain.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.trevorwiebe.trackacow.data.entities.PenEntity;
import com.trevorwiebe.trackacow.presentation.fragment_pen_feed.FeedPenListFragment;

import java.util.ArrayList;

public class FeedPenViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<PenEntity> penEntities;

    public FeedPenViewPagerAdapter(FragmentManager fragmentManager, ArrayList<PenEntity> penEntities) {
        super(fragmentManager);
        this.penEntities = penEntities;
    }

    @Override
    public Fragment getItem(int i) {
        String penId = penEntities.get(i).getPenId();
        return FeedPenListFragment.newInstance(penId);
    }

    @Override
    public int getCount() {
        if (penEntities == null) return 0;
        return penEntities.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (penEntities == null) return "";
        String penName = penEntities.get(position).getPenName();
        return "Pen: " + penName;
    }
}
