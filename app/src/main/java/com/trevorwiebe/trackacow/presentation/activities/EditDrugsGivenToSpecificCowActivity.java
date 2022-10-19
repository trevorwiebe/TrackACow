package com.trevorwiebe.trackacow.presentation.activities;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
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
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugsGivenEntity;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.drugsGiven.DeleteDrugsGivenByDrugsGivenId;
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingDrugsGiven.InsertHoldingDrugGiven;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.cow.QueryCowIdByCowId;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.drug.QueryDrugByDrugId;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.drugsGiven.QueryDrugsGivenByDrugsGivenId;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.drugsGiven.UpdateDrugGivenAmountGiven;
import com.trevorwiebe.trackacow.data.entities.CowEntity;
import com.trevorwiebe.trackacow.data.entities.DrugEntity;
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.domain.utils.Constants;
import com.trevorwiebe.trackacow.domain.utils.Utility;

public class EditDrugsGivenToSpecificCowActivity extends AppCompatActivity implements
        QueryCowIdByCowId.OnCowByIdLoaded,
        UpdateDrugGivenAmountGiven.OnDrugGivenInserted,
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
                        mBaseRef.child(Constants.DRUGS_GIVEN).child(mDrugsGivenEntity.getDrugGivenId()).setValue(mDrugsGivenEntity);
                    } else {
                        Utility.setNewDataToUpload(EditDrugsGivenToSpecificCowActivity.this, true);

                        CacheDrugsGivenEntity cacheDrugsGivenEntity = new CacheDrugsGivenEntity();
                        cacheDrugsGivenEntity.setLotId(mDrugsGivenEntity.getLotId());
                        cacheDrugsGivenEntity.setDrugId(mDrugsGivenEntity.getDrugId());
                        cacheDrugsGivenEntity.setCowId(mDrugsGivenEntity.getCowId());
                        cacheDrugsGivenEntity.setAmountGiven(mDrugsGivenEntity.getAmountGiven());
                        cacheDrugsGivenEntity.setWhatHappened(Constants.INSERT_UPDATE);
                        cacheDrugsGivenEntity.setDrugGivenId(mDrugsGivenEntity.getDrugGivenId());

                        new InsertHoldingDrugGiven(cacheDrugsGivenEntity).execute(EditDrugsGivenToSpecificCowActivity.this);
                    }

                    new UpdateDrugGivenAmountGiven(mDrugsGivenEntity.getDrugGivenId(), amountGiven, EditDrugsGivenToSpecificCowActivity.this).execute(EditDrugsGivenToSpecificCowActivity.this);

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
                mBaseRef.child(Constants.DRUGS_GIVEN).child(mDrugsGivenEntity.getDrugGivenId()).removeValue();
            } else {
                Utility.setNewDataToUpload(EditDrugsGivenToSpecificCowActivity.this, true);

                CacheDrugsGivenEntity cacheDrugsGivenEntity = new CacheDrugsGivenEntity();
                cacheDrugsGivenEntity.setLotId(mDrugsGivenEntity.getLotId());
                cacheDrugsGivenEntity.setDrugId(mDrugsGivenEntity.getDrugId());
                cacheDrugsGivenEntity.setCowId(mDrugsGivenEntity.getCowId());
                cacheDrugsGivenEntity.setAmountGiven(mDrugsGivenEntity.getAmountGiven());
                cacheDrugsGivenEntity.setWhatHappened(Constants.DELETE);
                cacheDrugsGivenEntity.setDrugGivenId(mDrugsGivenEntity.getDrugGivenId());

                new InsertHoldingDrugGiven(cacheDrugsGivenEntity).execute(EditDrugsGivenToSpecificCowActivity.this);
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
