package com.trevorwiebe.trackacow.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.objects.DrugReportsObject;

import java.util.ArrayList;

public class TimeDrugRvAdapter extends RecyclerView.Adapter<TimeDrugRvAdapter.DayViewHolder> {

    private static final String TAG = "TimeDrugRvAdapter";

    private ArrayList<DrugReportsObject> mDrugReportsList;

    public TimeDrugRvAdapter(ArrayList<DrugReportsObject> drugReportsObjects) {
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

        DrugReportsObject drugReportsObject = mDrugReportsList.get(position);

        String drugId = drugReportsObject.getDrugId();
        int amountGiven = drugReportsObject.getDrugAmount();

        holder.mDrugName.setText(drugId);
        holder.mDrugAmountGiven.setText(Integer.toString(amountGiven) + " units");
    }

    @Override
    public int getItemCount() {
        if (mDrugReportsList == null) return 0;
        return mDrugReportsList.size();
    }

    public void swapData(ArrayList<DrugReportsObject> drugReportsObjects) {
        this.mDrugReportsList = new ArrayList<>(drugReportsObjects);
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
