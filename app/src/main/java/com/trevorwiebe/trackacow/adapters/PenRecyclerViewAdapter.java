package com.trevorwiebe.trackacow.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.db.entities.PenEntity;

import java.util.ArrayList;

public class PenRecyclerViewAdapter extends RecyclerView.Adapter<PenRecyclerViewAdapter.PenViewHolder> {

    private ArrayList<PenEntity> mPenList;
    private Context mContext;
    private boolean isEditing;

    public PenRecyclerViewAdapter(ArrayList<PenEntity> penObjectList, boolean isEditing, Context context){
        this.mPenList = penObjectList;
        this.mContext = context;
        this.isEditing = isEditing;
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

        if(isEditing){
            penViewHolder.mIsPenActive.setVisibility(View.GONE);

            final float scale = mContext.getResources().getDisplayMetrics().density;
            int pixels16 = (int) (16 * scale + 0.5f);
            int pixels8 = (int) (8 * scale + 0.5f);

            penViewHolder.mPen.setPadding(pixels8, pixels16, pixels8, pixels16);
        }else {
            int isActive = penEntity.getIsActive();
            if (isActive == 1) {
                penViewHolder.mIsPenActive.setText("Pen is active");
                penViewHolder.mIsPenActive.setTextColor(mContext.getResources().getColor(R.color.greenText));
            } else {
                penViewHolder.mIsPenActive.setText("Pen is idle");
                penViewHolder.mIsPenActive.setTextColor(mContext.getResources().getColor(R.color.redText));
            }
        }

    }

    public void swapData(ArrayList<PenEntity> penObjects){
        mPenList = penObjects;
        if(mPenList != null){
            notifyDataSetChanged();
        }
    }

    public class PenViewHolder extends RecyclerView.ViewHolder{

        private TextView mPen;
        private TextView mIsPenActive;

        public PenViewHolder(View view){
            super(view);

            mPen = view.findViewById(R.id.pen);
            mIsPenActive = view.findViewById(R.id.is_pen_active);
        }
    }
}
