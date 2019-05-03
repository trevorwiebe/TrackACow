package com.trevorwiebe.trackacow.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;

import java.util.ArrayList;
import java.util.ListIterator;

public class PenRecyclerViewAdapter extends RecyclerView.Adapter<PenRecyclerViewAdapter.PenViewHolder> {

    private static final String TAG = "PenRecyclerViewAdapter";

    private ArrayList<PenEntity> mPenList;
    private ArrayList<LotEntity> mLotList;
    private Context mContext;

    public PenRecyclerViewAdapter(ArrayList<PenEntity> penObjectList, ArrayList<LotEntity> lotEntities, Context context) {
        this.mPenList = penObjectList;
        this.mLotList = lotEntities;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        if(mPenList == null)return 0;
        return mPenList.size();
    }

    @NonNull
    @Override
    public PenViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_pen, viewGroup, false);
        return new PenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PenViewHolder penViewHolder, int i) {

        PenEntity penEntity = mPenList.get(i);
        String penName = penEntity.getPenName();
        penViewHolder.mPen.setText(penName);

        if (mLotList != null) {

            ArrayList<LotEntity> lotEntities = findLotEntities(penEntity.getPenId());
            if (lotEntities.size() != 0) {
                penViewHolder.mLotNames.setTextColor(mContext.getResources().getColor(R.color.greenText));
                StringBuilder lotTextName = new StringBuilder();
                for (int q = 0; q < lotEntities.size(); q++) {
                    LotEntity lotEntity = lotEntities.get(q);
                    String lotName = lotEntity.getLotName();
                    lotTextName.append(lotName).append("  ");
                }
                penViewHolder.mLotNames.setText(lotTextName);
            } else {
                penViewHolder.mLotNames.setTextColor(mContext.getResources().getColor(R.color.redText));
                penViewHolder.mLotNames.setText("No cattle in this pen");
            }

        } else {
            penViewHolder.mLotNames.setVisibility(View.GONE);
        }

    }

    public void swapData(ArrayList<PenEntity> penObjects, ArrayList<LotEntity> lotEntities) {
        mPenList = new ArrayList<>(penObjects);
        if (lotEntities != null) {
            mLotList = new ArrayList<>(lotEntities);
        }
        notifyDataSetChanged();
    }

    public class PenViewHolder extends RecyclerView.ViewHolder{

        private TextView mPen;
        private TextView mLotNames;

        public PenViewHolder(View view){
            super(view);

            mPen = view.findViewById(R.id.pen);
            mLotNames = view.findViewById(R.id.lot_names);
        }
    }

    private ArrayList<LotEntity> findLotEntities(String penId) {
        ArrayList<LotEntity> lotEntities = new ArrayList<>();
        for (int o = 0; o < mLotList.size(); o++) {
            LotEntity lotEntity = mLotList.get(o);
            if (lotEntity.getPenId().equals(penId)) {
                lotEntities.add(lotEntity);
            }
        }
        return lotEntities;
    }

}
