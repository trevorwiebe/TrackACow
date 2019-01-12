package com.trevorwiebe.trackacow.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.objects.DrugObject;

import java.util.ArrayList;


public class ManageDrugRecyclerViewAdapter extends RecyclerView.Adapter<ManageDrugRecyclerViewAdapter.ManageDrugViewHolder> {


    private ArrayList<DrugObject> mDrugList;
    private Context mContext;

    public ManageDrugRecyclerViewAdapter(ArrayList<DrugObject> drugList, Context context){
        this.mDrugList = drugList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ManageDrugViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_drug_rv, viewGroup, false);
        return new ManageDrugViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageDrugViewHolder manageDrugViewHolder, int i) {
        DrugObject drugObject = mDrugList.get(i);

        String drugName = drugObject.getDrugName();
        String defaultGiven = Integer.toString(drugObject.getDefaultAmount());

        manageDrugViewHolder.mDrugName.setText(drugName);
        manageDrugViewHolder.mDefaultAmount.setText(defaultGiven);
    }

    @Override
    public int getItemCount() {
        if(mDrugList == null) return 0;
        return mDrugList.size();
    }

    public void swapData(ArrayList<DrugObject> newDrugList){
        mDrugList = newDrugList;
        if(mDrugList != null){
            notifyDataSetChanged();
        }
    }

    public class ManageDrugViewHolder extends RecyclerView.ViewHolder{

        private TextView mDrugName;
        private TextView mDefaultAmount;

        public ManageDrugViewHolder(View view){
            super(view);

            mDrugName = view.findViewById(R.id.drug_name);
            mDefaultAmount = view.findViewById(R.id.manage_default_given);
        }
    }
}
