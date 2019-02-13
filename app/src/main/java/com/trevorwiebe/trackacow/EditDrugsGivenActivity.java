package com.trevorwiebe.trackacow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.trevorwiebe.trackacow.adapters.DrugsGivenRecyclerViewAdapter;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.QueryDrugsGivenByCowId;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;

import java.util.ArrayList;

public class EditDrugsGivenActivity extends AppCompatActivity implements
    QueryDrugsGivenByCowId.OnDrugsGivenByCowIdLoaded,
    QueryAllDrugs.OnAllDrugsLoaded{

    private RecyclerView mDrugsGivenRv;
    private DrugsGivenRecyclerViewAdapter drugsGivenRecyclerViewAdapter;

    private ArrayList<DrugsGivenEntity> mDrugsGivenEntities = new ArrayList<>();
    private ArrayList<DrugEntity> mDrugEntities = new ArrayList<>();
    private String cowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drugs_given);

        cowId = getIntent().getStringExtra("cowId");

        new QueryAllDrugs(this).execute(this);

        mDrugsGivenRv = findViewById(R.id.drugs_given_rv);
        mDrugsGivenRv.setLayoutManager(new LinearLayoutManager(this));
        drugsGivenRecyclerViewAdapter = new DrugsGivenRecyclerViewAdapter(this, mDrugsGivenEntities, mDrugEntities);
        mDrugsGivenRv.setAdapter(drugsGivenRecyclerViewAdapter);

    }

    @Override
    public void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities) {
        mDrugEntities = drugEntities;
        new QueryDrugsGivenByCowId(this, cowId).execute(this);
    }

    @Override
    public void onDrugsLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        mDrugsGivenEntities = drugsGivenEntities;
        drugsGivenRecyclerViewAdapter.swapData(mDrugsGivenEntities, mDrugEntities);
    }
}
