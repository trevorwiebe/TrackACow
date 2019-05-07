package com.trevorwiebe.trackacow.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.DeleteAllLocalData;
import com.trevorwiebe.trackacow.dataLoaders.InsertHoldingUserEntity;
import com.trevorwiebe.trackacow.dataLoaders.InsertNewUser;
import com.trevorwiebe.trackacow.dataLoaders.QueryUserEntity;
import com.trevorwiebe.trackacow.db.entities.UserEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingUserEntity;
import com.trevorwiebe.trackacow.fragments.FeedFragment;
import com.trevorwiebe.trackacow.fragments.MedicateFragment;
import com.trevorwiebe.trackacow.fragments.MoreFragment;
import com.trevorwiebe.trackacow.fragments.MoveFragment;
import com.trevorwiebe.trackacow.fragments.ReportsFragment;
import com.trevorwiebe.trackacow.services.SyncDatabaseService;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.SyncDatabase;
import com.trevorwiebe.trackacow.utils.Utility;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements
        SyncDatabase.OnDatabaseSynced,
        QueryUserEntity.OnUserLoaded {

    private static final String TAG = "MainActivity";

    private static final int SIGNIN_IN_CODE = 435;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());

    private BottomNavigationView mBottomNavigationView;
    private ProgressBar mMainProgressBar;
    private Toolbar mToolBar;
    private FrameLayout mMainLayout;

    private int mLastUsedScreen = Constants.MEDICATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);


        mMainLayout = findViewById(R.id.main_fragment_container);
        mMainProgressBar = findViewById(R.id.main_progress_bar);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.action_medicate:
                        setTitle("Medicate");
                        MedicateFragment medicateFragment = new MedicateFragment();
                        FragmentTransaction medicateTransactionManager = getSupportFragmentManager().beginTransaction();
                        medicateTransactionManager.replace(R.id.main_fragment_container, medicateFragment);
                        medicateTransactionManager.commit();
                        mLastUsedScreen = Constants.MEDICATE;
                        break;
