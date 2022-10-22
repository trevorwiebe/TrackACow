package com.trevorwiebe.trackacow.presentation.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.domain.adapters.ArchiveRvAdapter;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.archivedLot.QueryArchivedLots;
import com.trevorwiebe.trackacow.data.entities.ArchivedLotEntity;
import com.trevorwiebe.trackacow.domain.utils.Constants;
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener;
import com.trevorwiebe.trackacow.presentation.lot_reports.LotReportActivity;

import java.util.ArrayList;

public class ArchivesActivity extends AppCompatActivity implements
        QueryArchivedLots.OnArchivedLotsLoaded {

    private ArchiveRvAdapter archiveRvAdapter = new ArchiveRvAdapter();
    private ArrayList<ArchivedLotEntity> archivedLotEntities = new ArrayList<>();

    private TextView mNoArchives;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archives);

        mNoArchives = findViewById(R.id.no_archives);
        RecyclerView archiveRv = findViewById(R.id.archive_rv);
        archiveRv.setLayoutManager(new LinearLayoutManager(this));
        archiveRv.setAdapter(archiveRvAdapter);

        archiveRv.addOnItemTouchListener(new ItemClickListener(this, archiveRv, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ArchivedLotEntity archivedLotEntity = archivedLotEntities.get(position);
                Intent archiveReportsIntent = new Intent(ArchivesActivity.this, LotReportActivity.class);
                archiveReportsIntent.putExtra("lotId", archivedLotEntity.getLotId());
                archiveReportsIntent.putExtra("reportType", Constants.ARCHIVE);
                startActivity(archiveReportsIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        new QueryArchivedLots(ArchivesActivity.this).execute(ArchivesActivity.this);

    }

    @Override
    public void onArchivedLotsLoaded(ArrayList<ArchivedLotEntity> archivedLotEntities) {
        this.archivedLotEntities = archivedLotEntities;
        if (archivedLotEntities.size() == 0) {
            mNoArchives.setVisibility(View.VISIBLE);
        } else {
            mNoArchives.setVisibility(View.INVISIBLE);
        }
        archiveRvAdapter.swapArchivedLots(archivedLotEntities);
    }
}
