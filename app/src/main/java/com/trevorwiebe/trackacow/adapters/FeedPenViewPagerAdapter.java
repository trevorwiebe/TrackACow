package com.trevorwiebe.trackacow.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.fragments.PenFeedFragment;

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
        return PenFeedFragment.newInstance(penId);
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
