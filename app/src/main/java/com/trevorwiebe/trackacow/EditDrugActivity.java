package com.trevorwiebe.trackacow;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.dataLoaders.DeleteDrug;
import com.trevorwiebe.trackacow.dataLoaders.InsertAllLocalChangeToCloud;
import com.trevorwiebe.trackacow.dataLoaders.InsertDrug;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingDrug;
import com.trevorwiebe.trackacow.dataLoaders.UpdateDrug;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugEntity;
import com.trevorwiebe.trackacow.utils.Utility;

public class EditDrugActivity extends AppCompatActivity {

    private TextInputEditText mUpdateDrugName;
    private TextInputEditText mUpdateDefaultAmount;
    private Button mUpdateDrug;
    private Button mDeleteDrug;

    private DatabaseReference mDrugRef;
    private DatabaseReference mBaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drug);

        final DrugEntity selectedDrug = getIntent().getParcelableExtra("drugObject");

        if(selectedDrug == null){
            Intent resultIntent = new Intent();

            setResult(Activity.RESULT_CANCELED, resultIntent);
            finish();
        }

        mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDrugRef = mBaseRef.child(DrugEntity.DRUG_OBJECT);

        mUpdateDrugName = findViewById(R.id.update_drug_name);
        mUpdateDefaultAmount = findViewById(R.id.update_default_amount_given);
        mUpdateDrug = findViewById(R.id.update_drug);
        mDeleteDrug = findViewById(R.id.delete_drug);


        mUpdateDrugName.setText(selectedDrug.getDrugName());
        mUpdateDefaultAmount.setText(Integer.toString(selectedDrug.getDefaultAmount()));

        mUpdateDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUpdateDrug.length() == 0 || mUpdateDefaultAmount.length() == 0){
                    Snackbar.make(view, "Please fill the blanks", Snackbar.LENGTH_LONG).show();
                }else{

                    String drugName = mUpdateDrugName.getText().toString();
                    int defaultAmount = Integer.parseInt(mUpdateDefaultAmount.getText().toString());

                    selectedDrug.setDrugName(drugName);
                    selectedDrug.setDefaultAmount(defaultAmount);

                    if(Utility.haveNetworkConnection(EditDrugActivity.this)) {

                        DatabaseReference drugRef = mDrugRef.child(selectedDrug.getDrugId());
                        drugRef.setValue(selectedDrug);

                    }else{

                        Utility.setNewDataToUpload(EditDrugActivity.this, true);

                        HoldingDrugEntity holdingDrugEntity = new HoldingDrugEntity();
                        holdingDrugEntity.setDefaultAmount(selectedDrug.getDefaultAmount());
                        holdingDrugEntity.setDrugId(selectedDrug.getDrugId());
                        holdingDrugEntity.setDrugName(selectedDrug.getDrugName());
                        holdingDrugEntity.setWhatHappened(Utility.INSERT_UPDATE);

                        new InsertHoldingDrug(holdingDrugEntity).execute(EditDrugActivity.this);

                    }

                    new UpdateDrug(selectedDrug).execute(EditDrugActivity.this);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("event", "edited");

                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        mDeleteDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utility.haveNetworkConnection(EditDrugActivity.this)) {
                    mDrugRef.child(selectedDrug.getDrugId()).removeValue();
                }else{
                    Utility.setNewDataToUpload(EditDrugActivity.this, true);
                    HoldingDrugEntity holdingDrugEntity = new HoldingDrugEntity();
                    holdingDrugEntity.setWhatHappened(Utility.DELETE);
                    holdingDrugEntity.setDrugName(selectedDrug.getDrugName());
                    holdingDrugEntity.setDrugId(selectedDrug.getDrugId());
                    holdingDrugEntity.setDefaultAmount(selectedDrug.getDefaultAmount());
                    new InsertHoldingDrug(holdingDrugEntity).execute(EditDrugActivity.this);
                }

                new DeleteDrug(selectedDrug).execute(EditDrugActivity.this);

                Intent resultIntent = new Intent();

                resultIntent.putExtra("event", "deleted");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

    }
}
