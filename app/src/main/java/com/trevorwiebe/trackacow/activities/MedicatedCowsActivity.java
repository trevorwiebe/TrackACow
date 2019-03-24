package com.trevorwiebe.trackacow.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingLot;
import com.trevorwiebe.trackacow.dataLoaders.InsertLotEntity;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.QueryDrugsGivenByLotIds;
import com.trevorwiebe.trackacow.dataLoaders.QueryLotsByPenId;
import com.trevorwiebe.trackacow.dataLoaders.QueryMedicatedCowsByLotIds;
import com.trevorwiebe.trackacow.dataLoaders.QueryPenById;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
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
    private static final int MEDICATE_A_COW_CODE = 743;
    private boolean mIsActive = false;
    private boolean shouldShowCouldntFindTag;
    private Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener mDatePicker;

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
    private TextInputEditText mTotalCount;
    private TextInputEditText mDate;
    private TextInputEditText mNotes;

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
        mTotalCount = findViewById(R.id.total_head);
        mDate = findViewById(R.id.lot_date);
        mNotes = findViewById(R.id.pen_notes);

        String todayDate = Utility.convertMillisToDate(System.currentTimeMillis());
        mDate.setText(todayDate);

        mMarkAsActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLotName.length() == 0 || mCustomerName.length() == 0 || mTotalCount.length() == 0) {
                    Snackbar.make(view, "Please fill the blanks", Snackbar.LENGTH_LONG).show();
                    return;
                }

                String lotName = mLotName.getText().toString();
                String customerName = mCustomerName.getText().toString();
                int totalHead = Integer.parseInt(mTotalCount.getText().toString());
                String notes = mNotes.getText().toString();
                long date = mCalendar.getTimeInMillis();

                DatabaseReference lotPushRef = mBaseRef.child(LotEntity.LOT).push();
                String id = lotPushRef.getKey();
                LotEntity lotEntity = new LotEntity(lotName, id, customerName, totalHead, notes, date, mSelectedPen.getPenId());

                if (Utility.haveNetworkConnection(MedicatedCowsActivity.this)) {
                    lotPushRef.setValue(lotEntity);
                } else {

                    Utility.setNewDataToUpload(MedicatedCowsActivity.this, true);

                    HoldingLotEntity holdingLotEntity = new HoldingLotEntity(lotName, id, customerName, totalHead, notes, date, mSelectedPen.getPenId(), Constants.INSERT_UPDATE);

                    new InsertHoldingLot(holdingLotEntity).execute(MedicatedCowsActivity.this);

                }

                new InsertLotEntity(lotEntity).execute(MedicatedCowsActivity.this);

                mNoMedicatedCows.setVisibility(View.VISIBLE);
                setActive();

                android.support.v7.app.ActionBar ab = getSupportActionBar();
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

        mDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mDate.setText(Utility.convertMillisToDate(mCalendar.getTimeInMillis()));
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

        if(mIsActive){
            searchItem.setVisible(true);
        }else{
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
        String lotNameTitle = "";
        mLotIds.clear();
        for (int t = 0; t < lotEntities.size(); t++) {
            LotEntity lotEntity = lotEntities.get(t);
            mLotIds.add(lotEntity.getLotId());
            String lotName = lotEntity.getLotName();
            lotNameTitle = lotNameTitle + "  " + lotName;
        }
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (lotNameTitle.length() == 0) {
            setInActive();
        } else {
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
        startActivityForResult(medicateCowIntent, MEDICATE_A_COW_CODE);
    }

    public void markACowDead(View view) {
        mMedicateACowFabMenu.collapse();
        Intent markCowDeadIntent = new Intent(MedicatedCowsActivity.this, MarkACowDeadActivity.class);
        markCowDeadIntent.putExtra("penId", mSelectedPen.getPenId());
        startActivity(markCowDeadIntent);
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
