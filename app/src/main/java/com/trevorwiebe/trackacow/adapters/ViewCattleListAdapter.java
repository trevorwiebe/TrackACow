package com.trevorwiebe.trackacow.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.view.View;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.db.entities.LoadEntity;
import com.trevorwiebe.trackacow.utils.Utility;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ViewCattleListAdapter extends RecyclerView.Adapter<ViewCattleListAdapter.ViewCattleViewHolder> {


    private ArrayList<LoadEntity> loadEntities = new ArrayList<>();
    private NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());

    private static final String TAG = "ViewCattleListAdapter";

    public ViewCattleListAdapter() {
    }

    @Override
    public int getItemCount() {
        if (loadEntities == null) return 0;
        return loadEntities.size();
    }

    @NonNull
    @Override
    public ViewCattleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_load_of_cattle, viewGroup, false);
        return new ViewCattleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewCattleViewHolder viewCattleViewHolder, int i) {
        LoadEntity loadEntity = loadEntities.get(i);

        int headAdded = loadEntity.getNumberOfHead();
        long date = loadEntity.getDate();
        String notes = loadEntity.getDescription();

        String strDate = "Date: " + Utility.convertMillisToDate(date);
        String strHeadAddedStr = "Head added: " + numberFormat.format(headAdded);

        viewCattleViewHolder.mHeadAdded.setText(strHeadAddedStr);
        viewCattleViewHolder.mDate.setText(strDate);
        Log.d(TAG, "onBindViewHolder: " + notes);
        if (notes == null || notes.length() == 0) {
            viewCattleViewHolder.mNotes.setVisibility(View.GONE);
        } else {
            viewCattleViewHolder.mNotes.setVisibility(View.VISIBLE);
            viewCattleViewHolder.mNotes.setText("Memo: " + notes);
        }
    }

    public void setData(ArrayList<LoadEntity> loadEntities) {
        this.loadEntities = new ArrayList<>(loadEntities);
        notifyDataSetChanged();
    }

    public class ViewCattleViewHolder extends RecyclerView.ViewHolder {

        private TextView mHeadAdded;
        private TextView mDate;
        private TextView mNotes;

        public ViewCattleViewHolder(View view) {
            super(view);

            mHeadAdded = view.findViewById(R.id.item_head_count);
            mDate = view.findViewById(R.id.item_date);
            mNotes = view.findViewById(R.id.item_notes);

        }

    }
}
