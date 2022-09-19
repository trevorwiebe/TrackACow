package com.trevorwiebe.trackacow.activities;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.ViewCattleListAdapter;
import com.trevorwiebe.trackacow.dataLoaders.main.lot.DeleteLotEntity;
import com.trevorwiebe.trackacow.dataLoaders.main.archivedLot.InsertArchivedLotEntity;
import com.trevorwiebe.trackacow.dataLoaders.cache.holdingArchivedLot.InsertHoldingArchivedLot;
import com.trevorwiebe.trackacow.dataLoaders.cache.holdingLot.InsertHoldingLot;
import com.trevorwiebe.trackacow.dataLoaders.main.drug.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.main.archivedLot.QueryArchivedLotsByLotId;
import com.trevorwiebe.trackacow.dataLoaders.main.cow.QueryDeadCowsByLotIds;
import com.trevorwiebe.trackacow.dataLoaders.main.drugsGiven.QueryDrugsGivenByLotIds;
import com.trevorwiebe.trackacow.dataLoaders.main.feed.QueryFeedsByLotId;
import com.trevorwiebe.trackacow.dataLoaders.main.load.QueryLoadsByLotId;
import com.trevorwiebe.trackacow.dataLoaders.main.lot.QueryLotByLotId;
import com.trevorwiebe.trackacow.db.entities.ArchivedLotEntity;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.FeedEntity;
import com.trevorwiebe.trackacow.db.entities.LoadEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingArchivedLotEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLotEntity;
import com.trevorwiebe.trackacow.objects.DrugReportsObject;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.ItemClickListener;
import com.trevorwiebe.trackacow.utils.Utility;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LotReportActivity extends AppCompatActivity implements
        QueryAllDrugs.OnAllDrugsLoaded,
        QueryDrugsGivenByLotIds.OnDrugsGivenByLotIdLoaded,
        QueryDeadCowsByLotIds.OnDeadCowsLoaded,
        QueryLotByLotId.OnLotByLotIdLoaded,
        QueryArchivedLotsByLotId.OnArchivedLotLoaded,
        QueryFeedsByLotId.OnFeedsByLotIdReturned,
        QueryLoadsByLotId.OnLoadsByLotIdLoaded {

    private static final String TAG = "LotReportActivity";

    private ArrayList<DrugEntity> mDrugList = new ArrayList<>();
    private static final int EDIT_PEN_CODE = 747;
    private static final int EDIT_LOAD_CODE = 472;
    private int mTotalHeadInt;
    private String mLotId;
    private LotEntity mSelectedLotEntity;
    private int reportType;
    private NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
    private ViewCattleListAdapter cattleListAdapter = new ViewCattleListAdapter();
    private int mCurrentHeadDays;
    private ArrayList<LoadEntity> mLoadEntities = new ArrayList<>();
    private Integer mReportType;

    private TextView mCustomerName;
    private TextView mTotalHead;
    private TextView mCurrentHead;
    private TextView mDate;
    private TextView mNotes;
    private TextView mTotalDeathLoss;
    private TextView mDeathLossPercentage;
    private TextView mFeedReports;
    private LinearLayout mDrugsUsedLayout;
    private ProgressBar mLoadingReports;
    private TextView mNoDrugReports;
    private Button mDrugReports;
    private TextView mHeadDays;
    private TextView mNoCattleReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lot_reports);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
            }
        }

        String lotId = intent.getStringExtra("lotId");
        reportType = intent.getIntExtra("reportType", 0);

        if (reportType == Constants.LOT) {
            new QueryLotByLotId(lotId, this).execute(this);
            mReportType = Constants.LOT;
        } else if (reportType == Constants.ARCHIVE) {
            new QueryArchivedLotsByLotId(lotId, this).execute(this);
            mReportType = Constants.ARCHIVE;
        }

        mLoadingReports = findViewById(R.id.loading_reports);
        mNoDrugReports = findViewById(R.id.no_drug_reports);
        Button resetLotBtn = findViewById(R.id.archive_this_lot);
        resetLotBtn.setOnClickListener(archiveLotListener);

        if (reportType == Constants.ARCHIVE) {
            resetLotBtn.setVisibility(View.GONE);
        }

        mDrugsUsedLayout = findViewById(R.id.drugs_used_layout);
        mTotalDeathLoss = findViewById(R.id.reports_death_loss);
        mDeathLossPercentage = findViewById(R.id.reports_death_loss_percentage);
        mFeedReports = findViewById(R.id.feed_reports);
        mCustomerName = findViewById(R.id.reports_customer_name);
        mTotalHead = findViewById(R.id.reports_total_head);
        mCurrentHead = findViewById(R.id.reports_current_head);
        mDate = findViewById(R.id.reports_date);
        mNotes = findViewById(R.id.reports_notes);
        mHeadDays = findViewById(R.id.reports_head_days);
        mDrugReports = findViewById(R.id.drug_reports_button);
        RecyclerView viewLoadsOfCattle = findViewById(R.id.view_loads_of_cattle);
        mNoCattleReceived = findViewById(R.id.no_cattle_received_tv);
        viewLoadsOfCattle.setLayoutManager(new LinearLayoutManager(this));
        viewLoadsOfCattle.setAdapter(cattleListAdapter);
        viewLoadsOfCattle.addOnItemTouchListener(new ItemClickListener(this, viewLoadsOfCattle, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (reportType == Constants.LOT) {
                    LoadEntity loadEntity = mLoadEntities.get(position);
                    String loadId = loadEntity.getLoadId();
                    Intent editLoadIntent = new Intent(LotReportActivity.this, EditLoadActivity.class);
                    editLoadIntent.putExtra("loadId", loadId);
                    startActivityForResult(editLoadIntent, EDIT_LOAD_CODE);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        mDrugReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent drugReports = new Intent(LotReportActivity.this, DrugsGivenReportActivity.class);
                drugReports.putExtra("lotId", mLotId);
                drugReports.putExtra("reportType", mReportType);
                startActivity(drugReports);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pen_reports_menu, menu);
        MenuItem item = menu.findItem(R.id.reports_action_edit);
        if (reportType == Constants.ARCHIVE) {
            item.setVisible(false);
        }
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
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == EDIT_PEN_CODE || requestCode == EDIT_LOAD_CODE) && resultCode == RESULT_OK) {
            new QueryLotByLotId(mSelectedLotEntity.getLotId(), LotReportActivity.this).execute(LotReportActivity.this);
        }
    }

    @Override
    public void onLotByLotIdLoaded(LotEntity lotEntity) {
        mSelectedLotEntity = lotEntity;
        lotEntityLoaded(mSelectedLotEntity);
    }

    @Override
    public void onArchivedLotLoaded(ArchivedLotEntity archivedLotEntity) {
        mSelectedLotEntity = new LotEntity(archivedLotEntity);
        lotEntityLoaded(mSelectedLotEntity);
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
            int pixels4 = (int) (4 * scale + 0.5f);

            DrugReportsObject drugReportsObject = drugReports.get(p);
            DrugEntity drugEntity = findDrugEntity(drugReportsObject.getDrugId());
            String drugName;
            if (drugEntity != null) {
                drugName = drugEntity.getDrugName();
            } else {
                drugName = "[drug_unavailable]";
            }

            String textToSet = drugReportsObject.getDrugAmount() + " units of " + drugName;

            TextView textView = new TextView(LotReportActivity.this);
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            textViewParams.setMargins(pixels16, pixels4, pixels16, pixels4);
            textView.setTextSize(16f);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            textView.setLayoutParams(textViewParams);

            textView.setText(textToSet);

            mDrugsUsedLayout.addView(textView);
        }

    }

    @Override
    public void onFeedsByLotIdReturned(ArrayList<FeedEntity> feedEntities) {
        int totalAmountFed = 0;
        for (int y = 0; y < feedEntities.size(); y++) {
            FeedEntity feedEntity = feedEntities.get(y);
            int amountFed = feedEntity.getFeed();
            totalAmountFed = totalAmountFed + amountFed;
        }
        String amountFedStr = numberFormat.format(totalAmountFed);
        String amountFedText = amountFedStr + " lbs";
        mFeedReports.setText(amountFedText);
    }

    @Override
    public void onLoadsByLotIdLoaded(ArrayList<LoadEntity> loadEntities) {

        if (loadEntities.size() == 0) {
            mNoCattleReceived.setVisibility(View.VISIBLE);
        } else {
            mNoCattleReceived.setVisibility(View.GONE);
        }

        mLoadEntities = loadEntities;
        cattleListAdapter.setData(mLoadEntities);
        mTotalHeadInt = 0;
        mCurrentHeadDays = 0;

        for (int a = 0; a < mLoadEntities.size(); a++) {
            LoadEntity loadEntity = mLoadEntities.get(a);
            int numberOfHead = loadEntity.getNumberOfHead();
            mTotalHeadInt = mTotalHeadInt + numberOfHead;

            int daysHadLoad = getDaysSinceFromMillis(loadEntity.getDate());
            int thisLoadsHeadDays = daysHadLoad * numberOfHead;
            mCurrentHeadDays = mCurrentHeadDays + thisLoadsHeadDays;
        }

        String totalHeadStr = numberFormat.format(mTotalHeadInt);
        mTotalHead.setText(totalHeadStr);

        ArrayList<String> lotIds = new ArrayList<>();
        lotIds.add(mLotId);

        new QueryDeadCowsByLotIds(this, lotIds).execute(this);

    }

    @Override
    public void onDeadCowsLoaded(ArrayList<CowEntity> cowEntities) {

        int numberOfHeadDaysToSubtract = 0;
        for (int r = 0; r < cowEntities.size(); r++) {
            CowEntity cowEntity = cowEntities.get(r);
            int daysSinceDied = getDaysSinceFromMillis(cowEntity.getDate());
            numberOfHeadDaysToSubtract = numberOfHeadDaysToSubtract + daysSinceDied;
        }

        int currentHeadDays = mCurrentHeadDays - numberOfHeadDaysToSubtract;

        if (currentHeadDays < 0) {
            currentHeadDays = 0;
        }
        String headDaysStr = numberFormat.format(currentHeadDays);
        mHeadDays.setText(headDaysStr);

        int numberDead = cowEntities.size();

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        float percent = (numberDead * 100.f) / mTotalHeadInt;

        String deadText = numberFormat.format(numberDead) + " dead";
        String percentDeadText = decimalFormat.format(percent) + "%";
        mTotalDeathLoss.setText(deadText);
        mDeathLossPercentage.setText(percentDeadText);

        int currentHead = mTotalHeadInt - numberDead;
        if (currentHead < 0) {
            currentHead = 0;
        }
        String currentHeadStr = numberFormat.format(currentHead);
        mCurrentHead.setText(currentHeadStr);

    }

    private View.OnClickListener archiveLotListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            AlertDialog.Builder lotArchived = new AlertDialog.Builder(LotReportActivity.this);
            lotArchived.setTitle("Are you sure you want to archive lot?");
            lotArchived.setMessage("This action cannot be undone.  You will not be able to edit these reports after they are archived.  You will be able to view this lot's reports under Archives.");
            lotArchived.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ArchivedLotEntity archivedLotEntity = new ArchivedLotEntity(mSelectedLotEntity, System.currentTimeMillis());

                    if (Utility.haveNetworkConnection(LotReportActivity.this)) {
                        DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        // delete the lot entity
                        baseRef.child(LotEntity.LOT).child(mSelectedLotEntity.getLotId()).removeValue();

                        // push archived lot to the cloud;
                        baseRef.child(ArchivedLotEntity.ARCHIVED_LOT).child(archivedLotEntity.getLotId()).setValue(archivedLotEntity);

                    } else {

                        HoldingLotEntity holdingLotEntity = new HoldingLotEntity(mSelectedLotEntity, Constants.DELETE);
                        new InsertHoldingLot(holdingLotEntity).execute(LotReportActivity.this);

                        HoldingArchivedLotEntity holdingArchivedLotEntity = new HoldingArchivedLotEntity(archivedLotEntity, Constants.INSERT_UPDATE);
                        new InsertHoldingArchivedLot(holdingArchivedLotEntity).execute(LotReportActivity.this);

                    }

                    new DeleteLotEntity(mSelectedLotEntity.getLotId()).execute(LotReportActivity.this);
                    new InsertArchivedLotEntity(archivedLotEntity).execute(LotReportActivity.this);

                    finish();
                }
            });
            lotArchived.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            lotArchived.show();
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
            String notes = lotEntity.getNotes();
            String date = Utility.convertMillisToDate(lotEntity.getDate());
            mCustomerName.setText(customerName);
            mDate.setText(date);
            mNotes.setText(notes);
        }
    }

    private void lotEntityLoaded(LotEntity lotEntity) {

        updateUIWithPenInfo(lotEntity);

        new QueryAllDrugs(this).execute(this);

        mLotId = lotEntity.getLotId();

        new QueryFeedsByLotId(mLotId, this).execute(this);
        new QueryLoadsByLotId(mLotId, this).execute(this);
    }

    private int getDaysSinceFromMillis(long startDate) {
        long millisInOnDay = TimeUnit.DAYS.toMillis(1);
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - startDate;
        if (timeElapsed < 0) {
            return 0;
        } else if (millisInOnDay >= timeElapsed) {
            return 1;
        } else {
            long daysElapsed = timeElapsed / millisInOnDay;
            return (int) daysElapsed + 1;
        }
    }

}
