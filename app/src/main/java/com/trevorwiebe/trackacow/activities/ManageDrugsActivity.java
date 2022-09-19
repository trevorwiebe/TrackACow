package com.trevorwiebe.trackacow.activities;

import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.ManageDrugRecyclerViewAdapter;
import com.trevorwiebe.trackacow.dataLoaders.main.drug.QueryAllDrugs;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.utils.ItemClickListener;

import java.util.ArrayList;

public class ManageDrugsActivity extends AppCompatActivity implements QueryAllDrugs.OnAllDrugsLoaded {

    private ManageDrugRecyclerViewAdapter mManageDrugRecyclerViewAdapter;
    private ArrayList<DrugEntity> mDrugList = new ArrayList<>();

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
                startActivity(addNewDrugIntent);
            }
        });

        mManageDrugRv = findViewById(R.id.manage_drug_rv);
        mManageDrugRv.setLayoutManager(new LinearLayoutManager(this));
        mManageDrugRecyclerViewAdapter = new ManageDrugRecyclerViewAdapter(mDrugList, this);
        mManageDrugRv.setAdapter(mManageDrugRecyclerViewAdapter);

        mManageDrugRv.addOnItemTouchListener(new ItemClickListener(this, mManageDrugRv, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openEditDrugActivityForResult(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        new QueryAllDrugs(this).execute(this);
    }

    ActivityResultLauncher<Intent> editDrugActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    new QueryAllDrugs(ManageDrugsActivity.this).execute(ManageDrugsActivity.this);
                }
            }
    );

    private void openEditDrugActivityForResult(int position){
        DrugEntity selectedDrugObject = mDrugList.get(position);
        Intent editDrugIntent = new Intent(ManageDrugsActivity.this, EditDrugActivity.class);
        editDrugIntent.putExtra("drugObject", selectedDrugObject);
        editDrugActivityForResult.launch(editDrugIntent);
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
