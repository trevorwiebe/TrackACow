package com.trevorwiebe.trackacow;

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

public class AddNewDrugActivity extends AppCompatActivity {

    private DatabaseReference mDrugRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(DrugObject.DRUG_OBJECT);

    private TextInputEditText mDrugName;
    private TextInputEditText mDefaultAmount;
    private TextInputEditText mMinGiven;
    private TextInputEditText mMaxGiven;
    private Button mSaveDrug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_drug);

        mDrugName = findViewById(R.id.add_drug_name);
        mDefaultAmount = findViewById(R.id.default_amount_given);
        mMinGiven = findViewById(R.id.min_given);
        mMaxGiven = findViewById(R.id.maximum_given);
        mSaveDrug = findViewById(R.id.save_drug);

        mSaveDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDrugName.length() == 0 && mDefaultAmount.length() == 0 && mMinGiven.length() == 0 && mMaxGiven.length() == 0) {
                    Snackbar.make(view, "Please fill the blanks", Snackbar.LENGTH_LONG).show();
                }else if(Integer.parseInt(mMaxGiven.getText().toString()) <= Integer.parseInt(mMinGiven.getText().toString())){
                    Snackbar.make(view, "Minimum amount given must be less than maximum amount given", Snackbar.LENGTH_LONG).show();
                }else{

                    DatabaseReference pushRef = mDrugRef.push();
                    String key = pushRef.getKey();
                    String drugName = mDrugName.getText().toString();
                    int defaultGiven = Integer.parseInt(mDefaultAmount.getText().toString());
                    int minGiven = Integer.parseInt(mMinGiven.getText().toString());
                    int maxGiven = Integer.parseInt(mMaxGiven.getText().toString());

                    DrugObject drugObject = new DrugObject(key, drugName, defaultGiven, minGiven, maxGiven);

                    pushRef.setValue(drugObject);

                    Snackbar.make(view, "Drug saved successfully!", Snackbar.LENGTH_LONG).show();

                    mDrugName.setText("");
                    mDefaultAmount.setText("");
                    mMinGiven.setText("");
                    mMaxGiven.setText("");
                    mDrugName.requestFocus();
                }
            }
        });

    }
}
