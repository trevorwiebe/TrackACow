package com.trevorwiebe.trackacow.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.objects.DrugsGivenObject;
import com.trevorwiebe.trackacow.objects.CowObject;
import com.trevorwiebe.trackacow.objects.DrugObject;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;


public class MedicatedCowsRecyclerViewAdapter extends RecyclerView.Adapter<MedicatedCowsRecyclerViewAdapter.TrackCowViewHolder> {

    private static final String TAG = "MedicatedCowsRecyclerVi";

    private ArrayList<CowObject> mCowList;
    private ArrayList<DrugObject> mDrugList;
    private Context mContext;

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
        String notes = cowObject.getNotes();

        trackCowViewHolder.mTagNumber.setText(tagNumber);
        if(notes == null || notes.length() == 0){
            trackCowViewHolder.mNotes.setVisibility(View.GONE);
        }else {
            trackCowViewHolder.mNotes.setVisibility(View.VISIBLE);
            trackCowViewHolder.mNotes.setText("Notes: " + notes);
        }

        ArrayList<DrugsGivenObject> drugsGivenObjects = cowObject.getmDrugList();
        String message = "";
        for(int q=0; q<drugsGivenObjects.size(); q++){
            DrugsGivenObject drugsGivenObject = drugsGivenObjects.get(q);

            String drugId = drugsGivenObject.getDrugId();
            int amountGiven = drugsGivenObject.getAmountGiven();
            long date = drugsGivenObject.getDate();

            DrugObject drugObject = findDrugObject(drugId, mDrugList);
            String drugName = "";
            if(drugObject != null) {
                drugName = drugObject.getDrugName();
            }
            String amountGivenStr = Integer.toString(amountGiven);
            String dateStr = Utility.convertMillisToDate(date);

            message = message + amountGivenStr + "cc of " + drugName + " given on " + dateStr;
            if(drugsGivenObjects.size() != q+1){
                message = message + "\n";
            }
        }
        trackCowViewHolder.mDrugsGiven.setText(message);
    }

    public class TrackCowViewHolder extends RecyclerView.ViewHolder{

        private TextView mTagNumber;
        private TextView mDrugsGiven;
        private TextView mNotes;

        public TrackCowViewHolder(View view){
            super(view);

            mTagNumber = view.findViewById(R.id.medicated_cow_tag_number);
            mDrugsGiven = view.findViewById(R.id.medication_given);
            mNotes = view.findViewById(R.id.notes);

        }
    }

    private DrugObject findDrugObject(String drugId, ArrayList<DrugObject> drugList){
        for(int p=0; p<drugList.size(); p++){
            DrugObject drugObject = drugList.get(p);
            if(drugObject.getDrugId().equals(drugId)){
                return drugObject;
            }
        }
        return null;
    }
}