//                    case R.id.action_feed:
//                        setTitle("Feed");
//                        FeedFragment feedFragment = new FeedFragment();
//                        FragmentTransaction feedTransactionManager = getSupportFragmentManager().beginTransaction();
//                        feedTransactionManager.replace(R.id.main_fragment_container, feedFragment);
//                        feedTransactionManager.commit();
//                        mLastUsedScreen = Constants.FEED;
//                        break;
                    case R.id.action_move:
                        setTitle("Move");
                        MoveFragment moveFragment = new MoveFragment();
                        FragmentTransaction moveTransactionManager = getSupportFragmentManager().beginTransaction();
                        moveTransactionManager.replace(R.id.main_fragment_container, moveFragment);
                        moveTransactionManager.commit();
                        mLastUsedScreen = Constants.MOVE;
                        break;
                    case R.id.action_reports:
                        setTitle("Reports");
                        ReportsFragment reportsFragment = new ReportsFragment();
                        FragmentTransaction reportsTransactionManager = getSupportFragmentManager().beginTransaction();
                        reportsTransactionManager.replace(R.id.main_fragment_container, reportsFragment);
                        reportsTransactionManager.commit();
                        mLastUsedScreen = Constants.REPORTS;
                        break;
                    case R.id.action_more:
                        setTitle("More");
                        MoreFragment moreFragment = new MoreFragment();
                        FragmentTransaction moreTransactionManager = getSupportFragmentManager().beginTransaction();
                        moreTransactionManager.replace(R.id.main_fragment_container, moreFragment);
                        moreTransactionManager.commit();
                        mLastUsedScreen = Constants.MORE;
                        break;
                    default:
                        break;
                }

                Utility.saveLastUsedScreen(MainActivity.this, mLastUsedScreen);

                Log.d(TAG, "onNavigationItemSelected: " + Utility.getLastUsedScreen(MainActivity.this));

                return true;
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseAuth = firebaseAuth;
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    onSignedOutCleanUp();
                    Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
                    startActivity(welcomeIntent);
                } else {
                    onSignedInInitialized();
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
    protected void onDestroy() {
        super.onDestroy();
        Utility.saveLastFeedPen(this, 0);
    }

    private void onSignedInInitialized() {

        Log.d(TAG, "onSignedInInitialized: here");

        long lastSync = Utility.getLastSync(MainActivity.this);
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - lastSync;
        long twoHoursInMillis = TimeUnit.HOURS.toMillis(2);
        mMainProgressBar.setVisibility(View.VISIBLE);
        new SyncDatabase(MainActivity.this, MainActivity.this).beginSync();

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        int periodicity = (int) TimeUnit.HOURS.toSeconds(24);
        int toleranceInterval = (int) TimeUnit.HOURS.toSeconds(3);

        Job syncDatabase = dispatcher.newJobBuilder()
                .setService(SyncDatabaseService.class)
                .setTag("sync_database_job_tag")
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(periodicity, toleranceInterval + periodicity))
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                .build();

        dispatcher.mustSchedule(syncDatabase);

        mBottomNavigationView.setVisibility(View.VISIBLE);

        // open the medicate fragment
        mLastUsedScreen = Utility.getLastUsedScreen(MainActivity.this);
        switch (mLastUsedScreen) {
            case Constants.MEDICATE:
                setTitle("Medicate");
                mBottomNavigationView.setSelectedItemId(R.id.action_medicate);
                MedicateFragment medicateFragment = new MedicateFragment();
                FragmentTransaction medicateTransactionManager = getSupportFragmentManager().beginTransaction();
                medicateTransactionManager.replace(R.id.main_fragment_container, medicateFragment);
                medicateTransactionManager.commit();
                break;
            case Constants.FEED:
                setTitle("Feed");
//                mBottomNavigationView.setSelectedItemId(R.id.action_feed);
                FeedFragment feedFragment = new FeedFragment();
                FragmentTransaction feedTransactionManager = getSupportFragmentManager().beginTransaction();
                feedTransactionManager.replace(R.id.main_fragment_container, feedFragment);
                feedTransactionManager.commit();
                break;
            case Constants.MOVE:
                setTitle("Move");
                mBottomNavigationView.setSelectedItemId(R.id.action_move);
                MoveFragment moveFragment = new MoveFragment();
                FragmentTransaction moveTransactionManager = getSupportFragmentManager().beginTransaction();
                moveTransactionManager.replace(R.id.main_fragment_container, moveFragment);
                moveTransactionManager.commit();
                break;
            case Constants.REPORTS:
                setTitle("Reports");
                mBottomNavigationView.setSelectedItemId(R.id.action_reports);
                ReportsFragment reportsFragment = new ReportsFragment();
                FragmentTransaction reportsTransactionManager = getSupportFragmentManager().beginTransaction();
                reportsTransactionManager.replace(R.id.main_fragment_container, reportsFragment);
                reportsTransactionManager.commit();
                break;
            case Constants.MORE:
                setTitle("More");
                mBottomNavigationView.setSelectedItemId(R.id.action_more);
                MoreFragment moreFragment = new MoreFragment();
                FragmentTransaction moreTransactionManager = getSupportFragmentManager().beginTransaction();
                moreTransactionManager.replace(R.id.main_fragment_container, moreFragment);
                moreTransactionManager.commit();
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
        Utility.saveLastUsedScreen(MainActivity.this, Constants.MEDICATE);
        new DeleteAllLocalData().execute(MainActivity.this);
    }

    @Override
    public void onDatabaseSynced(int resultCode) {
        switch (resultCode) {
            case Constants.SUCCESS:
                String uid = mFirebaseAuth.getCurrentUser().getUid();
                new QueryUserEntity(uid, MainActivity.this).execute(MainActivity.this);
                break;
            case Constants.ERROR_FETCHING_DATA_FROM_CLOUD:
                Toast.makeText(this, "Error interpreting data from the cloud", Toast.LENGTH_SHORT).show();
                break;
            case Constants.NO_NETWORK_CONNECTION:
                break;
            default:
                Toast.makeText(this, "An unknown error occurred while syncing database", Toast.LENGTH_SHORT).show();
        }
        mMainProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onUserLoaded(UserEntity userEntity) {

        if (userEntity == null) return;

        long currentTime = System.currentTimeMillis();
        long renewalDate = userEntity.getRenewalDate();
        long timeElapsed = renewalDate - currentTime;
        long daysLeft = (int) (timeElapsed / (1000 * 60 * 60 * 24));
        String daysLeftStr = numberFormat.format(daysLeft);

        String title;
        String message = "You will need to subscribe to a plan to continue.  All you data is saved and waiting for you.";

        switch (userEntity.getAccountType()) {
            case UserEntity.FREE_TRIAL:

                title = "Your free trial has ended.";

                if (daysLeft <= 0) {
                    showNoPassDialog(title, message);
                    return;
                }

                if (daysLeft <= 7) {
                    showPassableDialog("Free trial ends soon.", "You have " + daysLeftStr + " day(s) left on your free trial.  Please subscribe to avoid a stall in service.");
                }

                break;
            case UserEntity.MONTHLY_SUBSCRIPTION:
                title = "Your monthly subscription has ended.";

                if (daysLeft <= -3) {
                    showNoPassDialog(title, message);
                    return;
                }

                if (daysLeft <= 0) {
                    long daysLeftOnGracePeriod = daysLeft + 3;
                    String daysLeftOnGracePeriodStr = numberFormat.format(daysLeftOnGracePeriod);
                    showPassableDialog("Monthly subscription has ended.", "But we will give you a 3 day grace period to get your account issues ironed out. There is/are " + daysLeftOnGracePeriodStr + " day(s) left on the grace period.");
                }

                break;
            case UserEntity.ANNUAL_SUBSCRIPTION:
                title = "Your annual subscription has ended.";

                if (daysLeft <= -3) {
                    showNoPassDialog(title, message);
                    return;
                }

                if (daysLeft <= 0) {
                    long daysLeftOnGracePeriod = daysLeft + 3;
                    String daysLeftOnGracePeriodStr = numberFormat.format(daysLeftOnGracePeriod);
                    showPassableDialog("Monthly subscription has ended.", "But we will give you a 3 day grace period to get your account issues ironed out. There is/are " + daysLeftOnGracePeriodStr + " day(s) left on the grace period.");
                }

                break;
            case UserEntity.CANCELED:
                title = "Your account has been canceled.";
                message = "You will need to re-subscribe to a plan to continue. Your data may be all saved yet.";
                showNoPassDialog(title, message);
                return;
            case UserEntity.FOREVER_FREE_USER:
                break;
            default:
                title = "Error";
                message = "Unknown error occurred.  Please email support at app@trackacow.net for assistance.";
                showNoPassDialog(title, message);
                return;
        }

        mBottomNavigationView.setVisibility(View.VISIBLE);
        mToolBar.setVisibility(View.VISIBLE);
        mMainLayout.setVisibility(View.VISIBLE);

    }

    private void showPassableDialog(String title, String message) {
        AlertDialog.Builder accountEnding = new AlertDialog.Builder(MainActivity.this);
        accountEnding.setTitle(title);
        accountEnding.setMessage(message);
        accountEnding.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utility.setShouldShowTrialEndsSoon(MainActivity.this, true);
                Utility.setLastSync(MainActivity.this, System.currentTimeMillis());
            }
        });
        accountEnding.setPositiveButton("Renew", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        accountEnding.setNeutralButton("Don't show again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utility.setShouldShowTrialEndsSoon(MainActivity.this, false);
                Utility.setLastSync(MainActivity.this, System.currentTimeMillis());
            }
        });
        if (Utility.shouldShowTrialEndsSoon(MainActivity.this)) {
            accountEnding.show();
        } else {
            Utility.setLastSync(MainActivity.this, System.currentTimeMillis());
        }
    }

    private void showNoPassDialog(String title, String message) {
        AlertDialog.Builder accountEnded = new AlertDialog.Builder(MainActivity.this);
        mBottomNavigationView.setVisibility(View.GONE);
        mToolBar.setVisibility(View.GONE);
        mMainLayout.setVisibility(View.GONE);
        accountEnded.setTitle(title);
        accountEnded.setMessage(message);
        accountEnded.setCancelable(false);
        accountEnded.setPositiveButton("Renew", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent subscribeActivity = new Intent(MainActivity.this, SubscribeActivity.class);
                startActivity(subscribeActivity);
            }
        });
        accountEnded.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        accountEnded.show();
    }
}
