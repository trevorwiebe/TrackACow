package com.trevorwiebe.trackacow.activities;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.DeleteDrugsGivenByDrugsGivenId;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingDrugGiven;
import com.trevorwiebe.trackacow.dataLoaders.QueryCowIdByCowId;
import com.trevorwiebe.trackacow.dataLoaders.QueryDrugByDrugId;
import com.trevorwiebe.trackacow.dataLoaders.QueryDrugsGivenByDrugsGivenId;
import com.trevorwiebe.trackacow.dataLoaders.UpdateDrugGiven;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugsGivenEntity;
import com.trevorwiebe.trackacow.utils.Utility;

public class EditDrugsGivenToSpecificCowActivity extends AppCompatActivity implements
        QueryCowIdByCowId.OnCowByIdLoaded,
        UpdateDrugGiven.OnDrugGivenInserted,
        QueryDrugByDrugId.OnDrugByDrugIdLoaded,
        QueryDrugsGivenByDrugsGivenId.OnDrugsGivenByDrugsGivenIdLoaded {

    private static final String TAG = "EditDrugsGivenToSpecifi";

    private DatabaseReference mBaseRef;
    private String mCowId;
    private CowEntity mCowEntity;
    private DrugsGivenEntity mDrugsGivenEntity;

    private Button mCancelButton;
    private Button mSaveButton;
    private TextView mDrugName;
    private TextInputEditText mAmountGiven;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drugs_given_to_specific_cow);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mCowId = getIntent().getStringExtra("cowId");
        String drugGivenId = getIntent().getStringExtra("drugGivenId");

        new QueryDrugsGivenByDrugsGivenId(drugGivenId, EditDrugsGivenToSpecificCowActivity.this).execute(EditDrugsGivenToSpecificCowActivity.this);
        new QueryCowIdByCowId(EditDrugsGivenToSpecificCowActivity.this, mCowId).execute(EditDrugsGivenToSpecificCowActivity.this);

        mCancelButton = findViewById(R.id.edit_drug_cancel_button);
        mSaveButton = findViewById(R.id.edit_drug_save_button);
        mDrugName = findViewById(R.id.name_of_drug_given);
        mAmountGiven = findViewById(R.id.edit_drug_given_amount_given);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAmountGiven.length() == 0) {
                    Snackbar.make(v, "Please fill the blank", Snackbar.LENGTH_LONG).show();
                } else {

                    int amountGiven = Integer.parseInt(mAmountGiven.getText().toString());
                    mDrugsGivenEntity.setAmountGiven(amountGiven);

                    if (Utility.haveNetworkConnection(EditDrugsGivenToSpecificCowActivity.this)) {
                        mBaseRef.child(DrugsGivenEntity.DRUGS_GIVEN).child(mDrugsGivenEntity.getDrugGivenId()).setValue(mDrugsGivenEntity);
                    } else {
                        Utility.setNewDataToUpload(EditDrugsGivenToSpecificCowActivity.this, true);

                        HoldingDrugsGivenEntity holdingDrugsGivenEntity = new HoldingDrugsGivenEntity();
                        holdingDrugsGivenEntity.setLotId(mDrugsGivenEntity.getLotId());
                        holdingDrugsGivenEntity.setDrugId(mDrugsGivenEntity.getDrugId());
                        holdingDrugsGivenEntity.setCowId(mDrugsGivenEntity.getCowId());
                        holdingDrugsGivenEntity.setAmountGiven(mDrugsGivenEntity.getAmountGiven());
                        holdingDrugsGivenEntity.setWhatHappened(Utility.INSERT_UPDATE);
                        holdingDrugsGivenEntity.setDrugGivenId(mDrugsGivenEntity.getDrugGivenId());

                        new InsertHoldingDrugGiven(holdingDrugsGivenEntity).execute(EditDrugsGivenToSpecificCowActivity.this);
                    }

                    new UpdateDrugGiven(mDrugsGivenEntity.getDrugGivenId(), amountGiven, EditDrugsGivenToSpecificCowActivity.this).execute(EditDrugsGivenToSpecificCowActivity.this);

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_drugs_given_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_drug_given) {
            if (Utility.haveNetworkConnection(EditDrugsGivenToSpecificCowActivity.this)) {
                mBaseRef.child(DrugsGivenEntity.DRUGS_GIVEN).child(mDrugsGivenEntity.getDrugGivenId()).removeValue();
            } else {
                Utility.setNewDataToUpload(EditDrugsGivenToSpecificCowActivity.this, true);

                HoldingDrugsGivenEntity holdingDrugsGivenEntity = new HoldingDrugsGivenEntity();
                holdingDrugsGivenEntity.setLotId(mDrugsGivenEntity.getLotId());
                holdingDrugsGivenEntity.setDrugId(mDrugsGivenEntity.getDrugId());
                holdingDrugsGivenEntity.setCowId(mDrugsGivenEntity.getCowId());
                holdingDrugsGivenEntity.setAmountGiven(mDrugsGivenEntity.getAmountGiven());
                holdingDrugsGivenEntity.setWhatHappened(Utility.DELETE);
                holdingDrugsGivenEntity.setDrugGivenId(mDrugsGivenEntity.getDrugGivenId());

                new InsertHoldingDrugGiven(holdingDrugsGivenEntity).execute(EditDrugsGivenToSpecificCowActivity.this);
            }

            new DeleteDrugsGivenByDrugsGivenId(mDrugsGivenEntity.getDrugGivenId()).execute(EditDrugsGivenToSpecificCowActivity.this);

            setResult(RESULT_OK);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCowByIdLoaded(CowEntity cowEntity) {
        mCowEntity = cowEntity;
        String tagNumber = Integer.toString(mCowEntity.getTagNumber());
        setTitle("Edit drug given to cow " + tagNumber);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    public void onDrugGivenInsert() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onDrugByDrugIdLoaded(DrugEntity drugEntity) {
        String drugName;
        if (drugEntity == null) {
            drugName = "[drug_unavailable]";
        } else {
            drugName = drugEntity.getDrugName();
        }
        mDrugName.setText(drugName);
    }

    @Override
    public void onDrugsGivenByDrugsGivenIdLoaded(DrugsGivenEntity drugsGivenEntity) {
        mDrugsGivenEntity = drugsGivenEntity;
        new QueryDrugByDrugId(mDrugsGivenEntity.getDrugId(), EditDrugsGivenToSpecificCowActivity.this).execute(EditDrugsGivenToSpecificCowActivity.this);

        int amountGiven = drugsGivenEntity.getAmountGiven();
        String amountGivenStr = Integer.toString(amountGiven);
        int length = amountGivenStr.length();

        mAmountGiven.setText(amountGivenStr);
        mAmountGiven.setSelection(0, length);
        mAmountGiven.requestFocus();

    }
}
