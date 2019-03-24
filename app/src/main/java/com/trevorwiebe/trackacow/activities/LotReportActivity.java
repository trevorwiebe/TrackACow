package com.trevorwiebe.trackacow.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.QueryDeadCowsByLotIds;
import com.trevorwiebe.trackacow.dataLoaders.QueryDrugsGivenByLotIds;
import com.trevorwiebe.trackacow.dataLoaders.QueryLotByLotId;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class LotReportActivity extends AppCompatActivity implements
        QueryAllDrugs.OnAllDrugsLoaded,
        QueryDrugsGivenByLotIds.OnDrugsGivenByLotIdLoaded,
        QueryDeadCowsByLotIds.OnDeadCowsLoaded,
        QueryLotByLotId.OnLotByLotIdLoaded {
    private static final String TAG = "LotReportActivity";

    private DatabaseReference mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private ArrayList<DrugEntity> mDrugList = new ArrayList<>();
    private ArrayList<DrugsGivenEntity> mDrugGivenList = new ArrayList<>();
    private static final int EDIT_PEN_CODE = 747;
    private LotEntity mSelectedLotEntity;

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

        String lotId = getIntent().getStringExtra("lotId");

        new QueryLotByLotId(lotId, this).execute(this);

        mLoadingReports = findViewById(R.id.loading_reports);
        mNoDrugReports = findViewById(R.id.no_drug_reports);
        mResetPenBtn = findViewById(R.id.archive_this_lot);
        mResetPenBtn.setOnClickListener(resetPenListener);

        mDrugsUsedLayout = findViewById(R.id.drugs_used_layout);
        mTotalDeathLoss = findViewById(R.id.reports_death_loss);
        mDeathLossPercentage = findViewById(R.id.reports_death_loss_percentage);
        mCustomerName = findViewById(R.id.reports_customer_name);
        mTotalHead = findViewById(R.id.reports_total_head);
        mNotes = findViewById(R.id.reports_notes);

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
        if (id == R.id.reports_action_edit) {
            Intent editLotIntent = new Intent(LotReportActivity.this, EditLotActivity.class);
            editLotIntent.putExtra("lotId", mSelectedLotEntity.getLotId());
            startActivityForResult(editLotIntent, EDIT_PEN_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == EDIT_PEN_CODE && resultCode == RESULT_OK) {
            new QueryLotByLotId(mSelectedLotEntity.getLotId(), LotReportActivity.this).execute(LotReportActivity.this);
        }
    }

    @Override
    public void onLotByLotIdLoaded(LotEntity lotEntity) {
        mSelectedLotEntity = lotEntity;

        updateUIWithPenInfo(lotEntity);

        new QueryAllDrugs(this).execute(this);

        ArrayList<String> lotIds = new ArrayList<>();
        lotIds.add(mSelectedLotEntity.getLotId());

        new QueryDeadCowsByLotIds(this, lotIds).execute(this);
    }

    @Override
    public void onDeadCowsLoaded(ArrayList<CowEntity> cowEntities) {
        int numberDead = cowEntities.size();
        int total = mSelectedLotEntity.getTotalHead();

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        float percent = (numberDead * 100.f) / total;

        mTotalDeathLoss.setText(Integer.toString(numberDead) + " dead");
        mDeathLossPercentage.setText(decimalFormat.format(percent) + "%");

    }

    @Override
    public void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities) {
        mDrugList = drugEntities;

        ArrayList<String> lotIds = new ArrayList<>();
        lotIds.add(mSelectedLotEntity.getLotId());

        new QueryDrugsGivenByLotIds(this, lotIds).execute(this);
    }

    @Override
    public void onDrugsGivenByLotIdLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities) {

        mDrugsUsedLayout.removeAllViews();

        ArrayList<DrugReportsObject> drugReports = new ArrayList<>();
        mDrugGivenList = drugsGivenEntities;

        mLoadingReports.setVisibility(View.GONE);

        for (int i = 0; i < drugsGivenEntities.size(); i++) {
            DrugsGivenEntity drugsGivenEntity = drugsGivenEntities.get(i);
            int amountGiven = drugsGivenEntity.getAmountGiven();
            String id = drugsGivenEntity.getDrugId();
            if (findAndUpdateDrugReports(id, amountGiven, drugReports) == 0) {
                DrugReportsObject drugReportsObject = new DrugReportsObject(id, amountGiven);
                drugReports.add(drugReportsObject);
            }
        }

        if (drugReports.size() == 0) {
            mNoDrugReports.setVisibility(View.VISIBLE);
        }

        for (int p = 0; p < drugReports.size(); p++) {
            final float scale = getResources().getDisplayMetrics().density;
            int pixels16 = (int) (16 * scale + 0.5f);
            int pixels8 = (int) (8 * scale + 0.5f);

            DrugReportsObject drugReportsObject = drugReports.get(p);
            DrugEntity drugEntity = findDrugEntity(drugReportsObject.getDrugId());
            String drugName;
            if (drugEntity != null) {
                drugName = drugEntity.getDrugName();
            } else {
                drugName = "[drug_unavailable]";
            }

            String textToSet = Integer.toString(drugReportsObject.drugAmount) + " ccs of " + drugName;

            TextView textView = new TextView(LotReportActivity.this);
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

        }
    };

    private DrugEntity findDrugEntity(String drugId) {
        for (int r = 0; r < mDrugList.size(); r++) {
            DrugEntity drugEntity = mDrugList.get(r);
            if (drugEntity.getDrugId().equals(drugId)) {
                return drugEntity;
            }
        }
        return null;
    }

    private int findAndUpdateDrugReports(String drugId, int amountGiven, ArrayList<DrugReportsObject> drugReportsObjects) {
        for (int r = 0; r < drugReportsObjects.size(); r++) {
            DrugReportsObject drugReportsObject = drugReportsObjects.get(r);
            if (drugReportsObject.getDrugId().endsWith(drugId)) {
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

    private void updateUIWithPenInfo(LotEntity lotEntity) {
        if (lotEntity != null) {
            setTitle(lotEntity.getLotName());
            String customerName = lotEntity.getCustomerName();
            String totalHead = Integer.toString(lotEntity.getTotalHead());
            String notes = lotEntity.getNotes();
            mCustomerName.setText(customerName);
            mTotalHead.setText(totalHead);
            mNotes.setText(notes);
        }
    }

    @Keep
    private class DrugReportsObject {

        private String drugId;
        private int drugAmount;

        public DrugReportsObject(String drugId, int drugAmount) {
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
