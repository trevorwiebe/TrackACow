package com.trevorwiebe.trackacow.domain.adapters;

import android.content.Context;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.data.db.entities.CowEntity;
import com.trevorwiebe.trackacow.data.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.data.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.domain.utils.Utility;

import java.util.ArrayList;


public class MedicatedCowsRecyclerViewAdapter extends RecyclerView.Adapter<MedicatedCowsRecyclerViewAdapter.TrackCowViewHolder> {

    private static final String TAG = "MedicatedCowsRecyclerVi";

    private ArrayList<CowEntity> mCowList;
    private ArrayList<DrugEntity> mDrugList;
    private ArrayList<DrugsGivenEntity> mDrugsGivenEntities;
    private Context mContext;

    public MedicatedCowsRecyclerViewAdapter(ArrayList<CowEntity> cowObjects, ArrayList<DrugEntity> drugList, ArrayList<DrugsGivenEntity> drugsGivenObjects, Context context) {
        this.mCowList = cowObjects;
        this.mDrugList = drugList;
        this.mDrugsGivenEntities = drugsGivenObjects;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        if (mCowList == null || mDrugList == null || mDrugsGivenEntities == null) return 0;
        return mCowList.size();
    }

    @NonNull
    @Override
    public TrackCowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_medicated_cows, viewGroup, false);
        return new TrackCowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TrackCowViewHolder trackCowViewHolder, final int position) {

        CowEntity cowEntity = mCowList.get(position);
        String tagNumber = Integer.toString(cowEntity.getTagNumber());
        String notes = cowEntity.getNotes();
        String cowId = cowEntity.getCowId();
        long date = cowEntity.getDate();

        trackCowViewHolder.mDate.setText(Utility.convertMillisToFriendlyDate(date));

        trackCowViewHolder.mTagNumber.setText(tagNumber);
        if (notes == null || notes.length() == 0) {
            trackCowViewHolder.mNotes.setVisibility(View.GONE);
        } else {
            trackCowViewHolder.mNotes.setVisibility(View.VISIBLE);
            trackCowViewHolder.mNotes.setText("Notes: " + notes);
        }

        if (cowEntity.getIsAlive() == 1) {
            trackCowViewHolder.mTagNumber.setTextColor(mContext.getResources().getColor(android.R.color.black));
            String message = "";
            ArrayList<DrugsGivenEntity> drugsGivenEntities = Utility.findDrugsGivenEntityByCowId(cowId, mDrugsGivenEntities);
            if(drugsGivenEntities.size() == 0){
                trackCowViewHolder.mDrugsGiven.setText("No drugs given");
                trackCowViewHolder.mDrugsGiven.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            }else{
                trackCowViewHolder.mDrugsGiven.setTypeface(Typeface.DEFAULT);
                for (int q = 0; q < drugsGivenEntities.size(); q++) {
                    DrugsGivenEntity drugsGivenEntity = drugsGivenEntities.get(q);

                    String drugId = drugsGivenEntity.getDrugId();
                    int amountGiven = drugsGivenEntity.getAmountGiven();

                    DrugEntity drugEntity = findDrugEntities(drugId, mDrugList);
                    String drugName = "";
                    if (drugEntity != null) {
                        drugName = drugEntity.getDrugName();
                    }else{
                        drugName = "[drug_unavailable]";
                    }
                    String amountGivenStr = Integer.toString(amountGiven);

                    message = message + amountGivenStr + " units of " + drugName;
                    if (drugsGivenEntities.size() != q + 1) {
                        message = message + "\n";
                    }
                    trackCowViewHolder.mDrugsGiven.setText(message);
                }
            }
        } else {
            trackCowViewHolder.mTagNumber.setTextColor(mContext.getResources().getColor(R.color.redText));
            trackCowViewHolder.mDrugsGiven.setText("This cow is dead");
        }
    }

    public void swapData(ArrayList<CowEntity> cowObjectsList, ArrayList<DrugEntity> drugEntities, ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        mCowList = new ArrayList<>(cowObjectsList);
        mDrugList = new ArrayList<>(drugEntities);
        mDrugsGivenEntities = new ArrayList<>(drugsGivenEntities);
        notifyDataSetChanged();
    }

    public class TrackCowViewHolder extends RecyclerView.ViewHolder {

        private TextView mTagNumber;
        private TextView mDate;
        private TextView mDrugsGiven;
        private TextView mNotes;

        public TrackCowViewHolder(View view) {
            super(view);

            mTagNumber = view.findViewById(R.id.medicated_cow_tag_number);
            mDate = view.findViewById(R.id.date_treated_on);
            mDrugsGiven = view.findViewById(R.id.medication_given);
            mNotes = view.findViewById(R.id.notes);

        }
    }

    private DrugEntity findDrugEntities(String drugId, ArrayList<DrugEntity> drugList) {
        for (int p = 0; p < drugList.size(); p++) {
            DrugEntity drugEntity = drugList.get(p);
            if (drugEntity.getDrugId().equals(drugId)) {
                return drugEntity;
            }
        }
        return null;
    }

}
