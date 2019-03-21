package com.trevorwiebe.trackacow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.DeleteAllLocalData;
import com.trevorwiebe.trackacow.fragments.FeedFragment;
import com.trevorwiebe.trackacow.fragments.MedicateFragment;
import com.trevorwiebe.trackacow.fragments.MoreFragment;
import com.trevorwiebe.trackacow.fragments.MoveFragment;
import com.trevorwiebe.trackacow.fragments.ReportsFragment;
import com.trevorwiebe.trackacow.services.SyncDatabaseService;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.SyncDatabase;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements
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
                        Utility.saveLastUsedScreen(MainActivity.this, Constants.MEDICATE);
                        break;
                    case R.id.action_feed:
                        setTitle("Feed");
                        FeedFragment feedFragment = new FeedFragment();
                        FragmentTransaction feedTransactionManager = getSupportFragmentManager().beginTransaction();
                        feedTransactionManager.replace(R.id.main_fragment_container, feedFragment);
                        feedTransactionManager.commit();
                        Utility.saveLastUsedScreen(MainActivity.this, Constants.FEED);
                        break;
                    case R.id.action_move:
                        setTitle("Move");
                        MoveFragment moveFragment = new MoveFragment();
                        FragmentTransaction moveTransactionManager = getSupportFragmentManager().beginTransaction();
                        moveTransactionManager.replace(R.id.main_fragment_container, moveFragment);
                        moveTransactionManager.commit();
                        Utility.saveLastUsedScreen(MainActivity.this, Constants.MOVE);
                        break;
                    case R.id.action_reports:
                        setTitle("Reports");
                        ReportsFragment reportsFragment = new ReportsFragment();
                        FragmentTransaction reportsTransactionManager = getSupportFragmentManager().beginTransaction();
                        reportsTransactionManager.replace(R.id.main_fragment_container, reportsFragment);
                        reportsTransactionManager.commit();
                        Utility.saveLastUsedScreen(MainActivity.this, Constants.REPORTS);
                        break;
                    case R.id.action_more:
                        setTitle("More");
                        MoreFragment moreFragment = new MoreFragment();
                        FragmentTransaction moreTransactionManager = getSupportFragmentManager().beginTransaction();
                        moreTransactionManager.replace(R.id.main_fragment_container, moreFragment);
                        moreTransactionManager.commit();
                        Utility.saveLastUsedScreen(MainActivity.this, Constants.MORE);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

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
    protected void onStop() {
        Utility.saveLastUsedScreen(this, Constants.MEDICATE);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGN_IN_CODE && resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    private void onSignedInInitialized(FirebaseUser user) {

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
        int lastUsedScreen = Utility.getLastUsedScreen(MainActivity.this);
        switch (lastUsedScreen) {
            case Constants.MEDICATE:
                setTitle("Medicate");
                MedicateFragment medicateFragment = new MedicateFragment();
                FragmentTransaction medicateTransactionManager = getSupportFragmentManager().beginTransaction();
                medicateTransactionManager.replace(R.id.main_fragment_container, medicateFragment);
                medicateTransactionManager.commit();
                Utility.saveLastUsedScreen(MainActivity.this, Constants.MEDICATE);
                break;
            case Constants.FEED:
                setTitle("Feed");
                FeedFragment feedFragment = new FeedFragment();
                FragmentTransaction feedTransactionManager = getSupportFragmentManager().beginTransaction();
                feedTransactionManager.replace(R.id.main_fragment_container, feedFragment);
                feedTransactionManager.commit();
                Utility.saveLastUsedScreen(MainActivity.this, Constants.FEED);
                break;
            case Constants.MOVE:
                setTitle("Move");
                MoveFragment moveFragment = new MoveFragment();
                FragmentTransaction moveTransactionManager = getSupportFragmentManager().beginTransaction();
                moveTransactionManager.replace(R.id.main_fragment_container, moveFragment);
                moveTransactionManager.commit();
                Utility.saveLastUsedScreen(MainActivity.this, Constants.MOVE);
                break;
            case Constants.REPORTS:
                setTitle("Reports");
                ReportsFragment reportsFragment = new ReportsFragment();
                FragmentTransaction reportsTransactionManager = getSupportFragmentManager().beginTransaction();
                reportsTransactionManager.replace(R.id.main_fragment_container, reportsFragment);
                reportsTransactionManager.commit();
                Utility.saveLastUsedScreen(MainActivity.this, Constants.REPORTS);
                break;
            case Constants.MORE:
                setTitle("More");
                MoreFragment moreFragment = new MoreFragment();
                FragmentTransaction moreTransactionManager = getSupportFragmentManager().beginTransaction();
                moreTransactionManager.replace(R.id.main_fragment_container, moreFragment);
                moreTransactionManager.commit();
                Utility.saveLastUsedScreen(MainActivity.this, Constants.MORE);
                break;
            default:
                setTitle("Medicate");
                mBottomNavigationView.setSelectedItemId(R.id.action_medicate);
                MedicateFragment medicateFragmentD = new MedicateFragment();
                FragmentTransaction medicateTransactionManagerD = getSupportFragmentManager().beginTransaction();
                medicateTransactionManagerD.replace(R.id.main_fragment_container, medicateFragmentD);
                medicateTransactionManagerD.commit();
                break;

        }
    }

    private void onSignedOutCleanUp() {
        new DeleteAllLocalData().execute(MainActivity.this);
    }

    @Override
    public void onDatabaseSynced(int resultCode) {
    }

}
