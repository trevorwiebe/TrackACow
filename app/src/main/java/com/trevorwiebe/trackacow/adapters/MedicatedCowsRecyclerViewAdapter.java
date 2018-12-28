package com.trevorwiebe.trackacow.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.objects.CowDrugObject;
import com.trevorwiebe.trackacow.objects.CowObject;
import com.trevorwiebe.trackacow.objects.DrugObject;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;


public class MedicatedCowsRecyclerViewAdapter extends RecyclerView.Adapter<MedicatedCowsRecyclerViewAdapter.TrackCowViewHolder> {

    private static final String TAG = "MedicatedCowsRecyclerVi";

    private ArrayList<CowObject> mCowList;
    private ArrayList<DrugObject> mDrugList;
    private Context mContext;
    private int mExpandedPosition = -1;
    private MedicatedCowsRecyclerViewAdapter.TrackCowViewHolder mExpandedHolder;

    public MedicatedCowsRecyclerViewAdapter(ArrayList<CowObject> cowObjects, ArrayList<DrugObject> drugList, Context context){
        this.mCowList = cowObjects;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        if(mCowList == null)return 0;
        return mCowList.size();
    }

    @NonNull
    @Override
    public TrackCowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_medicated_cows, viewGroup, false);
        return new TrackCowViewHolder(view);
    }

    public void swapData(ArrayList<CowObject> cowObjectsList, ArrayList<DrugObject> drugObjects){
        mCowList = cowObjectsList;
        mDrugList = drugObjects;
        if(mCowList != null && mDrugList != null){
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final TrackCowViewHolder trackCowViewHolder, final int position) {
        CowObject cowObject = mCowList.get(position);

        String tagNumber = Integer.toString(cowObject.getCowNumber());
        long date = cowObject.getDate();

        trackCowViewHolder.mTagNumber.setText(tagNumber);
        String strDate = Utility.convertMillisToDate(date);
        trackCowViewHolder.mDate.setText("Date treated:" + strDate);

        String notes = cowObject.getNotes();
        trackCowViewHolder.mNotes.setText(notes);

        ArrayList<CowDrugObject> cowDrugObjects = cowObject.getmDrugList();
        for(int q=0; q<cowDrugObjects.size(); q++){
            CowDrugObject cowDrugObject = cowDrugObjects.get(q);

        }

        final boolean isExpanded = position == mExpandedPosition;
        trackCowViewHolder.mMoreLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        trackCowViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mExpandedHolder != null) {
                    mExpandedHolder.mMoreLayout.setVisibility(View.GONE);
                    notifyItemChanged(mExpandedPosition);
                }
                mExpandedPosition = isExpanded ? -1 : trackCowViewHolder.getAdapterPosition();
                mExpandedHolder = isExpanded ? null : trackCowViewHolder;
                notifyItemChanged(trackCowViewHolder.getAdapterPosition());
            }
        });
    }

    public class TrackCowViewHolder extends RecyclerView.ViewHolder{

        private TextView mTagNumber;
        private TextView mDate;
        private TextView mNotes;
        private LinearLayout mMoreLayout;

        public TrackCowViewHolder(View view){
            super(view);

            mTagNumber = view.findViewById(R.id.medicated_cow_tag_number);
            mDate = view.findViewById(R.id.date);
            mNotes = view.findViewById(R.id.notes);
            mMoreLayout = view.findViewById(R.id.more_layout);

        }
    }
}
