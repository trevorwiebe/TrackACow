package com.trevorwiebe.trackacow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.adapters.PenRecyclerViewAdapter;
import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.utils.ItemClickListener;
import com.trevorwiebe.trackacow.dataLoaders.LoadPens;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        LoadPens.OnPensLoaded {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 132;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private PenRecyclerViewAdapter mPenRecyclerViewAdapter;
    private ArrayList<PenEntity> mPenList = new ArrayList<>();
    private LinearLayout mNoConnectionLayout;
    private ProgressBar mLoadingMain;
    private TextView mNoPensTv;
    private RecyclerView mPenRv;

    private ArrayList<CowEntity> mCowEntityUpdateList = new ArrayList<>();
    private ArrayList<DrugEntity> mDrugEntityUpdateList = new ArrayList<>();
    private ArrayList<DrugsGivenEntity> mDrugsGivenEntityUpdateList = new ArrayList<>();
    private ArrayList<PenEntity> mPenEntityUpdateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mNoPensTv = findViewById(R.id.no_pens_tv);
        mLoadingMain = findViewById(R.id.loading_main);
        mNoConnectionLayout = findViewById(R.id.no_connection_and_signed_out_layout);
        mPenRv = findViewById(R.id.main_rv);
        mPenRv.setLayoutManager(new LinearLayoutManager(this));
        mPenRecyclerViewAdapter = new PenRecyclerViewAdapter(mPenList, false, this);
        mPenRv.setAdapter(mPenRecyclerViewAdapter);

        mPenRv.addOnItemTouchListener(new ItemClickListener(this, mPenRv,
                new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent trackCowIntent = new Intent(MainActivity.this, MedicatedCowsActivity.class);
                trackCowIntent.putExtra("penObject", mPenList.get(position));
                startActivity(trackCowIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseAuth = firebaseAuth;
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){

                    mLoadingMain.setVisibility(View.INVISIBLE);
                    mPenRv.setVisibility(View.INVISIBLE);

                    if(Utility.haveNetworkConnection(MainActivity.this)) {
                        onSignedOutCleanUp();

                        // Choose authentication providers
                        List<AuthUI.IdpConfig> providers = Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build());

                        // Create and launch sign-in intent
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setAvailableProviders(providers)
                                        .build(),
                                RC_SIGN_IN);
                    }else{
                        mNoConnectionLayout.setVisibility(View.VISIBLE);
                    }
                }else{
                    onSignedInInitialized(user);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_manage_drugs:
                Intent manageDrugsIntent = new Intent(MainActivity.this, ManageDrugsActivity.class);
                startActivity(manageDrugsIntent);
                break;
            case R.id.nav_manage_pens:
                Intent managePensIntent = new Intent(MainActivity.this, ManagePensActivity.class);
                startActivity(managePensIntent);
                break;
            case R.id.nav_sign_out:
                AuthUI.getInstance().signOut(this);
                break;
            default:
                Log.e(TAG, "onNavigationItemSelected: unknown menu id");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPensLoaded(ArrayList<PenEntity> penObjectList) {
        mPenList = penObjectList;
        if(mPenList.size() == 0) {
            mNoPensTv.setVisibility(View.VISIBLE);
        }else {
            mNoPensTv.setVisibility(View.INVISIBLE);
        }
        mPenRecyclerViewAdapter.swapData(mPenList);
        mPenRv.setVisibility(View.VISIBLE);
    }

    public void signInButton(View view){
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    private void onSignedInInitialized(FirebaseUser user){

        mLoadingMain.setVisibility(View.VISIBLE);
        mNoConnectionLayout.setVisibility(View.INVISIBLE);
        mPenRv.setVisibility(View.INVISIBLE);


        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.nav_userName);
        TextView userEmail = headerView.findViewById(R.id.nav_userEmail);

        userName.setText(user.getDisplayName());
        userEmail.setText(user.getEmail());

        if(Utility.haveNetworkConnection(this)) {
            DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            DrugsGivenEntity drugsGivenEntity = new DrugsGivenEntity();
            drugsGivenEntity.setPrimaryKey(1);
            drugsGivenEntity.setAmountGiven(45);
            drugsGivenEntity.setCowId("cowId");
            drugsGivenEntity.setDate(System.currentTimeMillis());
            baseRef.child("test").setValue(drugsGivenEntity);

            baseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        if (key != null) {
                            switch (key) {
                                case "cows":
                                    for (DataSnapshot cowSnapshot : snapshot.getChildren()) {
                                        CowEntity cowEntity = cowSnapshot.getValue(CowEntity.class);
                                        if (cowEntity != null) {
                                            mCowEntityUpdateList.add(cowEntity);
                                        }
                                    }
                                    break;
                                case "drugs":
                                    for (DataSnapshot drugsSnapshot : snapshot.getChildren()) {
                                        DrugEntity drugEntity = drugsSnapshot.getValue(DrugEntity.class);
                                        if (drugEntity != null) {
                                            mDrugEntityUpdateList.add(drugEntity);
                                        }
                                    }
                                    break;
                                case "pens":
                                    mPenList.clear();
                                    for (DataSnapshot penSnapshot : snapshot.getChildren()) {
                                        PenEntity penEntity = penSnapshot.getValue(PenEntity.class);
                                        if (penEntity != null) {
                                            mPenList.add(penEntity);
                                            mPenEntityUpdateList.add(penEntity);
                                        }
                                    }
                                    mLoadingMain.setVisibility(View.INVISIBLE);
                                    if(mPenList.size() == 0) {
                                        mNoPensTv.setVisibility(View.VISIBLE);
                                    }else {
                                        mNoPensTv.setVisibility(View.INVISIBLE);
                                    }
                                    mPenRecyclerViewAdapter.swapData(mPenList);
                                    mPenRv.setVisibility(View.VISIBLE);
                                    break;
                                case "drugsGiven":
                                    break;
                                default:
                                    Log.e(TAG, "onDataChange: unknown snapshot key");
                            }
                        }
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());

                            db.cowDao().deleteCowTable();
                            db.drugDao().deleteDrugTable();
                            db.drugsGivenDao().deleteDrugsGivenTable();
                            db.penDao().deletePenTable();

                            db.cowDao().insertCowList(mCowEntityUpdateList);
                            db.drugDao().insertListDrug(mDrugEntityUpdateList);
                            db.drugsGivenDao().insertDrugsGivenList(mDrugsGivenEntityUpdateList);
                            db.penDao().insertPenList(mPenEntityUpdateList);

                            mCowEntityUpdateList.clear();
                            mDrugEntityUpdateList.clear();
                            mDrugsGivenEntityUpdateList.clear();
                            mPenEntityUpdateList.clear();

                        }
                    }).start();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            mLoadingMain.setVisibility(View.INVISIBLE);
            new LoadPens(MainActivity.this).execute(MainActivity.this);
        }

    }

    private void onSignedOutCleanUp(){
        mPenList.clear();
        mPenRecyclerViewAdapter.swapData(mPenList);
    }

}
