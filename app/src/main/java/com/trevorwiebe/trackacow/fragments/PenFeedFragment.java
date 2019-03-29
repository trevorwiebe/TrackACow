package com.trevorwiebe.trackacow.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trevorwiebe.trackacow.R;

public class PenFeedFragment extends Fragment {

    String mPenId;

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

        return rootView;
    }
}
