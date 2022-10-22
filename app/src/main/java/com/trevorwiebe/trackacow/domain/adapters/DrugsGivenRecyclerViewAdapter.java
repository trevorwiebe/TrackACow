package com.trevorwiebe.trackacow.domain.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.data.entities.DrugEntity;
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.domain.utils.Utility;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class DrugsGivenRecyclerViewAdapter extends RecyclerView.Adapter<DrugsGivenRecyclerViewAdapter.DrugsGivenViewHolder> {

    private ArrayList<DrugsGivenEntity> drugsGivenEntities;
    private ArrayList<DrugEntity> drugEntities;
    private Context mContext;
    private NumberFormat format = NumberFormat.getInstance(Locale.getDefault());

    public DrugsGivenRecyclerViewAdapter(Context context, ArrayList<DrugsGivenEntity> drugsGivenEntities, ArrayList<DrugEntity> drugEntities){
        this.mContext = context;
        this.drugsGivenEntities = drugsGivenEntities;
        this.drugEntities = drugEntities;
    }

    @Override
    public int getItemCount() {
        if(drugsGivenEntities == null) return 0;
        return drugsGivenEntities.size();
    }

    @NonNull
    @Override
    public DrugsGivenViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_drug_given, viewGroup, false);
        return new DrugsGivenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrugsGivenViewHolder drugsGivenViewHolder, int i) {
        DrugsGivenEntity drugsGivenEntity = drugsGivenEntities.get(i);
        String drugId = drugsGivenEntity.getDrugsGivenDrugId();
        int amountGiven = drugsGivenEntity.getDrugsGivenAmountGiven();

        DrugEntity drugEntity = Utility.findDrugEntity(drugId, drugEntities);
        String amountGivenStr = format.format(amountGiven);

        String drugName;

        if(drugEntity != null){
            drugName = drugEntity.getDrugName();
        }else{
            drugName = "[drug_unavailable]";
        }
        String drugsGivenMessage =  amountGivenStr + " units of " + drugName;

        drugsGivenViewHolder.mDrugGiven.setText(drugsGivenMessage);
    }

    public void swapData(ArrayList<DrugsGivenEntity> drugsGivenEntities, ArrayList<DrugEntity> drugEntities){
        this.drugsGivenEntities = drugsGivenEntities;
        this.drugEntities = drugEntities;
        if(drugsGivenEntities != null && drugEntities != null){
            notifyDataSetChanged();
        }
    }

    public class DrugsGivenViewHolder extends RecyclerView.ViewHolder{

        private TextView mDrugGiven;

        public DrugsGivenViewHolder(View view){
            super(view);

            mDrugGiven = view.findViewById(R.id.drug_given_name_and_amount);
        }
    }
}
