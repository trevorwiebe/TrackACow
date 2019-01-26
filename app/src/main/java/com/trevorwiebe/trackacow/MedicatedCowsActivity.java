package com.trevorwiebe.trackacow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
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
    private ProgressBar mLoadMedicatedCows;
    private TextView mNoMedicatedCows;
    private SearchView mSearchView;
    private static final int MEDICATE_A_COW_CODE = 743;
    private boolean mIsActive;

    private RecyclerView mMedicatedCows;
    private CardView mResultsNotFound;
    private FloatingActionButton mMedicateACowFab;
    private Button mMarkAsActive;
    private ScrollView mPenIdleLayout;
    private TextInputEditText mCustomerName;
    private TextInputEditText mTotalCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicated_cows);

        mSelectedPen = getIntent().getParcelableExtra("penObject");
        mTrackCow = mBaseRef.child(CowObject.COW).orderByChild(CowObject.PEN_ID).equalTo(mSelectedPen.getPenId());

        mIsActive = mSelectedPen.isActive();

        setTitle("Pen " + mSelectedPen.getPenName());

        mMedicateACowFab = findViewById(R.id.medicate_a_cow_fab);
        mLoadMedicatedCows = findViewById(R.id.load_medicated_cows);
        mNoMedicatedCows = findViewById(R.id.no_medicated_cows_tv);
        mResultsNotFound = findViewById(R.id.result_not_found);
        mMarkAsActive = findViewById(R.id.mark_as_active);
        mPenIdleLayout = findViewById(R.id.pen_idle);
        mCustomerName = findViewById(R.id.customer_name);
        mTotalCount = findViewById(R.id.total_head);

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

                mSelectedPen.setActive(mIsActive);
                mSelectedPen.setCustomerName(customerName);
                mSelectedPen.setTotalHead(totalHead);

                mBaseRef.child(PenObject.PEN_OBJECT).child(mSelectedPen.getPenId()).setValue(mSelectedPen);
                mPenIdleLayout.setVisibility(View.INVISIBLE);

                mMedicateACowFab.show();
                mTrackCow.addValueEventListener(mTrackCowListener);
                mDrugRef.addListenerForSingleValueEvent(mDrugListener);
                invalidateOptionsMenu();
            }
        });

        mMedicatedCows = findViewById(R.id.track_cow_rv);
        mMedicatedCows.setLayoutManager(new LinearLayoutManager(this));
        mMedicatedCowsRecyclerViewAdapter = new MedicatedCowsRecyclerViewAdapter(mTreatedCows, mDrugList,this);
        mMedicatedCows.setAdapter(mMedicatedCowsRecyclerViewAdapter);

        if(mIsActive){
            mLoadMedicatedCows.setVisibility(View.VISIBLE);
            mMedicateACowFab.show();
            mDrugRef.addListenerForSingleValueEvent(mDrugListener);
            mPenIdleLayout.setVisibility(View.INVISIBLE);
        }else{
            mMedicateACowFab.hide();
        }
    }

    public void medicateCow(View view){
        Intent medicateCowIntent = new Intent(MedicatedCowsActivity.this, MedicateACowActivity.class);
        medicateCowIntent.putExtra("penObject", mSelectedPen);
        startActivityForResult(medicateCowIntent, MEDICATE_A_COW_CODE);
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
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mIsActive) {
            mTrackCow.addValueEventListener(mTrackCowListener);
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
                ArrayList<CowObject> list = findTags(s);
                if(list.size() == 0){
                    mResultsNotFound.setVisibility(View.VISIBLE);
                    mMedicateACowFab.hide();
                }else{
                    mMedicateACowFab.show();
                    mResultsNotFound.setVisibility(View.INVISIBLE);
                }
                mMedicatedCowsRecyclerViewAdapter.swapData(list, mDrugList);
                return false;
            }
        });
        return true;
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
}
