package com.trevorwiebe.trackacow.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.objects.DrugReportsObject;

import java.util.ArrayList;

public class DayDrugRvAdapter extends RecyclerView.Adapter<DayDrugRvAdapter.DayViewHolder> {

    private ArrayList<DrugReportsObject> mDayDrugReportsList;

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_day_drug_report, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {

        DrugReportsObject drugReportsObject = mDayDrugReportsList.get(position);

        String drugId = drugReportsObject.getDrugId();
        int amountGiven = drugReportsObject.getDrugAmount();

        holder.mDrugName.setText(drugId);
        holder.mDrugAmountGiven.setText(amountGiven);
    }

    @Override
    public int getItemCount() {
        if (mDayDrugReportsList == null) return 0;
        return mDayDrugReportsList.size();
    }

    public void swapData(ArrayList<DrugReportsObject> drugReportsObjects) {
        mDayDrugReportsList = new ArrayList<>(drugReportsObjects);
    }

    public class DayViewHolder extends RecyclerView.ViewHolder {

        private TextView mDrugName;
        private TextView mDrugAmountGiven;

        public DayViewHolder(View view) {
            super(view);

            mDrugName = view.findViewById(R.id.day_drug_name);
            mDrugAmountGiven = view.findViewById(R.id.day_drug_amount_given);

        }
    }
}
