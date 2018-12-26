package com.trevorwiebe.trackacow.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.objects.PenObject;

import java.util.ArrayList;

public class PenRecyclerViewAdapter extends RecyclerView.Adapter<PenRecyclerViewAdapter.PenViewHolder> {

    private ArrayList<PenObject> mPenList;
    private Context mContext;

    public PenRecyclerViewAdapter(ArrayList<PenObject> penObjectList, Context context){
        this.mPenList = penObjectList;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_pen_recyclerview, viewGroup, false);
        return new PenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PenViewHolder penViewHolder, int i) {

        PenObject penObject = mPenList.get(i);
        String penName = penObject.getPenName();

        penViewHolder.mPen.setText(penName);
    }

    public void swapData(ArrayList<PenObject> penObjects){
        mPenList = penObjects;
        if(mPenList != null){
            notifyDataSetChanged();
        }
    }

    public class PenViewHolder extends RecyclerView.ViewHolder{

        private TextView mPen;

        public PenViewHolder(View view){
            super(view);

            mPen = view.findViewById(R.id.view_pen_tv);
        }
    }
}
