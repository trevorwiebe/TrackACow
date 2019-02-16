package com.trevorwiebe.trackacow;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.adapters.DrugsGivenRecyclerViewAdapter;
import com.trevorwiebe.trackacow.dataLoaders.DeleteDrugGivenById;
import com.trevorwiebe.trackacow.dataLoaders.UpdateDrugGiven;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingDrugGiven;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.QueryDrugsGivenByCowId;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugsGivenEntity;
import com.trevorwiebe.trackacow.utils.ItemClickListener;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;

public class EditDrugsGivenActivity extends AppCompatActivity implements
        QueryDrugsGivenByCowId.OnDrugsGivenByCowIdLoaded,
        QueryAllDrugs.OnAllDrugsLoaded,
        DeleteDrugGivenById.OnDrugDelete,
        UpdateDrugGiven.OnDrugGivenInserted{

    private DatabaseReference mBaseRef;
    private RecyclerView mDrugsGivenRv;
    private DrugsGivenRecyclerViewAdapter drugsGivenRecyclerViewAdapter;
    private TextView mNoDrugsGiven;

    private ArrayList<DrugsGivenEntity> mDrugsGivenEntities = new ArrayList<>();
    private ArrayList<DrugEntity> mDrugEntities = new ArrayList<>();
    private String cowId;

    private static final String TAG = "EditDrugsGivenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drugs_given);

        mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        cowId = getIntent().getStringExtra("cowId");

        new QueryAllDrugs(this).execute(this);

        mNoDrugsGiven = findViewById(R.id.edit_drugs_no_drugs_given);
        mDrugsGivenRv = findViewById(R.id.drugs_given_rv);
        mDrugsGivenRv.setLayoutManager(new LinearLayoutManager(this));
        drugsGivenRecyclerViewAdapter = new DrugsGivenRecyclerViewAdapter(this, mDrugsGivenEntities, mDrugEntities);
        mDrugsGivenRv.setAdapter(drugsGivenRecyclerViewAdapter);

        mDrugsGivenRv.addOnItemTouchListener(new ItemClickListener(this, mDrugsGivenRv, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final DrugsGivenEntity drugsGivenEntity = mDrugsGivenEntities.get(position);

                AlertDialog.Builder drugDialog = new AlertDialog.Builder(EditDrugsGivenActivity.this);
                drugDialog.setTitle("Edit drug given");


                View drugsGivenView = LayoutInflater.from(EditDrugsGivenActivity.this).inflate(R.layout.dialog_edit_drugs_given, null);
                drugDialog.setView(drugsGivenView);


                final TextView drugNameTextView = drugsGivenView.findViewById(R.id.edit_drugs_given_drug_name);
                final DrugEntity drugEntity = Utility.findDrugEntity(drugsGivenEntity.getDrugId(), mDrugEntities);
                String drugName;
                if(drugEntity != null){
                    drugName = drugEntity.getDrugName();
                }else{
                    drugName = "[drug_unavailable]";
                }
                drugNameTextView.setText(drugName);


                final EditText amountGivenEditText = drugsGivenView.findViewById(R.id.edit_drugs_given_amount_given);
                final String amountGivenStr = Integer.toString(drugsGivenEntity.getAmountGiven());

                amountGivenEditText.setText(amountGivenStr);

                drugDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(amountGivenEditText.length() != 0) {

                            int newAmountGiven = Integer.parseInt(amountGivenEditText.getText().toString());
                            Log.d(TAG, "onClick: " + newAmountGiven);

                            drugsGivenEntity.setAmountGiven(newAmountGiven);

                            if(Utility.haveNetworkConnection(EditDrugsGivenActivity.this)){
                                mBaseRef.child(DrugsGivenEntity.DRUGS_GIVEN).child(drugsGivenEntity.getDrugGivenId()).setValue(drugsGivenEntity);
                            }else{

                                Utility.setNewDataToUpload(EditDrugsGivenActivity.this, true);

                                HoldingDrugsGivenEntity holdingDrugsGivenEntity = new HoldingDrugsGivenEntity();
                                holdingDrugsGivenEntity.setDrugGivenId(drugsGivenEntity.getDrugGivenId());
                                holdingDrugsGivenEntity.setWhatHappened(Utility.INSERT_UPDATE);
                                holdingDrugsGivenEntity.setAmountGiven(drugsGivenEntity.getAmountGiven());
                                holdingDrugsGivenEntity.setCowId(drugsGivenEntity.getCowId());
                                holdingDrugsGivenEntity.setDrugId(drugsGivenEntity.getDrugId());
                                holdingDrugsGivenEntity.setPenId(drugsGivenEntity.getPenId());

                                new InsertHoldingDrugGiven(holdingDrugsGivenEntity).execute(EditDrugsGivenActivity.this);

                            }

                            new UpdateDrugGiven(drugsGivenEntity.getDrugGivenId(), drugsGivenEntity.getAmountGiven(), EditDrugsGivenActivity.this).execute(EditDrugsGivenActivity.this);

                        }
                    }
                });
                drugDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                drugDialog.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(Utility.haveNetworkConnection(EditDrugsGivenActivity.this)){
                            mBaseRef.child(DrugsGivenEntity.DRUGS_GIVEN).child(drugsGivenEntity.getDrugGivenId()).removeValue();
                        }else{

                            Utility.setNewDataToUpload(EditDrugsGivenActivity.this, true);

                            HoldingDrugsGivenEntity holdingDrugsGivenEntity = new HoldingDrugsGivenEntity();
                            holdingDrugsGivenEntity.setDrugGivenId(drugsGivenEntity.getDrugGivenId());
                            holdingDrugsGivenEntity.setWhatHappened(Utility.DELETE);
                            holdingDrugsGivenEntity.setAmountGiven(drugsGivenEntity.getAmountGiven());
                            holdingDrugsGivenEntity.setCowId(drugsGivenEntity.getCowId());
                            holdingDrugsGivenEntity.setDrugId(drugsGivenEntity.getDrugId());
                            holdingDrugsGivenEntity.setPenId(drugsGivenEntity.getPenId());

                            new InsertHoldingDrugGiven(holdingDrugsGivenEntity).execute(EditDrugsGivenActivity.this);

                        }

                        new DeleteDrugGivenById(EditDrugsGivenActivity.this, drugsGivenEntity.getDrugId()).execute(EditDrugsGivenActivity.this);

                    }
                });
                drugDialog.show();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        // TODO: 2/16/2019 add the ability to add new drugs given to a cow that has been already treated
    }

    @Override
    public void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities) {
        mDrugEntities = drugEntities;
        new QueryDrugsGivenByCowId(this, cowId).execute(this);
    }

    @Override
    public void onDrugsLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        mDrugsGivenEntities = drugsGivenEntities;
        instantiateRv(mDrugsGivenEntities, mDrugEntities);
    }

    @Override
    public void onDrugDeleted() {
        new QueryDrugsGivenByCowId(this, cowId).execute(this);
    }

    @Override
    public void onDrugGivenInsert() {
        new QueryDrugsGivenByCowId(this, cowId).execute(this);
    }

    private void instantiateRv(ArrayList<DrugsGivenEntity> drugsGivenEntities, ArrayList<DrugEntity> drugEntities){
        if(drugsGivenEntities.size() == 0){
            mNoDrugsGiven.setVisibility(View.VISIBLE);
            mDrugsGivenRv.setVisibility(View.INVISIBLE);
        }else{
            mNoDrugsGiven.setVisibility(View.INVISIBLE);
            mDrugsGivenRv.setVisibility(View.VISIBLE);
            drugsGivenRecyclerViewAdapter.swapData(drugsGivenEntities, drugEntities);
        }
    }
}
