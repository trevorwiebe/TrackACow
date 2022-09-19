package com.trevorwiebe.trackacow.domain.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.data.db.entities.DrugsGivenEntity;

import java.util.ArrayList;

public class TimeDrugRvAdapter extends RecyclerView.Adapter<TimeDrugRvAdapter.DayViewHolder> {

    private static final String TAG = "TimeDrugRvAdapter";

    private ArrayList<DrugsGivenEntity> mDrugReportsList;

    public TimeDrugRvAdapter(ArrayList<DrugsGivenEntity> drugReportsObjects) {
        this.mDrugReportsList = drugReportsObjects;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_drug_report, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {

        DrugsGivenEntity drugs = mDrugReportsList.get(position);

        String drugId = drugs.getDrugId();
        int amountGiven = drugs.getAmountGiven();

        if (drugId == null || drugId == "") {
            drugId = "[drug_unavailable]";
        }

        holder.mDrugName.setText(drugId);
        holder.mDrugAmountGiven.setText(amountGiven + " units");
    }

    @Override
    public int getItemCount() {
        if (mDrugReportsList == null) return 0;
        return mDrugReportsList.size();
    }

    public void swapData(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        this.mDrugReportsList = new ArrayList<>(drugsGivenEntities);
        notifyDataSetChanged();
    }

    public class DayViewHolder extends RecyclerView.ViewHolder {

        private TextView mDrugName;
        private TextView mDrugAmountGiven;

        public DayViewHolder(View view) {
            super(view);
            mDrugName = view.findViewById(R.id.time_drug_report_name);
            mDrugAmountGiven = view.findViewById(R.id.time_drug_report_amount_given);
        }
    }
}
