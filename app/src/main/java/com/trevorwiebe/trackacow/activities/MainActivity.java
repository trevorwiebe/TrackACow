package com.trevorwiebe.trackacow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.trevorwiebe.trackacow.fragments.FeedFragment;
import com.trevorwiebe.trackacow.fragments.MedicateFragment;
import com.trevorwiebe.trackacow.fragments.ReportsFragment;
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
        SyncDatabase.OnDatabaseSynced {

    private static final String TAG = "MainActivity";

    private static final int SIGN_IN_CODE = 838;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    private BottomNavigationView mBottomNavigationView;


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

        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.action_medicate:
                        Log.d(TAG, "onNavigationItemSelected: here");
                        setTitle("Medicate");
                        MedicateFragment medicateFragment = new MedicateFragment();
                        FragmentTransaction medicateTransactionManager = getSupportFragmentManager().beginTransaction();
                        medicateTransactionManager.replace(R.id.main_fragment_container, medicateFragment);
                        medicateTransactionManager.commit();
                        break;
                    case R.id.action_feed:
                        setTitle("Feed");
                        FeedFragment feedFragment = new FeedFragment();
                        FragmentTransaction feedTransactionManager = getSupportFragmentManager().beginTransaction();
                        feedTransactionManager.replace(R.id.main_fragment_container, feedFragment);
                        feedTransactionManager.commit();
                        break;
                    case R.id.action_reports:
                        setTitle("Reports");
                        ReportsFragment reportsFragment = new ReportsFragment();
                        FragmentTransaction reportsTransactionManager = getSupportFragmentManager().beginTransaction();
                        reportsTransactionManager.replace(R.id.main_fragment_container, reportsFragment);
                        reportsTransactionManager.commit();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseAuth = firebaseAuth;
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {

                    if (Utility.haveNetworkConnection(MainActivity.this)) {
                        onSignedOutCleanUp();
                        Intent signInIntent = new Intent(MainActivity.this, SignInActivity.class);
                        startActivityForResult(signInIntent, SIGN_IN_CODE);
                    }
                } else {
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

    private void onSignedInInitialized(FirebaseUser user) {

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.nav_userName);
        TextView userEmail = headerView.findViewById(R.id.nav_userEmail);

        userName.setText(user.getDisplayName());
        userEmail.setText(user.getEmail());

        new SyncDatabase(MainActivity.this, MainActivity.this).beginSync();

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

        mBottomNavigationView.setVisibility(View.VISIBLE);

        // open the medicate fragment
        setTitle("Medicate");
        mBottomNavigationView.setSelectedItemId(R.id.action_medicate);
        MedicateFragment medicateFragment = new MedicateFragment();
        FragmentTransaction medicateTransactionManager = getSupportFragmentManager().beginTransaction();
        medicateTransactionManager.replace(R.id.main_fragment_container, medicateFragment);
        medicateTransactionManager.commit();
    }

    private void onSignedOutCleanUp() {
        new DeleteAllLocalData().execute(MainActivity.this);
    }

    @Override
    public void onDatabaseSynced(int resultCode) {
    }

}
