package com.trevorwiebe.trackacow.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
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
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.MedicatedCowsRecyclerViewAdapter;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingPen;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllDrugs;
import com.trevorwiebe.trackacow.dataLoaders.QueryDrugsGivenByPenId;
import com.trevorwiebe.trackacow.dataLoaders.QueryPenById;
import com.trevorwiebe.trackacow.dataLoaders.UpdatePen;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.dataLoaders.QueryMedicatedCowsByPenId;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingPenEntity;
import com.trevorwiebe.trackacow.utils.ItemClickListener;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;

public class MedicatedCowsActivity extends AppCompatActivity implements
        QueryMedicatedCowsByPenId.OnCowsLoaded,
        QueryDrugsGivenByPenId.OnDrugsGivenLoaded,
        QueryAllDrugs.OnAllDrugsLoaded,
        QueryPenById.OnPenByIdReturned {

    private static final String TAG = "MedicatedCowsActivity";

    DatabaseReference mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private ArrayList<CowEntity> mSelectedCows = new ArrayList<>();
    private ArrayList<CowEntity> mTreatedCows = new ArrayList<>();
    private ArrayList<DrugEntity> mDrugList = new ArrayList<>();
    private ArrayList<DrugsGivenEntity> mDrugsGivenList = new ArrayList<>();
    private MedicatedCowsRecyclerViewAdapter mMedicatedCowsRecyclerViewAdapter;
    private PenEntity mSelectedPen;
    private static final int MEDICATE_A_COW_CODE = 743;
    private static final int VIEW_PEN_REPORTS_CODE = 345;
    private boolean mIsActive;
    private boolean shouldShowCouldntFindTag;

    private TextView mNoMedicatedCows;
    private SearchView mSearchView;
    private RecyclerView mMedicatedCows;
    private CardView mResultsNotFound;
    private FloatingActionsMenu mMedicateACowFabMenu;
    private Button mMarkAsActive;
    private ScrollView mPenIdleLayout;
    private TextInputEditText mCustomerName;
    private TextInputEditText mTotalCount;
    private TextInputEditText mNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicated_cows);

        // TODO: 2/16/2019 add context menu and move 'Pen Reports' and the ability to sort by tag number or date
        // TODO: 2/16/2019 make the context menu item disappear when the search bar is open

        mMedicateACowFabMenu = findViewById(R.id.floating_action_btn_menu);
        mNoMedicatedCows = findViewById(R.id.no_medicated_cows_tv);
        mResultsNotFound = findViewById(R.id.result_not_found);
        mMarkAsActive = findViewById(R.id.mark_as_active);
        mPenIdleLayout = findViewById(R.id.pen_idle);
        mCustomerName = findViewById(R.id.customer_name);
        mTotalCount = findViewById(R.id.total_head);
        mNotes = findViewById(R.id.pen_notes);

        mMarkAsActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCustomerName.length() == 0 || mTotalCount.length() == 0){
                    Snackbar.make(view, "Please fill the blanks", Snackbar.LENGTH_LONG).show();
                    return;
                }

                String customerName = mCustomerName.getText().toString();
                int totalHead = Integer.parseInt(mTotalCount.getText().toString());
                String notes = mNotes.getText().toString();

                mSelectedPen.setIsActive(1);
                mSelectedPen.setCustomerName(customerName);
                mSelectedPen.setTotalHead(totalHead);
                mSelectedPen.setNotes(notes);

                if(Utility.haveNetworkConnection(MedicatedCowsActivity.this)) {
                    mBaseRef.child(PenEntity.PEN_OBJECT).child(mSelectedPen.getPenId()).setValue(mSelectedPen);
                }else{

                    Utility.setNewDataToUpload(MedicatedCowsActivity.this, true);

                    HoldingPenEntity holdingPenEntity = new HoldingPenEntity();

                    holdingPenEntity.setNotes(mSelectedPen.getNotes());
                    holdingPenEntity.setTotalHead(mSelectedPen.getTotalHead());
                    holdingPenEntity.setCustomerName(mSelectedPen.getCustomerName());
                    holdingPenEntity.setPenName(mSelectedPen.getPenName());
                    holdingPenEntity.setIsActive(mSelectedPen.getIsActive());
                    holdingPenEntity.setPenId(mSelectedPen.getPenId());
                    holdingPenEntity.setWhatHappened(Utility.INSERT_UPDATE);

                    new InsertHoldingPen(holdingPenEntity).execute(MedicatedCowsActivity.this);

                }

                new UpdatePen(mSelectedPen).execute(MedicatedCowsActivity.this);

                setActive();

                mCustomerName.setText("");
                mTotalCount.setText("");
                mNotes.setText("");

            }
        });

        mMedicatedCows = findViewById(R.id.track_cow_rv);
        mMedicatedCows.setLayoutManager(new LinearLayoutManager(this));
        mMedicatedCowsRecyclerViewAdapter = new MedicatedCowsRecyclerViewAdapter(mTreatedCows, mDrugList, mDrugsGivenList,this);
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
    }

    public void medicateCow(View view){
        mMedicateACowFabMenu.collapse();
        Intent medicateCowIntent = new Intent(MedicatedCowsActivity.this, MedicateACowActivity.class);
        medicateCowIntent.putExtra("penObject", mSelectedPen);
        startActivityForResult(medicateCowIntent, MEDICATE_A_COW_CODE);
    }

    public void markACowDead(View view){
        mMedicateACowFabMenu.collapse();
        Intent markCowDeadIntent = new Intent(MedicatedCowsActivity.this, MarkACowDeadActivity.class);
        markCowDeadIntent.putExtra("penObject", mSelectedPen);
        startActivity(markCowDeadIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSearchView.setQuery("", false);
        mSearchView.setIconified(true);
        mResultsNotFound.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == VIEW_PEN_REPORTS_CODE && resultCode == Activity.RESULT_OK){
            if(data.getStringExtra("event").equals("deletion")){
                mIsActive = false;
                setInActive();
                invalidateOptionsMenu();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String penId;

        if (getIntent().getStringExtra("penEntityId") == null) {
            penId = Utility.getPenId(MedicatedCowsActivity.this);
        }else{
            penId = getIntent().getStringExtra("penEntityId");
        }

        new QueryPenById(penId, this).execute(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.track_cow_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        MenuItem reportsItem = menu.findItem(R.id.menu_reports);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setInputType(InputType.TYPE_CLASS_NUMBER);

        if(mIsActive){
            searchItem.setVisible(true);
            reportsItem.setVisible(true);
        }else{
            searchItem.setVisible(false);
            reportsItem.setVisible(false);
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                if(s.length() >= 1) {
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
                }else{
                    if(shouldShowCouldntFindTag) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_reports){
            Intent penReportsIntent = new Intent(MedicatedCowsActivity.this, PenReportsActivity.class);
            penReportsIntent.putExtra("selectedPenId", mSelectedPen.getPenId());
            startActivityForResult(penReportsIntent, VIEW_PEN_REPORTS_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPenByIdReturned(PenEntity penEntity) {
        mSelectedPen = penEntity;
        mIsActive = mSelectedPen.getIsActive() == 1;

        setTitle("Pen " + mSelectedPen.getPenName());


        if (mIsActive) {
            setActive();
        } else {
            setInActive();
        }
    }

    @Override
    public void onDrugsGivenLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        mDrugsGivenList = drugsGivenEntities;
        new QueryAllDrugs(this).execute(this);
    }

    @Override
    public void onAllDrugsLoaded(ArrayList<DrugEntity> drugEntities) {
        mDrugList = drugEntities;
        new QueryMedicatedCowsByPenId(this, mSelectedPen.getPenId()).execute(this);
    }

    @Override
    public void onCowsLoaded(ArrayList<CowEntity> cowObjectList) {
        mTreatedCows = cowObjectList;
        mSelectedCows = cowObjectList;
        mMedicatedCowsRecyclerViewAdapter.swapData(mTreatedCows, mDrugList, mDrugsGivenList);
        if(mTreatedCows.size() == 0){
            mNoMedicatedCows.setVisibility(View.VISIBLE);
        }else{
            mNoMedicatedCows.setVisibility(View.INVISIBLE);
        }
    }

    private ArrayList<CowEntity> findTags(String inputString){
        ArrayList<CowEntity> listToReturn = new ArrayList<>();
        for(int e=0; e<mTreatedCows.size(); e++){
            CowEntity cowEntity = mTreatedCows.get(e);
            String tag = Integer.toString(cowEntity.getTagNumber());
            if(tag.startsWith(inputString)){
                listToReturn.add(cowEntity);
            }
        }
        return listToReturn;
    }

    private void setActive(){
        mMedicateACowFabMenu.setVisibility(View.VISIBLE);
        new QueryDrugsGivenByPenId(this, mSelectedPen.getPenId()).execute(this);
        mPenIdleLayout.setVisibility(View.GONE);
        mMedicatedCows.setVisibility(View.VISIBLE);
        shouldShowCouldntFindTag = true;
        invalidateOptionsMenu();
        mIsActive = true;
    }

    private void setInActive(){
        mIsActive = false;
        mNoMedicatedCows.setVisibility(View.GONE);
        mMedicateACowFabMenu.setVisibility(View.GONE);
        shouldShowCouldntFindTag = false;
        mMedicatedCows.setVisibility(View.GONE);
        mPenIdleLayout.setVisibility(View.VISIBLE);
        invalidateOptionsMenu();
    }
}
