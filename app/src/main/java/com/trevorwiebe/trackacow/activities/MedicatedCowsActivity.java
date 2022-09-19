package com.trevorwiebe.trackacow.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.MedicatedCowsRecyclerViewAdapter;
import com.trevorwiebe.trackacow.dataLoaders.cache.holdingLoad.InsertHoldingLoad;
import com.trevorwiebe.trackacow.dataLoaders.cache.holdingLot.InsertHoldingLot;
import com.trevorwiebe.trackacow.dataLoaders.main.load.InsertLoadEntity;
import com.trevorwiebe.trackacow.dataLoaders.main.lot.InsertLotEntity;
import com.trevorwiebe.trackacow.dataLoaders.main.drug.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.main.drugsGiven.QueryDrugsGivenByLotIds;
import com.trevorwiebe.trackacow.dataLoaders.main.lot.QueryLotsByPenId;
import com.trevorwiebe.trackacow.dataLoaders.main.cow.QueryMedicatedCowsByLotIds;
import com.trevorwiebe.trackacow.dataLoaders.main.pen.QueryPenById;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.LoadEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLoadEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLotEntity;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.ItemClickListener;
import com.trevorwiebe.trackacow.utils.SyncDatabase;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;

public class MedicatedCowsActivity extends AppCompatActivity implements
        QueryPenById.OnPenByIdReturned,
        QueryAllDrugs.OnAllDrugsLoaded,
        QueryLotsByPenId.OnLotsByPenIdLoaded,
        QueryDrugsGivenByLotIds.OnDrugsGivenByLotIdLoaded,
        QueryMedicatedCowsByLotIds.OnCowsByLotIdLoaded,
        SyncDatabase.OnDatabaseSynced {

    private static final String TAG = "MedicatedCowsActivity";

    DatabaseReference mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private ArrayList<CowEntity> mSelectedCows = new ArrayList<>();
    private ArrayList<CowEntity> mTreatedCows = new ArrayList<>();
    private ArrayList<DrugEntity> mDrugList = new ArrayList<>();
    private ArrayList<DrugsGivenEntity> mDrugsGivenList = new ArrayList<>();
    private ArrayList<String> mLotIds = new ArrayList<>();
    private MedicatedCowsRecyclerViewAdapter mMedicatedCowsRecyclerViewAdapter;
    private PenEntity mSelectedPen;
    private boolean mIsActive = false;
    private boolean shouldShowCouldntFindTag;
    private Calendar mCalendar = Calendar.getInstance();
    private Calendar mLoadCalender = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener mDatePicker;
    private DatePickerDialog.OnDateSetListener mLoadDatePicker;

    private TextView mNoMedicatedCows;
    private SearchView mSearchView;
    private SwipeRefreshLayout mMedicatedCowSwipeToRefresh;
    private RecyclerView mMedicatedCows;
    private CardView mResultsNotFound;
    private FloatingActionsMenu mMedicateACowFabMenu;
    private Button mMarkAsActive;
    private ScrollView mPenIdleLayout;

    private TextInputEditText mLotName;
    private TextInputEditText mCustomerName;
    private TextInputEditText mDate;
    private TextInputEditText mNotes;

    private TextInputEditText mTotalCount;
    private TextInputEditText mLoadDate;
    private TextInputEditText mLoadMemo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicated_cows);

        mMedicateACowFabMenu = findViewById(R.id.floating_action_btn_menu);
        mNoMedicatedCows = findViewById(R.id.no_medicated_cows_tv);
        mResultsNotFound = findViewById(R.id.result_not_found);
        mMarkAsActive = findViewById(R.id.mark_as_active);
        mPenIdleLayout = findViewById(R.id.pen_idle);

        mLotName = findViewById(R.id.lot_name);
        mCustomerName = findViewById(R.id.customer_name);
        mDate = findViewById(R.id.lot_date);
        mNotes = findViewById(R.id.lot_memo);

        mTotalCount = findViewById(R.id.first_load_head_count);
        mLoadDate = findViewById(R.id.first_load_date);
        mLoadMemo = findViewById(R.id.first_load_memo);

        String todayDate = Utility.convertMillisToDate(System.currentTimeMillis());
        mDate.setText(todayDate);
        mLoadDate.setText(todayDate);

        mMarkAsActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean shouldSave = true;
                if (mLotName.length() == 0){
                    mLotName.setError("Please fill this blank");
                    shouldSave = false;
                }

                if (mCustomerName.length() == 0){
                    mCustomerName.setError("Please fill this blank");
                    shouldSave = false;
                }

                if (mTotalCount.length() == 0) {
                    mTotalCount.setError("Please fill this blank");
                    shouldSave = false;
                }

                if(!shouldSave) return;

                String lotName = mLotName.getText().toString();
                String customerName = mCustomerName.getText().toString();
                int totalHead = Integer.parseInt(mTotalCount.getText().toString());
                String notes = mNotes.getText().toString();
                long date = mCalendar.getTimeInMillis();

                DatabaseReference lotPushRef = mBaseRef.child(LotEntity.LOT).push();
                String id = lotPushRef.getKey();
                LotEntity lotEntity = new LotEntity(lotName, id, customerName, notes, date, mSelectedPen.getPenId());

                String loadDescription = mLoadMemo.getText().toString();
                DatabaseReference loadPushRef = mBaseRef.child(LoadEntity.LOAD).push();
                String loadId = loadPushRef.getKey();
                LoadEntity loadEntity = new LoadEntity(totalHead, date, loadDescription, id, loadId);

                if (Utility.haveNetworkConnection(MedicatedCowsActivity.this)) {
                    lotPushRef.setValue(lotEntity);
                    loadPushRef.setValue(loadEntity);
                } else {

                    Utility.setNewDataToUpload(MedicatedCowsActivity.this, true);

                    HoldingLotEntity holdingLotEntity = new HoldingLotEntity(lotEntity, Constants.INSERT_UPDATE);
                    new InsertHoldingLot(holdingLotEntity).execute(MedicatedCowsActivity.this);

                    HoldingLoadEntity holdingLoadEntity = new HoldingLoadEntity(loadEntity, Constants.INSERT_UPDATE);
                    new InsertHoldingLoad(holdingLoadEntity).execute(MedicatedCowsActivity.this);
                }

                new InsertLotEntity(lotEntity).execute(MedicatedCowsActivity.this);
                new InsertLoadEntity(loadEntity).execute(MedicatedCowsActivity.this);

                mNoMedicatedCows.setVisibility(View.VISIBLE);
                setActive();

                androidx.appcompat.app.ActionBar ab = getSupportActionBar();
                if (ab != null) {
                    ab.setSubtitle(lotName);
                }

                mLotName.setText("");
                mCustomerName.setText("");
                mTotalCount.setText("");
                mNotes.setText("");

            }
        });

        mMedicatedCowSwipeToRefresh = findViewById(R.id.medicated_cow_swipe_to_refresh);
        mMedicatedCows = findViewById(R.id.track_cow_rv);
        mMedicatedCows.setLayoutManager(new LinearLayoutManager(this));
        mMedicatedCowsRecyclerViewAdapter = new MedicatedCowsRecyclerViewAdapter(mTreatedCows, mDrugList, mDrugsGivenList, this);
        mMedicatedCows.setAdapter(mMedicatedCowsRecyclerViewAdapter);

        mMedicatedCows.addOnItemTouchListener(new ItemClickListener(this, mMedicatedCows, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String cowEntityId = mSelectedCows.get(position).getCowId();
                Utility.setCowId(MedicatedCowsActivity.this, cowEntityId);
                Intent editCowIntent = new Intent(MedicatedCowsActivity.this, EditMedicatedCowActivity.class);
                editCowIntent.putExtra("cowEntityId", cowEntityId);
                startActivity(editCowIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MedicatedCowsActivity.this,
                        mDatePicker,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
        mLoadDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MedicatedCowsActivity.this,
                        mLoadDatePicker,
                        mLoadCalender.get(Calendar.YEAR),
                        mLoadCalender.get(Calendar.MONTH),
                        mLoadCalender.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        mDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mDate.setText(Utility.convertMillisToDate(mCalendar.getTimeInMillis()));
            }
        };
        mLoadDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mLoadCalender.set(Calendar.YEAR, year);
                mLoadCalender.set(Calendar.MONTH, month);
                mLoadCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mLoadDate.setText(Utility.convertMillisToDate(mLoadCalender.getTimeInMillis()));
            }
        };

        mMedicatedCowSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SyncDatabase(MedicatedCowsActivity.this, MedicatedCowsActivity.this).beginSync();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSearchView.setQuery("", false);
        mSearchView.setIconified(true);
        mResultsNotFound.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String penId;

        if (getIntent().getStringExtra("penEntityId") == null) {
            penId = Utility.getPenId(MedicatedCowsActivity.this);
        } else {
            penId = getIntent().getStringExtra("penEntityId");
        }

        new QueryPenById(penId, this).execute(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.track_cow_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setInputType(InputType.TYPE_CLASS_NUMBER);

        if (mIsActive) {
            searchItem.setVisible(true);
        } else {
            searchItem.setVisible(false);
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() >= 1) {
                    mSelectedCows = findTags(s);
                    if (mSelectedCows.size() == 0 && shouldShowCouldntFindTag) {
                        mResultsNotFound.setVisibility(View.VISIBLE);
                        mMedicateACowFabMenu.setVisibility(View.INVISIBLE);
                    } else {
                        mMedicateACowFabMenu.setVisibility(View.VISIBLE);
                        mResultsNotFound.setVisibility(View.INVISIBLE);
                    }
                    shouldShowCouldntFindTag = true;
                    mMedicatedCowsRecyclerViewAdapter.swapData(mSelectedCows, mDrugList, mDrugsGivenList);
                } else {
                    if (shouldShowCouldntFindTag) {
                        mMedicateACowFabMenu.setVisibility(View.VISIBLE);
                    }
                    mResultsNotFound.setVisibility(View.INVISIBLE);
                    mSelectedCows = mTreatedCows;
                    mMedicatedCowsRecyclerViewAdapter.swapData(mTreatedCows, mDrugList, mDrugsGivenList);
                }
                return false;
            }
        });
        return true;
    }

    ActivityResultLauncher<Intent> addCattleResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Snackbar.make(mMedicatedCows, "Cattle added successfully", Snackbar.LENGTH_LONG).show();
                    }else{
                        Snackbar.make(mMedicatedCows, "An error occurred", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
    );

    private void openAddCattleActivityForResult(){
        mMedicateACowFabMenu.collapse();
        Intent addLoadOfCattle = new Intent(MedicatedCowsActivity.this, AddLoadOfCattleActivity.class);
        addLoadOfCattle.putExtra("lotId", mLotIds.get(0));
        addCattleResultLauncher.launch(addLoadOfCattle);
    }

    /*
    Database return callbacks
    */
    @Override
    public void onPenByIdReturned(PenEntity penEntity) {
        mSelectedPen = penEntity;
        String activityTitle = "Pen: " + mSelectedPen.getPenName();
        setTitle(activityTitle);

        new QueryLotsByPenId(mSelectedPen.getPenId(), MedicatedCowsActivity.this).execute(MedicatedCowsActivity.this);
    }

    @Override
    public void onLotsByPenIdLoaded(ArrayList<LotEntity> lotEntities) {
        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        if (lotEntities.size() == 0) {
            setInActive();
        } else {
            LotEntity lotEntity = lotEntities.get(0);
            mLotIds = new ArrayList<>();
            mLotIds.add(lotEntity.getLotId());
            String lotNameTitle = lotEntity.getLotName();
            ab.setSubtitle(lotNameTitle);
            setActive();
        }

        new QueryAllDrugs(MedicatedCowsActivity.this).execute(MedicatedCowsActivity.this);
    }

    @Override
    public void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities) {
        mDrugList = drugEntities;
        new QueryDrugsGivenByLotIds(MedicatedCowsActivity.this, mLotIds).execute(MedicatedCowsActivity.this);
    }

    @Override
    public void onDrugsGivenByLotIdLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        mDrugsGivenList = drugsGivenEntities;

        new QueryMedicatedCowsByLotIds(MedicatedCowsActivity.this, mLotIds).execute(MedicatedCowsActivity.this);
    }

    @Override
    public void onCowsByLotIdLoaded(ArrayList<CowEntity> cowObjectList) {
        mTreatedCows = cowObjectList;
        mSelectedCows = cowObjectList;

        if (cowObjectList.size() == 0 && mIsActive) {
            mNoMedicatedCows.setVisibility(View.VISIBLE);
        } else {
            mNoMedicatedCows.setVisibility(View.INVISIBLE);
        }

        mMedicatedCowsRecyclerViewAdapter.swapData(mTreatedCows, mDrugList, mDrugsGivenList);

    }


    /*
    private methods
     */
    public void medicateCow(View view) {
        mMedicateACowFabMenu.collapse();
        Intent medicateCowIntent = new Intent(MedicatedCowsActivity.this, MedicateACowActivity.class);
        medicateCowIntent.putExtra("penId", mSelectedPen.getPenId());
        startActivity(medicateCowIntent);
    }

    public void markACowDead(View view) {
        mMedicateACowFabMenu.collapse();
        Intent markCowDeadIntent = new Intent(MedicatedCowsActivity.this, MarkACowDeadActivity.class);
        markCowDeadIntent.putExtra("penId", mSelectedPen.getPenId());
        startActivity(markCowDeadIntent);
    }

    public void addCattle(View view) {
        openAddCattleActivityForResult();
    }


    private ArrayList<CowEntity> findTags(String inputString) {
        ArrayList<CowEntity> listToReturn = new ArrayList<>();
        for (int e = 0; e < mTreatedCows.size(); e++) {
            CowEntity cowEntity = mTreatedCows.get(e);
            String tag = Integer.toString(cowEntity.getTagNumber());
            if (tag.startsWith(inputString)) {
                listToReturn.add(cowEntity);
            }
        }
        return listToReturn;
    }

    private void setActive() {
        mMedicateACowFabMenu.setVisibility(View.VISIBLE);
        mPenIdleLayout.setVisibility(View.GONE);
        mMedicatedCows.setVisibility(View.VISIBLE);
        shouldShowCouldntFindTag = true;
        mIsActive = true;
        invalidateOptionsMenu();
    }

    private void setInActive() {
        mIsActive = false;
        mNoMedicatedCows.setVisibility(View.GONE);
        mMedicateACowFabMenu.setVisibility(View.GONE);
        shouldShowCouldntFindTag = false;
        mMedicatedCows.setVisibility(View.GONE);
        mPenIdleLayout.setVisibility(View.VISIBLE);
        invalidateOptionsMenu();
    }

    @Override
    public void onDatabaseSynced(int resultCode) {
        if (resultCode != Constants.SUCCESS) {
            Toast.makeText(this, "Failed to sync database", Toast.LENGTH_SHORT).show();
        }
        mMedicatedCowSwipeToRefresh.setRefreshing(false);
    }
}
