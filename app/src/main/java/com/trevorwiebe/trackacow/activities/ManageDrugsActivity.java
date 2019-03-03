package com.trevorwiebe.trackacow.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.ManageDrugRecyclerViewAdapter;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllDrugs;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.utils.ItemClickListener;

import java.util.ArrayList;

public class ManageDrugsActivity extends AppCompatActivity implements QueryAllDrugs.OnAllDrugsLoaded {

    private ManageDrugRecyclerViewAdapter mManageDrugRecyclerViewAdapter;
    private ArrayList<DrugEntity> mDrugList = new ArrayList<>();
    private static final int UPDATE_DRUG_CALLBACK_CODE = 747;

    private RecyclerView mManageDrugRv;
    private TextView mDrugEmptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_drugs);

        mDrugEmptyList = findViewById(R.id.drug_list_empty);

        FloatingActionButton addNewDrugFab = findViewById(R.id.add_new_drug);
        addNewDrugFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewDrugIntent = new Intent(ManageDrugsActivity.this, AddNewDrugActivity.class);
                startActivityForResult(addNewDrugIntent, UPDATE_DRUG_CALLBACK_CODE);
            }
        });

        mManageDrugRv = findViewById(R.id.manage_drug_rv);
        mManageDrugRv.setLayoutManager(new LinearLayoutManager(this));
        mManageDrugRecyclerViewAdapter = new ManageDrugRecyclerViewAdapter(mDrugList, this);
        mManageDrugRv.setAdapter(mManageDrugRecyclerViewAdapter);

        mManageDrugRv.addOnItemTouchListener(new ItemClickListener(this, mManageDrugRv, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DrugEntity selectedDrugObject = mDrugList.get(position);
                Intent editDrugIntent = new Intent(ManageDrugsActivity.this, EditDrugActivity.class);
                editDrugIntent.putExtra("drugObject", selectedDrugObject);
                startActivityForResult(editDrugIntent, UPDATE_DRUG_CALLBACK_CODE);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        new QueryAllDrugs(this).execute(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == UPDATE_DRUG_CALLBACK_CODE) {
            new QueryAllDrugs(this).execute(this);
        }
    }

    @Override
    public void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities) {
        mDrugList = drugEntities;
        if(mDrugList.size() == 0){
            // show drug list empty
            mDrugEmptyList.setVisibility(View.VISIBLE);
        }else{
            // hide drug list empty
            mDrugEmptyList.setVisibility(View.INVISIBLE);
        }
        mManageDrugRecyclerViewAdapter.swapData(mDrugList);
    }
}
