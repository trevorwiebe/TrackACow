package com.trevorwiebe.trackacow.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.DeleteDrug;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingDrug;
import com.trevorwiebe.trackacow.dataLoaders.UpdateDrug;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugEntity;
import com.trevorwiebe.trackacow.utils.Utility;

public class EditDrugActivity extends AppCompatActivity {

    private TextInputEditText mUpdateDrugName;
    private TextInputEditText mUpdateDefaultAmount;
    private Button mUpdateDrug;

    private DatabaseReference mDrugRef;
    private DatabaseReference mBaseRef;
    private DrugEntity mDrugEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drug);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrugEntity = getIntent().getParcelableExtra("drugObject");

        if (mDrugEntity == null) {
            Intent resultIntent = new Intent();

            setResult(Activity.RESULT_CANCELED, resultIntent);
            finish();
        }

        mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDrugRef = mBaseRef.child(DrugEntity.DRUG_OBJECT);

        mUpdateDrugName = findViewById(R.id.update_drug_name);
        mUpdateDefaultAmount = findViewById(R.id.update_default_amount_given);
        mUpdateDrug = findViewById(R.id.update_drug);
        Button cancelBtn = findViewById(R.id.update_drug_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mUpdateDrugName.setText(mDrugEntity.getDrugName());
        mUpdateDefaultAmount.setText(Integer.toString(mDrugEntity.getDefaultAmount()));

        mUpdateDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUpdateDrug.length() == 0 || mUpdateDefaultAmount.length() == 0) {
                    Snackbar.make(view, "Please fill the blanks", Snackbar.LENGTH_LONG).show();
                } else {

                    String drugName = mUpdateDrugName.getText().toString();
                    int defaultAmount = Integer.parseInt(mUpdateDefaultAmount.getText().toString());

                    mDrugEntity.setDrugName(drugName);
                    mDrugEntity.setDefaultAmount(defaultAmount);

                    if (Utility.haveNetworkConnection(EditDrugActivity.this)) {

                        DatabaseReference drugRef = mDrugRef.child(mDrugEntity.getDrugId());
                        drugRef.setValue(mDrugEntity);

                    } else {

                        Utility.setNewDataToUpload(EditDrugActivity.this, true);

                        HoldingDrugEntity holdingDrugEntity = new HoldingDrugEntity();
                        holdingDrugEntity.setDefaultAmount(mDrugEntity.getDefaultAmount());
                        holdingDrugEntity.setDrugId(mDrugEntity.getDrugId());
                        holdingDrugEntity.setDrugName(mDrugEntity.getDrugName());
                        holdingDrugEntity.setWhatHappened(Utility.INSERT_UPDATE);

                        new InsertHoldingDrug(holdingDrugEntity).execute(EditDrugActivity.this);

                    }

                    new UpdateDrug(mDrugEntity).execute(EditDrugActivity.this);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("event", "edited");

                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_drug_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_drug) {


            if (Utility.haveNetworkConnection(EditDrugActivity.this)) {
                mDrugRef.child(mDrugEntity.getDrugId()).removeValue();
            } else {
                Utility.setNewDataToUpload(EditDrugActivity.this, true);
                HoldingDrugEntity holdingDrugEntity = new HoldingDrugEntity();
                holdingDrugEntity.setWhatHappened(Utility.DELETE);
                holdingDrugEntity.setDrugName(mDrugEntity.getDrugName());
                holdingDrugEntity.setDrugId(mDrugEntity.getDrugId());
                holdingDrugEntity.setDefaultAmount(mDrugEntity.getDefaultAmount());
                new InsertHoldingDrug(holdingDrugEntity).execute(EditDrugActivity.this);
            }

            new DeleteDrug(mDrugEntity).execute(EditDrugActivity.this);

            Intent resultIntent = new Intent();

            resultIntent.putExtra("event", "deleted");
            setResult(Activity.RESULT_OK, resultIntent);
            finish();

            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
