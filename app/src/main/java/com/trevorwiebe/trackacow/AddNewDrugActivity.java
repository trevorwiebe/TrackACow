package com.trevorwiebe.trackacow;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.dataLoaders.InsertDrug;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingDrug;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugEntity;
import com.trevorwiebe.trackacow.utils.Utility;

public class AddNewDrugActivity extends AppCompatActivity {

    private DatabaseReference mDrugRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(DrugEntity.DRUG_OBJECT);

    private TextInputEditText mDrugName;
    private TextInputEditText mDefaultAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_drug);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrugName = findViewById(R.id.add_drug_name);
        mDefaultAmount = findViewById(R.id.default_amount_given);
        Button saveDrug = findViewById(R.id.save_drug);
        Button cancel = findViewById(R.id.add_new_drug_cancel);

        saveDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDrugName.length() == 0 || mDefaultAmount.length() == 0) {
                    Snackbar.make(view, "Please fill the blanks", Snackbar.LENGTH_LONG).show();
                }else{

                    DatabaseReference pushRef = mDrugRef.push();
                    String key = pushRef.getKey();
                    String drugName = mDrugName.getText().toString();
                    int defaultGiven = Integer.parseInt(mDefaultAmount.getText().toString());

                    DrugEntity drugEntity = new DrugEntity(defaultGiven, key, drugName);

                    if(Utility.haveNetworkConnection(AddNewDrugActivity.this)){
                        pushRef.setValue(drugEntity);
                    }else{
                        Utility.setNewDataToUpload(AddNewDrugActivity.this, true);

                        HoldingDrugEntity holdingDrugEntity = new HoldingDrugEntity();
                        holdingDrugEntity.setDefaultAmount(drugEntity.getDefaultAmount());
                        holdingDrugEntity.setDrugId(drugEntity.getDrugId());
                        holdingDrugEntity.setDrugName(drugEntity.getDrugName());
                        holdingDrugEntity.setWhatHappened(Utility.INSERT_UPDATE);
                        new InsertHoldingDrug(holdingDrugEntity).execute(AddNewDrugActivity.this);
                    }

                    new InsertDrug(drugEntity).execute(AddNewDrugActivity.this);

                    mDrugName.setText("");
                    mDefaultAmount.setText("");
                    mDrugName.requestFocus();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

}
