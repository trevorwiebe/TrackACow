package com.trevorwiebe.trackacow;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.trevorwiebe.trackacow.dataLoaders.DeleteCowsByPenId;
import com.trevorwiebe.trackacow.dataLoaders.DeleteDrugsGivenByPenId;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingPen;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.QueryDeadCowsByPenId;
import com.trevorwiebe.trackacow.dataLoaders.QueryDrugsGivenByPenId;
import com.trevorwiebe.trackacow.dataLoaders.UpdatePen;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingPenEntity;
import com.trevorwiebe.trackacow.utils.Utility;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PenReportsActivity extends AppCompatActivity implements
        QueryAllDrugs.OnAllDrugsLoaded,
        QueryDrugsGivenByPenId.OnDrugsGivenLoaded,
        QueryDeadCowsByPenId.OnDeadCowsLoaded {

    private static final String TAG = "PenReportsActivity";

    private DatabaseReference mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private PenEntity mSelectedPen;
    private ArrayList<DrugEntity> mDrugList = new ArrayList<>();

    private TextView mCustomerName;
    private TextView mTotalHead;
    private TextView mNotes;
    private TextView mTotalDeathLoss;
    private TextView mDeathLossPercentage;
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
        mTotalDeathLoss = findViewById(R.id.reports_death_loss);
        mDeathLossPercentage = findViewById(R.id.reports_death_loss_percentage);
        mCustomerName = findViewById(R.id.reports_customer_name);
        mTotalHead = findViewById(R.id.reports_total_head);
        mNotes = findViewById(R.id.reports_notes);
        String customerName = mSelectedPen.getCustomerName();
        String totalHead = Integer.toString(mSelectedPen.getTotalHead());
        String notes = mSelectedPen.getNotes();
        mCustomerName.setText(customerName);
        mTotalHead.setText(totalHead);
        mNotes.setText(notes);

        new QueryAllDrugs(this).execute(this);
        new QueryDeadCowsByPenId(this, mSelectedPen.getPenId()).execute(this);

        String activityTitle = "Pen " + mSelectedPen.getPenName() + " reports";
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

    @Override
    public void onDeadCowsLoaded(ArrayList<CowEntity> cowEntities) {
        int numberDead = cowEntities.size();
        int total = mSelectedPen.getTotalHead();

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        float percent = (numberDead * 100.f) / total;

        mTotalDeathLoss.setText(Integer.toString(numberDead) + " dead");
        mDeathLossPercentage.setText(decimalFormat.format(percent) + "%");

    }

    @Override
    public void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities) {
        mDrugList = drugEntities;
        new QueryDrugsGivenByPenId(this, mSelectedPen.getPenId()).execute(this);
    }

    @Override
    public void onDrugsGivenLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities) {

        ArrayList<DrugReportsObject> drugReports = new ArrayList<>();

        mLoadingReports.setVisibility(View.GONE);

        for(int i=0; i<drugsGivenEntities.size(); i++){
            DrugsGivenEntity drugsGivenEntity = drugsGivenEntities.get(i);
            int amountGiven = drugsGivenEntity.getAmountGiven();
            String id = drugsGivenEntity.getDrugId();
            if(findAndUpdateDrugReports(id, amountGiven, drugReports) == 0){
                DrugReportsObject drugReportsObject = new DrugReportsObject(id, amountGiven);
                drugReports.add(drugReportsObject);
            }
        }

        if(drugReports.size() == 0){
            mNoDrugReports.setVisibility(View.VISIBLE);
        }

        for(int p=0; p<drugReports.size(); p++){
            final float scale = getResources().getDisplayMetrics().density;
            int pixels16 = (int) (16 * scale + 0.5f);
            int pixels8 = (int) (8 * scale + 0.5f);

            DrugReportsObject drugReportsObject = drugReports.get(p);
            DrugEntity drugEntity = findDrugEntity(drugReportsObject.getDrugId());
            String textToSet = Integer.toString(drugReportsObject.drugAmount) + " ccs of " + drugEntity.getDrugName();

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
                    mSelectedPen.setNotes("");
                    mSelectedPen.setTotalHead(0);
                    mSelectedPen.setCustomerName("");
                    mSelectedPen.setIsActive(0);

                    if(Utility.haveNetworkConnection(PenReportsActivity.this)){
                        Query deleteCowsQuery = mBaseRef.child(CowEntity.COW).orderByChild(CowEntity.PEN_ID).equalTo(penId);
                        deleteCowsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    snapshot.getRef().removeValue();
                                }

                                mBaseRef.child(PenEntity.PEN_OBJECT).child(penId).setValue(mSelectedPen);

                                new UpdatePen(mSelectedPen).execute(PenReportsActivity.this);

                                onPenReset();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else{

                        Utility.setNewDataToUpload(PenReportsActivity.this, true);

                        HoldingPenEntity holdingPenEntity = new HoldingPenEntity();
                        holdingPenEntity.setWhatHappened(Utility.INSERT_UPDATE);
                        holdingPenEntity.setPenId(mSelectedPen.getPenId());
                        holdingPenEntity.setIsActive(mSelectedPen.getIsActive());
                        holdingPenEntity.setPenName(mSelectedPen.getPenName());
                        holdingPenEntity.setCustomerName(mSelectedPen.getCustomerName());
                        holdingPenEntity.setTotalHead(mSelectedPen.getTotalHead());
                        holdingPenEntity.setNotes(mSelectedPen.getNotes());

                        new InsertHoldingPen(holdingPenEntity).execute(PenReportsActivity.this);

                        // TODO: 2/9/2019 add code to delete all the drugs given in this pen 

                    }

                    new UpdatePen(mSelectedPen).execute(PenReportsActivity.this);
                    new DeleteCowsByPenId(mSelectedPen.getPenId()).execute(PenReportsActivity.this);
                    new DeleteDrugsGivenByPenId(mSelectedPen.getPenId()).execute(PenReportsActivity.this);

                    onPenReset();
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

    private void onPenReset(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("event", "deletion");

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private DrugEntity findDrugEntity(String drugId){
        for(int r=0; r<mDrugList.size(); r++){
            DrugEntity drugEntity = mDrugList.get(r);
            if(drugEntity.getDrugId().equals(drugId)){
                return drugEntity;
            }
        }
        return null;
    }

    private int findAndUpdateDrugReports(String drugId, int amountGiven, ArrayList<DrugReportsObject> drugReportsObjects){
        for(int r=0; r<drugReportsObjects.size(); r++){
            DrugReportsObject drugReportsObject = drugReportsObjects.get(r);
            if(drugReportsObject.getDrugId().endsWith(drugId)){
                int currentAmount = drugReportsObject.getDrugAmount();
                int amountToUpdateTo = currentAmount + amountGiven;
                drugReportsObject.setDrugAmount(amountToUpdateTo);
                drugReportsObjects.remove(r);
                drugReportsObjects.add(r, drugReportsObject);
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
