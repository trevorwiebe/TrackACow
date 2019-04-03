package com.trevorwiebe.trackacow.adapters;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.CollapsibleActionView;
import android.view.LayoutInflater;
import android.view.View;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.utils.Utility;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FeedPenRecyclerViewAdapter extends RecyclerView.Adapter<FeedPenRecyclerViewAdapter.FeedPenViewHolder> {

    private ArrayList<Long> mDateList;
    private NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());

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
    public void onBindViewHolder(@NonNull final FeedPenViewHolder feedPenViewHolder, int i) {

        long date = mDateList.get(i);

        String friendlyDate = Utility.convertMillisToFriendlyDate(date);
        feedPenViewHolder.mDate.setText(friendlyDate);

        feedPenViewHolder.mCall.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    feedPenViewHolder.mLeftToFeedTv.setText("0");
                } else {
                    String callStr = s.toString();
                    int call = Integer.parseInt(callStr);

                    int leftToFeed;

                    String fedStr = feedPenViewHolder.mFed.getText().toString();
                    if (fedStr.length() != 0) {
                        int amountFed = Integer.parseInt(fedStr);

                        leftToFeed = call - amountFed;
                    } else {
                        leftToFeed = call;
                    }

                    feedPenViewHolder.mLeftToFeedTv.setText(numberFormat.format(leftToFeed));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        feedPenViewHolder.mFed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String callStr = feedPenViewHolder.mCall.getText().toString();
                if (callStr.length() == 0) {
                    callStr = "0";
                }

                int call = Integer.parseInt(callStr);

                if (s.length() != 0) {
                    int fed = Integer.parseInt(s.toString());

                    int leftToFeed = call - fed;

                    feedPenViewHolder.mLeftToFeedTv.setText(numberFormat.format(leftToFeed));
                } else {
                    feedPenViewHolder.mLeftToFeedTv.setText(numberFormat.format(call));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public class FeedPenViewHolder extends RecyclerView.ViewHolder {

        private TextView mDate;
        private EditText mCall;
        private EditText mFed;
        private TextView mLeftToFeedTv;
        private TextView mTotalFeedTv;

        public FeedPenViewHolder(View view) {
            super(view);

            mDate = view.findViewById(R.id.feed_pen_date_text);
            mCall = view.findViewById(R.id.feed_pen_call_edit_text);
            mFed = view.findViewById(R.id.fed_edit_text);
            mLeftToFeedTv = view.findViewById(R.id.left_to_feed_text);
            mTotalFeedTv = view.findViewById(R.id.total_feed_text);

        }
    }
}
