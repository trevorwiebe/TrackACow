package com.trevorwiebe.trackacow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.adapters.MedicatedCowsRecyclerViewAdapter;
import com.trevorwiebe.trackacow.objects.CowObject;
import com.trevorwiebe.trackacow.objects.DrugObject;
import com.trevorwiebe.trackacow.objects.PenObject;

import java.util.ArrayList;

public class MedicatedCowsActivity extends AppCompatActivity {

    private static final String TAG = "MedicatedCowsActivity";

    private DatabaseReference mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private Query mTrackCow;
    private DatabaseReference mDrugRef;
    private ValueEventListener mTrackCowListener;
    private ValueEventListener mDrugListener;
    private ArrayList<CowObject> mTreatedCows = new ArrayList<>();
    private ArrayList<DrugObject> mDrugList = new ArrayList<>();
    private MedicatedCowsRecyclerViewAdapter mMedicatedCowsRecyclerViewAdapter;
    private PenObject mSelectedPen;
    private static final int MEDICATE_A_COW_CODE = 743;
    private static final int VIEW_PEN_REPORTS_CODE = 345;
    private boolean mIsActive;
    private boolean shouldShowCouldntFindTag;

    private ProgressBar mLoadMedicatedCows;
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

        // TODO: 1/26/2019 add the ability to edit medicated cow entries

        mSelectedPen = getIntent().getParcelableExtra("penObject");
        mTrackCow = mBaseRef.child(CowObject.COW).orderByChild(CowObject.PEN_ID).equalTo(mSelectedPen.getPenId());

        mIsActive = mSelectedPen.isActive();

        setTitle("Pen " + mSelectedPen.getPenName());

        mMedicateACowFabMenu = findViewById(R.id.floating_action_btn_menu);
        mLoadMedicatedCows = findViewById(R.id.load_medicated_cows);
        mNoMedicatedCows = findViewById(R.id.no_medicated_cows_tv);
        mResultsNotFound = findViewById(R.id.result_not_found);
        mMarkAsActive = findViewById(R.id.mark_as_active);
        mPenIdleLayout = findViewById(R.id.pen_idle);
        mCustomerName = findViewById(R.id.customer_name);
        mTotalCount = findViewById(R.id.total_head);
        mNotes = findViewById(R.id.pen_notes);

        mTrackCowListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTreatedCows.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CowObject cowObject = snapshot.getValue(CowObject.class);
                    if(cowObject != null){
                        mTreatedCows.add(cowObject);
                    }
                }
                mLoadMedicatedCows.setVisibility(View.INVISIBLE);
                mMedicatedCowsRecyclerViewAdapter.swapData(mTreatedCows, mDrugList);
                if(mTreatedCows.size() == 0){
                    mNoMedicatedCows.setVisibility(View.VISIBLE);
                }else{
                    mNoMedicatedCows.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDrugRef = mBaseRef.child(DrugObject.DRUG_OBJECT);
        mDrugListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    DrugObject drugObject = snapshot.getValue(DrugObject.class);
                    if(drugObject != null){
                        mDrugList.add(drugObject);
                    }
                }
                mMedicatedCowsRecyclerViewAdapter.swapData(mTreatedCows, mDrugList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mMarkAsActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCustomerName.length() == 0 || mTotalCount.length() == 0){
                    Snackbar.make(view, "Please fill the blanks", Snackbar.LENGTH_LONG).show();
                    return;
                }

                mIsActive = true;
                String customerName = mCustomerName.getText().toString();
                int totalHead = Integer.parseInt(mTotalCount.getText().toString());
                String notes = mNotes.getText().toString();

                mSelectedPen.setActive(mIsActive);
                mSelectedPen.setCustomerName(customerName);
                mSelectedPen.setTotalHead(totalHead);
                mSelectedPen.setNotes(notes);

                mBaseRef.child(PenObject.PEN_OBJECT).child(mSelectedPen.getPenId()).setValue(mSelectedPen);

                setActive();
            }
        });

        mMedicatedCows = findViewById(R.id.track_cow_rv);
        mMedicatedCows.setLayoutManager(new LinearLayoutManager(this));
        mMedicatedCowsRecyclerViewAdapter = new MedicatedCowsRecyclerViewAdapter(mTreatedCows, mDrugList,this);
        mMedicatedCows.setAdapter(mMedicatedCowsRecyclerViewAdapter);

        if(mIsActive){
            setActive();
        }else{
            setInActive();
        }
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
        if(requestCode == MEDICATE_A_COW_CODE && resultCode == Activity.RESULT_OK){
            Snackbar.make(mMedicatedCows, "Save successfully!", Snackbar.LENGTH_SHORT).show();
        }else if(requestCode == VIEW_PEN_REPORTS_CODE && resultCode == Activity.RESULT_OK){
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
        if(mIsActive) {
            mTrackCow.addValueEventListener(mTrackCowListener);
            mMedicateACowFabMenu.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        if(mIsActive) {
            mTrackCow.removeEventListener(mTrackCowListener);
        }
        super.onPause();
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
                    ArrayList<CowObject> list = findTags(s);
                    if (list.size() == 0 && shouldShowCouldntFindTag) {
                        mResultsNotFound.setVisibility(View.VISIBLE);
                        mMedicateACowFabMenu.setVisibility(View.INVISIBLE);
                    } else {
                        mMedicateACowFabMenu.setVisibility(View.VISIBLE);
                        mResultsNotFound.setVisibility(View.INVISIBLE);
                    }
                    shouldShowCouldntFindTag = true;
                    mMedicatedCowsRecyclerViewAdapter.swapData(list, mDrugList);
                }else{
                    mMedicateACowFabMenu.setVisibility(View.VISIBLE);
                    mResultsNotFound.setVisibility(View.INVISIBLE);
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
            penReportsIntent.putExtra("selectedPen", mSelectedPen);
            startActivityForResult(penReportsIntent, VIEW_PEN_REPORTS_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<CowObject> findTags(String inputString){
        ArrayList<CowObject> listToReturn = new ArrayList<>();
        for(int e=0; e<mTreatedCows.size(); e++){
            CowObject cowObject = mTreatedCows.get(e);
            String tag = Integer.toString(cowObject.getCowNumber());
            if(tag.startsWith(inputString)){
                listToReturn.add(cowObject);
            }
        }
        return listToReturn;
    }

    private void setActive(){
        mLoadMedicatedCows.setVisibility(View.VISIBLE);
        mMedicateACowFabMenu.setVisibility(View.VISIBLE);
        mTrackCow.addValueEventListener(mTrackCowListener);
        mDrugRef.addListenerForSingleValueEvent(mDrugListener);
        mPenIdleLayout.setVisibility(View.INVISIBLE);
        shouldShowCouldntFindTag = true;
        invalidateOptionsMenu();
        mIsActive = true;
    }

    private void setInActive(){
        mIsActive = false;
        mNoMedicatedCows.setVisibility(View.GONE);
        mMedicateACowFabMenu.setVisibility(View.INVISIBLE);
        shouldShowCouldntFindTag = false;
        mPenIdleLayout.setVisibility(View.VISIBLE);
        invalidateOptionsMenu();
    }
}
