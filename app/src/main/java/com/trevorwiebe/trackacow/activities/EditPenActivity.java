package com.trevorwiebe.trackacow.activities;

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
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingPen;
import com.trevorwiebe.trackacow.dataLoaders.UpdatePen;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingPenEntity;
import com.trevorwiebe.trackacow.utils.Utility;

public class EditPenActivity extends AppCompatActivity {

    private DatabaseReference mBaseRef;
    private PenEntity mSelectedPen;

    private TextInputEditText mCustomerName;
    private TextInputEditText mTotalHead;
    private TextInputEditText mNotes;
    private Button mUpdateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pen);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mSelectedPen = getIntent().getParcelableExtra("selectedPen");

        mCustomerName = findViewById(R.id.edit_customer_name);
        mTotalHead = findViewById(R.id.edit_total_head);
        mNotes = findViewById(R.id.edit_notes);
        mUpdateBtn = findViewById(R.id.update_pen_btn);
        Button cancel = findViewById(R.id.cancel_update_pen_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mCustomerName.setText(mSelectedPen.getCustomerName());
        mTotalHead.setText(Integer.toString(mSelectedPen.getTotalHead()));
        mNotes.setText(mSelectedPen.getNotes());

        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCustomerName.length() == 0 || mTotalHead.length() == 0){
                    Snackbar.make(v, "Please fill the blanks", Snackbar.LENGTH_LONG).show();
                }else{

                    String customerName = mCustomerName.getText().toString();
                    int totalHead = Integer.parseInt(mTotalHead.getText().toString());
                    String notes = mNotes.getText().toString();

                    mSelectedPen.setCustomerName(customerName);
                    mSelectedPen.setTotalHead(totalHead);
                    mSelectedPen.setNotes(notes);

                    if(Utility.haveNetworkConnection(EditPenActivity.this)){
                        mBaseRef.child(PenEntity.PEN_OBJECT).child(mSelectedPen.getPenId()).setValue(mSelectedPen);
                    }else{

                        Utility.setNewDataToUpload(EditPenActivity.this, true);

                        HoldingPenEntity holdingPenEntity = new HoldingPenEntity();
                        holdingPenEntity.setNotes(mSelectedPen.getNotes());
                        holdingPenEntity.setTotalHead(mSelectedPen.getTotalHead());
                        holdingPenEntity.setCustomerName(mSelectedPen.getCustomerName());
                        holdingPenEntity.setPenName(mSelectedPen.getPenName());
                        holdingPenEntity.setIsActive(mSelectedPen.getIsActive());
                        holdingPenEntity.setPenId(mSelectedPen.getPenId());
                        holdingPenEntity.setWhatHappened(Utility.INSERT_UPDATE);

                        new InsertHoldingPen(holdingPenEntity).execute(EditPenActivity.this);
                    }

                    new UpdatePen(mSelectedPen).execute(EditPenActivity.this);

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

}
