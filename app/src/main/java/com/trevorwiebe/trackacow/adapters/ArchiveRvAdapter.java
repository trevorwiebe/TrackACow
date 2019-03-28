package com.trevorwiebe.trackacow.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.db.entities.ArchivedLotEntity;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;

public class ArchiveRvAdapter extends RecyclerView.Adapter<ArchiveRvAdapter.ArchiveViewHolder> {

    ArrayList<ArchivedLotEntity> archivedLotEntities = new ArrayList<>();

    @Override
    public int getItemCount() {
        if (archivedLotEntities == null) return 0;
        return archivedLotEntities.size();
    }

    @NonNull
    @Override
    public ArchiveViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_archived_lot, viewGroup, false);
        return new ArchiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveViewHolder archiveViewHolder, int i) {
        ArchivedLotEntity archivedLotEntity = archivedLotEntities.get(i);

        String name = archivedLotEntity.getLotName();
        long date = archivedLotEntity.getDateEnded();
        String strDate = Utility.convertMillisToDate(date);

        archiveViewHolder.mArchiveLotName.setText(name);
        archiveViewHolder.mDateArchived.setText(strDate);
    }

    public void swapArchivedLots(ArrayList<ArchivedLotEntity> archivedLotEntities) {
        this.archivedLotEntities = new ArrayList<>(archivedLotEntities);
        notifyDataSetChanged();
    }

    public class ArchiveViewHolder extends RecyclerView.ViewHolder {

        private TextView mArchiveLotName;
        private TextView mDateArchived;

        public ArchiveViewHolder(View view) {
            super(view);
            mArchiveLotName = view.findViewById(R.id.archived_lot_name);
            mDateArchived = view.findViewById(R.id.date_archived);
        }
    }
}
