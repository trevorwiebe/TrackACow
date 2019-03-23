package com.trevorwiebe.trackacow.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.db.entities.LotEntity;

import java.util.ArrayList;


public class ReportsRecyclerViewAdapter extends RecyclerView.Adapter<ReportsRecyclerViewAdapter.ReportsViewHolder> {

    private ArrayList<LotEntity> mLotEntities = new ArrayList<>();

    private static final String TAG = "ReportsRecyclerViewAdap";

    public ReportsRecyclerViewAdapter() {
    }

    @Override
    public int getItemCount() {
        if (mLotEntities == null) return 0;
        return mLotEntities.size();
    }

    @NonNull
    @Override
    public ReportsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_reports, viewGroup, false);
        return new ReportsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsViewHolder reportsViewHolder, int i) {
        LotEntity lotEntity = mLotEntities.get(i);
        String lotName = lotEntity.getLotName();

        reportsViewHolder.mLotName.setText(lotName);
    }

    public void swapLotData(ArrayList<LotEntity> lotEntities) {
        mLotEntities = lotEntities;
        notifyDataSetChanged();
    }

    public class ReportsViewHolder extends RecyclerView.ViewHolder {

        private TextView mLotName;

        public ReportsViewHolder(View view) {
            super(view);
            mLotName = view.findViewById(R.id.reports_lot_name);
        }
    }
}
