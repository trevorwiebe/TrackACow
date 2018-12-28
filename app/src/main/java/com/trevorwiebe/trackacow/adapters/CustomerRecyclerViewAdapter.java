package com.trevorwiebe.trackacow.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.objects.CustomerObject;

import java.util.ArrayList;


public class CustomerRecyclerViewAdapter extends RecyclerView.Adapter<CustomerRecyclerViewAdapter.CustomerViewHolder> {

    private ArrayList<CustomerObject> mCustList;
    private Context mContext;

    public CustomerRecyclerViewAdapter(ArrayList<CustomerObject> custList, Context context){
        this.mCustList = custList;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        if(mCustList == null)return 0;
        return mCustList.size();
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_single_string, viewGroup, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder customerViewHolder, int i) {
        CustomerObject customerObject = mCustList.get(i);
        String customerName = customerObject.getCustomerName();

        customerViewHolder.mTextView.setText(customerName);
    }

    public void swapData(ArrayList<CustomerObject> newCustList){
        mCustList = newCustList;
        if(mCustList != null){
            notifyDataSetChanged();
        }
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder{

        private TextView mTextView;

        public CustomerViewHolder(View view){
            super(view);
            mTextView = view.findViewById(R.id.view_single_string);
        }
    }
}
