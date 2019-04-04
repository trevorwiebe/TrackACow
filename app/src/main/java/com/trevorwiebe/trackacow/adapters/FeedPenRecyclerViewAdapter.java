package com.trevorwiebe.trackacow.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.InsertCallEntity;
import com.trevorwiebe.trackacow.dataLoaders.UpdateCallById;
import com.trevorwiebe.trackacow.db.entities.CallEntity;
import com.trevorwiebe.trackacow.utils.Utility;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FeedPenRecyclerViewAdapter extends RecyclerView.Adapter<FeedPenRecyclerViewAdapter.FeedPenViewHolder> {

    private ArrayList<Long> mDateList;
    private ArrayList<CallEntity> mCallEntities;
    private boolean hasNetworkConnection;
    private DatabaseReference mBaseRef;
    private Context mContext;

    private String mCallId;
    private NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());

    public FeedPenRecyclerViewAdapter(ArrayList<Long> dateList, ArrayList<CallEntity> callEntities, boolean hasNetworkConnection, DatabaseReference databaseReference, Context context) {
        this.hasNetworkConnection = hasNetworkConnection;
        this.mCallEntities = callEntities;
        this.mDateList = dateList;
        this.mBaseRef = databaseReference;
        this.mContext = context;
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
        mCallId = null;

        String friendlyDate = Utility.convertMillisToFriendlyDate(date);
        feedPenViewHolder.mDate.setText(friendlyDate);

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
