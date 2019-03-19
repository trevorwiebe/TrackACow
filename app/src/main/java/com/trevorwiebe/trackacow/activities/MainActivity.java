package com.trevorwiebe.trackacow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.adapters.PenRecyclerViewAdapter;
import com.trevorwiebe.trackacow.dataLoaders.CloneCloudDatabaseToLocalDatabase;
import com.trevorwiebe.trackacow.dataLoaders.DeleteAllLocalData;
import com.trevorwiebe.trackacow.dataLoaders.InsertAllLocalChangeToCloud;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.services.SyncDatabaseService;
import com.trevorwiebe.trackacow.utils.ItemClickListener;
import com.trevorwiebe.trackacow.dataLoaders.QueryAllPens;
import com.trevorwiebe.trackacow.utils.SyncDatabase;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        QueryAllPens.OnPensLoaded,
        SyncDatabase.OnDatabaseSynced {

    private static final String TAG = "MainActivity";

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private PenRecyclerViewAdapter mPenRecyclerViewAdapter;
    private ArrayList<PenEntity> mPenList = new ArrayList<>();
    private static final int SIGN_IN_CODE = 37;

    private LinearLayout mNoConnectionLayout;
    private ProgressBar mLoadingMain;
    private TextView mNoPensTv;
    private RecyclerView mPenRv;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String channelId = getResources().getString(R.string.sync_notif_channel_id);
        Utility.setUpNotificationChannels(this, channelId, "Database synced", "This is a test notification");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mSwipeRefreshLayout = findViewById(R.id.main_swipe_refresh_layout);
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
                        String penId = mPenList.get(position).getPenId();
                        Utility.setPenId(MainActivity.this, penId);
                        trackCowIntent.putExtra("penEntityId", penId);
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
                if (user == null) {

                    mLoadingMain.setVisibility(View.INVISIBLE);
                    mPenRv.setVisibility(View.INVISIBLE);

                    if (Utility.haveNetworkConnection(MainActivity.this)) {
                        onSignedOutCleanUp();
                        Intent signInIntent = new Intent(MainActivity.this, SignInActivity.class);
                        startActivityForResult(signInIntent, SIGN_IN_CODE);
                    } else {
                        mNoConnectionLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    onSignedInInitialized(user);
                }
            }
        };

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SyncDatabase(MainActivity.this, MainActivity.this).beginSync();
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGN_IN_CODE && resultCode == RESULT_CANCELED) {
            finish();
        }
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

        switch (id) {
            case R.id.nav_manage_drugs:
                Intent manageDrugsIntent = new Intent(MainActivity.this, ManageDrugsActivity.class);
                startActivity(manageDrugsIntent);
                break;
            case R.id.nav_manage_pens:
                Intent managePensIntent = new Intent(MainActivity.this, ManagePensActivity.class);
                startActivity(managePensIntent);
                break;
            case R.id.nav_settings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
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
        setPenRecyclerView();
    }

    public void signInButton(View view) {
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    private void onSignedInInitialized(FirebaseUser user) {

        mNoConnectionLayout.setVisibility(View.INVISIBLE);
        mPenRv.setVisibility(View.INVISIBLE);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.nav_userName);
        TextView userEmail = headerView.findViewById(R.id.nav_userEmail);

        userName.setText(user.getDisplayName());
        userEmail.setText(user.getEmail());

        mLoadingMain.setVisibility(View.VISIBLE);
        new SyncDatabase(MainActivity.this, MainActivity.this).beginSync();

        new QueryAllPens(MainActivity.this).execute(MainActivity.this);

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        int periodicity = (int) TimeUnit.HOURS.toSeconds(24);
        int toleranceInterval = (int) TimeUnit.HOURS.toSeconds(3);

        Job syncDatabase = dispatcher.newJobBuilder()
                .setService(SyncDatabaseService.class)
                .setTag("sync_database_job_tag")
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(periodicity, periodicity + toleranceInterval))
                .setConstraints(
                        Constraint.DEVICE_CHARGING,
                        Constraint.ON_UNMETERED_NETWORK,
                        Constraint.DEVICE_IDLE
                )
                .build();

        dispatcher.mustSchedule(syncDatabase);
    }

    private void onSignedOutCleanUp() {
        new DeleteAllLocalData().execute(MainActivity.this);
        mPenList.clear();
        mPenRecyclerViewAdapter.swapData(mPenList);
    }

    private void setPenRecyclerView() {
        if (mPenList.size() == 0) {
            mNoPensTv.setVisibility(View.VISIBLE);
        } else {
            mNoPensTv.setVisibility(View.INVISIBLE);
        }
        Collections.sort(mPenList, new Comparator<PenEntity>() {
            @Override
            public int compare(PenEntity pen1, PenEntity pen2) {
                return pen1.getPenName().compareTo(pen2.getPenName());
            }
        });
        mPenRecyclerViewAdapter.swapData(mPenList);
        mPenRv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDatabaseSynced(int resultCode) {
        mSwipeRefreshLayout.setRefreshing(false);
        mLoadingMain.setVisibility(View.INVISIBLE);
    }
}
