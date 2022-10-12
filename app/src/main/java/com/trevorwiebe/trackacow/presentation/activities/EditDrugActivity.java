package com.trevorwiebe.trackacow.presentation.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
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
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugEntity;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.drug.DeleteDrug;
import com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingDrug.InsertHoldingDrug;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.drug.UpdateDrug;
import com.trevorwiebe.trackacow.data.entities.DrugEntity;
import com.trevorwiebe.trackacow.domain.utils.Constants;
import com.trevorwiebe.trackacow.domain.utils.Utility;

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
        mDrugRef = mBaseRef.child(Constants.DRUG);

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

                        CacheDrugEntity cacheDrugEntity = new CacheDrugEntity(
                                mDrugEntity.getPrimaryKey(),
                                mDrugEntity.getDefaultAmount(),
                                mDrugEntity.getDrugId(),
                                mDrugEntity.getDrugName(),
                                Constants.INSERT_UPDATE
                        );

                        new InsertHoldingDrug(cacheDrugEntity).execute(EditDrugActivity.this);

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


            // show a dialog to confirm that the user understands the ramifications of deletion
            AlertDialog.Builder confirmDeleteDrug = new AlertDialog.Builder(EditDrugActivity.this);
            confirmDeleteDrug.setTitle("Are you sure you want to delete this drug?");
            confirmDeleteDrug.setMessage("If there are cows that have this drug to them, after deletion you will not be able to see what drug it is.");
            confirmDeleteDrug.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    if (Utility.haveNetworkConnection(EditDrugActivity.this)) {
                        mDrugRef.child(mDrugEntity.getDrugId()).removeValue();
                    } else {
                        Utility.setNewDataToUpload(EditDrugActivity.this, true);
                        CacheDrugEntity cacheDrugEntity = new CacheDrugEntity(
                                mDrugEntity.getPrimaryKey(),
                                mDrugEntity.getDefaultAmount(),
                                mDrugEntity.getDrugId(),
                                mDrugEntity.getDrugName(),
                                Constants.DELETE
                        );
                        new InsertHoldingDrug(cacheDrugEntity).execute(EditDrugActivity.this);
                    }

                    new DeleteDrug(mDrugEntity).execute(EditDrugActivity.this);

                    Intent resultIntent = new Intent();

                    resultIntent.putExtra("event", "deleted");
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();

                }
            });
            confirmDeleteDrug.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            confirmDeleteDrug.show();

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
