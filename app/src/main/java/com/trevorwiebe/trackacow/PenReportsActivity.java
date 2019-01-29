package com.trevorwiebe.trackacow;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.objects.CowObject;
import com.trevorwiebe.trackacow.objects.DrugObject;
import com.trevorwiebe.trackacow.objects.DrugsGivenObject;
import com.trevorwiebe.trackacow.objects.PenObject;

import java.util.ArrayList;

public class PenReportsActivity extends AppCompatActivity {

    private static final String TAG = "PenReportsActivity";

    private DatabaseReference mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private PenObject mSelectedPen;
    private ArrayList<DrugObject> mDrugList = new ArrayList<>();
    private ArrayList<DrugReportsObject> mDrugReports = new ArrayList<>();

    private TextView mCustomerName;
    private TextView mTotalHead;
    private TextView mNotes;
    private LinearLayout mDrugsUsedLayout;
    private ProgressBar mLoadingReports;
    private TextView mNoDrugReports;
    private Button mResetPenBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pen_reports);

        mSelectedPen = getIntent().getParcelableExtra("selectedPen");

        mLoadingReports = findViewById(R.id.loading_reports);
        mNoDrugReports = findViewById(R.id.no_drug_reports);
        mResetPenBtn = findViewById(R.id.reset_this_pen_btn);
        mResetPenBtn.setOnClickListener(resetPenListener);

        mDrugsUsedLayout = findViewById(R.id.drugs_used_layout);
        mCustomerName = findViewById(R.id.reports_customer_name);
        mTotalHead = findViewById(R.id.reports_total_head);
        mNotes = findViewById(R.id.reports_notes);
        String customerName = mSelectedPen.getCustomerName();
        String totalHead = Integer.toString(mSelectedPen.getTotalHead());
        String notes = mSelectedPen.getNotes();
        mCustomerName.setText(customerName);
        mTotalHead.setText(totalHead);
        mNotes.setText(notes);

        DatabaseReference drugRef = mBaseRef.child(DrugObject.DRUG_OBJECT);
        drugRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    DrugObject drugObject = snapshot.getValue(DrugObject.class);
                    if(drugObject != null){
                        mDrugList.add(drugObject);
                    }
                }

                Query cow = mBaseRef.child(CowObject.COW).orderByChild(CowObject.PEN_ID).equalTo(mSelectedPen.getPenId());
                cow.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            CowObject cowObject = snapshot.getValue(CowObject.class);
                            if(cowObject != null){
                                ArrayList<DrugsGivenObject> mDrugsGivenObject = cowObject.getmDrugList();
                                for(int i=0; i<mDrugsGivenObject.size(); i++){
                                    DrugsGivenObject drugsGivenObject = mDrugsGivenObject.get(i);
                                    int amountGiven = drugsGivenObject.getAmountGiven();
                                    String id = drugsGivenObject.getDrugId();
                                    if(findAndUpdateDrugReports(id, amountGiven) == 0){
                                        DrugReportsObject drugReportsObject = new DrugReportsObject(id, amountGiven);
                                        mDrugReports.add(drugReportsObject);
                                    }
                                }
                            }
                        }
                        mLoadingReports.setVisibility(View.GONE);
                        if(mDrugReports.size() == 0){
                            mNoDrugReports.setVisibility(View.VISIBLE);
                        }
                        for(int p=0; p<mDrugReports.size(); p++){

                            final float scale = getResources().getDisplayMetrics().density;
                            int pixels16 = (int) (16 * scale + 0.5f);
                            int pixels8 = (int) (8 * scale + 0.5f);

                            DrugReportsObject drugReportsObject = mDrugReports.get(p);
                            DrugObject drugObject = findDrugObject(drugReportsObject.getDrugId());
                            String textToSet = Integer.toString(drugReportsObject.drugAmount) + " ccs of " + drugObject.getDrugName();

                            TextView textView = new TextView(PenReportsActivity.this);
                            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            textViewParams.setMargins(pixels16, pixels8, pixels16, pixels8);
                            textView.setTextColor(getResources().getColor(android.R.color.black));
                            textView.setLayoutParams(textViewParams);

                            textView.setText(textToSet);

                            mDrugsUsedLayout.addView(textView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String activityTitle = "Pen " +mSelectedPen.getPenName()+ " reports";
        setTitle(activityTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pen_reports_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.reports_action_edit){
            Intent editPenIntent = new Intent(PenReportsActivity.this, EditPenActivity.class);
            editPenIntent.putExtra("selectedPen", mSelectedPen);
            startActivity(editPenIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener resetPenListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder confirmDeletion = new AlertDialog.Builder(PenReportsActivity.this);
            confirmDeletion.setTitle("Are you sure?");
            confirmDeletion.setMessage("This will delete all the cows you have treated, and set the pen back to an idle state.");
            confirmDeletion.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final String penId = mSelectedPen.getPenId();
                    Query deleteCowsQuery = mBaseRef.child(CowObject.COW).orderByChild(CowObject.PEN_ID).equalTo(penId);
                    deleteCowsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                snapshot.getRef().removeValue();
                            }
                            mSelectedPen.setNotes("");
                            mSelectedPen.setTotalHead(0);
                            mSelectedPen.setCustomerName("");
                            mSelectedPen.setActive(false);

                            mBaseRef.child(PenObject.PEN_OBJECT).child(penId).setValue(mSelectedPen);

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("event", "deletion");

                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
            confirmDeletion.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            confirmDeletion.show();
        }
    };

    private DrugObject findDrugObject(String drugId){
        for(int r=0; r<mDrugList.size(); r++){
            DrugObject drugObject = mDrugList.get(r);
            if(drugObject.getDrugId().equals(drugId)){
                return drugObject;
            }
        }
        return null;
    }

    private int findAndUpdateDrugReports(String drugId, int amountGiven){
        for(int r=0; r<mDrugReports.size(); r++){
            DrugReportsObject drugReportsObject = mDrugReports.get(r);
            if(drugReportsObject.getDrugId().endsWith(drugId)){
                int currentAmount = drugReportsObject.getDrugAmount();
                int amountToUpdateTo = currentAmount + amountGiven;
                drugReportsObject.setDrugAmount(amountToUpdateTo);
                mDrugReports.remove(r);
                mDrugReports.add(r, drugReportsObject);
                return 1;
            }
        }
        return 0;
    }

    @Keep
    private class DrugReportsObject{

        private String drugId;
        private int drugAmount;

        public DrugReportsObject(){}

        public DrugReportsObject(String drugId, int drugAmount){
            this.drugAmount = drugAmount;
            this.drugId = drugId;
        }

        public int getDrugAmount() {
            return drugAmount;
        }

        public void setDrugAmount(int drugAmount) {
            this.drugAmount = drugAmount;
        }

        public String getDrugId() {
            return drugId;
        }

        public void setDrugId(String drugId) {
            this.drugId = drugId;
        }
    }
}
