package com.trevorwiebe.trackacow.adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;

public class FeedPenRecyclerViewAdapter extends RecyclerView.Adapter<FeedPenRecyclerViewAdapter.FeedPenViewHolder> {

    private ArrayList<Long> mDateList;

    public FeedPenRecyclerViewAdapter(ArrayList<Long> dateList) {
        this.mDateList = dateList;
    }

    @Override
    public int getItemCount() {
        if (mDateList == null) return 0;
        return mDateList.size();
    }

    @NonNull
    @Override
    public FeedPenViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_feed_pen, viewGroup, false);
        return new FeedPenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedPenViewHolder feedPenViewHolder, int i) {

        long date = mDateList.get(i);

        String friendlyDate = Utility.convertMillisToDate(date);
        feedPenViewHolder.mDate.setText(friendlyDate);

    }

    public class FeedPenViewHolder extends RecyclerView.ViewHolder {

        private TextView mDate;

        public FeedPenViewHolder(View view) {
            super(view);

            mDate = view.findViewById(R.id.feed_pen_date_text);

        }
    }
}
