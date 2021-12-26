package com.trevorwiebe.trackacow.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.trevorwiebe.trackacow.dataLoaders.QueryUserEntity;
import com.trevorwiebe.trackacow.db.entities.UserEntity;
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
    private SyncDatabase mSyncDatabase;

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
        mBottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.action_medicate:
                        setTitle("Work");
                        MedicateFragment medicateFragment = new MedicateFragment();
                        FragmentTransaction medicateTransactionManager = getSupportFragmentManager().beginTransaction();
                        medicateTransactionManager.replace(R.id.main_fragment_container, medicateFragment);
                        medicateTransactionManager.commit();
                        mLastUsedScreen = Constants.MEDICATE;
                        break;
                    case R.id.action_feed:
                        setTitle("Feed");
                        FeedFragment feedFragment = new FeedFragment();
                        FragmentTransaction feedTransactionManager = getSupportFragmentManager().beginTransaction();
                        feedTransactionManager.replace(R.id.main_fragment_container, feedFragment);
                        feedTransactionManager.commit();
                        mLastUsedScreen = Constants.FEED;
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
                    Intent signInIntent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(signInIntent);
                } else {
                    onSignedInInitialized();
                }
            }
        };

    }


    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        if (mSyncDatabase != null) {
            mSyncDatabase.setSyncAvailability(true);
        }
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause() {
        if (mSyncDatabase != null) {
            mSyncDatabase.setSyncAvailability(false);
        }
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.saveLastFeedPen(this, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem upload_menu = menu.findItem(R.id.action_upload_data);
        if(Utility.isThereNewDataToUpload(MainActivity.this)){
            upload_menu.setVisible(true);
        }else{
            upload_menu.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_move_pens:
                setTitle("Move");
                MoveFragment moveFragment = new MoveFragment();
                FragmentTransaction moveTransactionManager = getSupportFragmentManager().beginTransaction();
                moveTransactionManager.replace(R.id.main_fragment_container, moveFragment);
                moveTransactionManager.commit();
                break;
            case R.id.action_sync_data:
                mMainProgressBar.setVisibility(View.VISIBLE);

                mSyncDatabase = new SyncDatabase(MainActivity.this, MainActivity.this);
                mSyncDatabase.setSyncAvailability(true);
                mSyncDatabase.beginSync();
                break;
            case R.id.action_upload_data:
                AlertDialog.Builder data_to_upload_dialog = new AlertDialog.Builder(MainActivity.this);

                data_to_upload_dialog.setTitle("Local changes made");
                data_to_upload_dialog.setMessage("You have made local changes that are not synced to the cloud.");
                data_to_upload_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                data_to_upload_dialog.setPositiveButton("Sync", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMainProgressBar.setVisibility(View.VISIBLE);

                        mSyncDatabase = new SyncDatabase(MainActivity.this, MainActivity.this);
                        mSyncDatabase.setSyncAvailability(true);
                        mSyncDatabase.beginSync();
                    }
                });
                data_to_upload_dialog.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSignedInInitialized() {

        long currentTime = System.currentTimeMillis();
        long lastSyncTime = Utility.getLastSync(MainActivity.this);

        long timeElapsed = currentTime - lastSyncTime;
        long timeUntilNextLoad = TimeUnit.MINUTES.toMillis(30);

        if(timeElapsed >= timeUntilNextLoad){
            mMainProgressBar.setVisibility(View.VISIBLE);

            mSyncDatabase = new SyncDatabase(MainActivity.this, MainActivity.this);
            mSyncDatabase.setSyncAvailability(true);
            mSyncDatabase.beginSync();
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresCharging(true)
                .setRequiresDeviceIdle(true)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(SyncDatabaseService.class, 24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork("sync_database_job_tag", ExistingPeriodicWorkPolicy.KEEP, request);

        mBottomNavigationView.setVisibility(View.VISIBLE);

        setSelectedFragment();
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
                Utility.setNewDataToUpload(MainActivity.this, false);
                invalidateOptionsMenu();
                setSelectedFragment();
                break;
            case Constants.ERROR_FETCHING_DATA_FROM_CLOUD:
                Toast.makeText(this, "There was an error fetching the data from the cloud.", Toast.LENGTH_SHORT).show();
                break;
            case Constants.ERROR_PUSHING_DATA_TO_CLOUD:
                Toast.makeText(this, "There was an error pushing data to the cloud.", Toast.LENGTH_SHORT).show();
                break;
            case Constants.NO_NETWORK_CONNECTION:
                Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
                break;
            case Constants.ERROR_ACTIVITY_DESTROYED_BEFORE_LOADED:
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
                    Utility.setLastSync(this, System.currentTimeMillis());
                } else if (daysLeft <= 7) {
                    showPassableDialog("Free trial ends soon.", "You have " + daysLeftStr + " day(s) left on your free trial.  Please subscribe to avoid a stall in service.");
                    Utility.setLastSync(this, System.currentTimeMillis());
                } else {
                    Utility.setLastSync(this, System.currentTimeMillis());
                }
                break;
            case UserEntity.MONTHLY_SUBSCRIPTION:
                title = "Your monthly subscription has ended.";

                if (daysLeft <= -3) {
                    showNoPassDialog(title, message);
                    Utility.setLastSync(this, System.currentTimeMillis());
                } else if (daysLeft <= 0) {
                    long daysLeftOnGracePeriod = daysLeft + 3;
                    String daysLeftOnGracePeriodStr = numberFormat.format(daysLeftOnGracePeriod);
                    showPassableDialog("Monthly subscription has ended.", "But we will give you a 3 day grace period to get your account issues ironed out. There is/are " + daysLeftOnGracePeriodStr + " day(s) left on the grace period.");
                    Utility.setLastSync(this, System.currentTimeMillis());
                } else {
                    Utility.setLastSync(this, System.currentTimeMillis());
                }
                break;
            case UserEntity.ANNUAL_SUBSCRIPTION:
                title = "Your annual subscription has ended.";

                if (daysLeft <= -3) {
                    showNoPassDialog(title, message);
                    Utility.setLastSync(this, System.currentTimeMillis());
                } else if (daysLeft <= 0) {
                    long daysLeftOnGracePeriod = daysLeft + 3;
                    String daysLeftOnGracePeriodStr = numberFormat.format(daysLeftOnGracePeriod);
                    showPassableDialog("Monthly subscription has ended.", "But we will give you a 3 day grace period to get your account issues ironed out. There is/are " + daysLeftOnGracePeriodStr + " day(s) left on the grace period.");
                    Utility.setLastSync(this, System.currentTimeMillis());
                } else {
                    Utility.setLastSync(this, System.currentTimeMillis());
                }
                break;
            case UserEntity.CANCELED:
                title = "Your account has been canceled.";
                message = "You will need to re-subscribe to a plan to continue. Your data may be all saved yet.";
                showNoPassDialog(title, message);
                Utility.setLastSync(this, System.currentTimeMillis());
            case UserEntity.FOREVER_FREE_USER:
                Utility.setLastSync(this, System.currentTimeMillis());
                break;
            default:
                title = "Error";
                message = "Unknown error occurred.  Please send an email to app@trackacow.net for support.";
                showNoPassDialog(title, message);
                Utility.setLastSync(this, System.currentTimeMillis());
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
            }
        });
        if (Utility.shouldShowTrialEndsSoon(MainActivity.this)) {
            accountEnding.show();
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
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.manage_sub_url)));
                startActivity(browserIntent);
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

    private void setSelectedFragment() {
        // open the medicate fragment
        mLastUsedScreen = Utility.getLastUsedScreen(MainActivity.this);
        switch (mLastUsedScreen) {
            case Constants.MEDICATE:
                setTitle("Work");
                mBottomNavigationView.setSelectedItemId(R.id.action_medicate);
                MedicateFragment medicateFragment = new MedicateFragment();
                FragmentTransaction medicateTransactionManager = getSupportFragmentManager().beginTransaction();
                medicateTransactionManager.replace(R.id.main_fragment_container, medicateFragment);
                medicateTransactionManager.commit();
                break;
            case Constants.FEED:
                setTitle("Feed");
                mBottomNavigationView.setSelectedItemId(R.id.action_feed);
                FeedFragment feedFragment = new FeedFragment();
                FragmentTransaction feedTransactionManager = getSupportFragmentManager().beginTransaction();
                feedTransactionManager.replace(R.id.main_fragment_container, feedFragment);
                feedTransactionManager.commit();
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
                setTitle("Work");
                mBottomNavigationView.setSelectedItemId(R.id.action_medicate);
                MedicateFragment medicateFragmentD = new MedicateFragment();
                FragmentTransaction medicateTransactionManagerD = getSupportFragmentManager().beginTransaction();
                medicateTransactionManagerD.replace(R.id.main_fragment_container, medicateFragmentD);
                medicateTransactionManagerD.commit();
                break;
        }
    }
}
