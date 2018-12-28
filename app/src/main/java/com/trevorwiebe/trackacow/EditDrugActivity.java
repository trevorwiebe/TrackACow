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
import com.trevorwiebe.trackacow.objects.DrugObject;

import org.w3c.dom.Text;

import java.text.NumberFormat;

public class EditDrugActivity extends AppCompatActivity {

    private TextInputEditText mUpdateDrugName;
    private TextInputEditText mUpdateDefaultAmount;
    private TextInputEditText mUpdateMinAmount;
    private TextInputEditText mUpdateMaxAmount;
    private Button mUpdateDrug;
    private Button mDeleteDrug;

    private DatabaseReference mDrugRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drug);

        final DrugObject selectedDrug = getIntent().getParcelableExtra("drugObject");

        if(selectedDrug == null){
            Intent resultIntent = new Intent();

            setResult(Activity.RESULT_CANCELED, resultIntent);
            finish();
        }

        mDrugRef = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(DrugObject.DRUG_OBJECT);

        mUpdateDrugName = findViewById(R.id.update_drug_name);
        mUpdateDefaultAmount = findViewById(R.id.update_default_amount_given);
        mUpdateMinAmount = findViewById(R.id.update_min_given);
        mUpdateMaxAmount = findViewById(R.id.update_maximum_given);
        mUpdateDrug = findViewById(R.id.update_drug);
        mDeleteDrug = findViewById(R.id.delete_drug);


        mUpdateDrugName.setText(selectedDrug.getDrugName());
        mUpdateDefaultAmount.setText(Integer.toString(selectedDrug.getDefaultAmount()));
        mUpdateMinAmount.setText(Integer.toString(selectedDrug.getMinAmount()));
        mUpdateMaxAmount.setText(Integer.toString(selectedDrug.getMaxAmount()));

        mUpdateDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUpdateDrug.length() == 0 && mUpdateDefaultAmount.length() == 0 && mUpdateMinAmount.length() == 0 && mUpdateMaxAmount.length() == 0){
                    Snackbar.make(view, "Please fill the blanks", Snackbar.LENGTH_LONG).show();
                }else if(Integer.parseInt(mUpdateMinAmount.getText().toString()) >= Integer.parseInt(mUpdateMaxAmount.getText().toString())){
                    Snackbar.make(view, "Minimum amount given must be less than maximum amount given", Snackbar.LENGTH_LONG).show();
                }else{

                    String drugName = mUpdateDrugName.getText().toString();
                    int defaultAmount = Integer.parseInt(mUpdateDefaultAmount.getText().toString());
                    int minAmount = Integer.parseInt(mUpdateMinAmount.getText().toString());
                    int maxAmount = Integer.parseInt(mUpdateMaxAmount.getText().toString());

                    if(selectedDrug != null){
                        selectedDrug.setDrugName(drugName);
                        selectedDrug.setDefaultAmount(defaultAmount);
                        selectedDrug.setMinAmount(minAmount);
                        selectedDrug.setMaxAmount(maxAmount);
                    }

                    DatabaseReference drugRef = mDrugRef.child(selectedDrug.getDrugId());
                    drugRef.setValue(selectedDrug);

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
                mDrugRef.child(selectedDrug.getDrugId()).removeValue();
                Intent resultIntent = new Intent();

                resultIntent.putExtra("event", "deleted");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

    }
}
