package com.trevorwiebe.trackacow.presentation.activities;

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

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.domain.dataLoaders.misc.DeleteAllLocalData;
import com.trevorwiebe.trackacow.domain.dataLoaders.main.user.QueryUserEntity;
import com.trevorwiebe.trackacow.data.entities.UserEntity;
import com.trevorwiebe.trackacow.presentation.fragment_feed.FeedContainerFragment;
import com.trevorwiebe.trackacow.presentation.fragments.MedicateFragment;
import com.trevorwiebe.trackacow.presentation.fragments.MoreFragment;
import com.trevorwiebe.trackacow.presentation.fragment_move.MoveFragment;
import com.trevorwiebe.trackacow.presentation.fragments.ReportsFragment;
import com.trevorwiebe.trackacow.domain.services.SyncDatabaseService;
import com.trevorwiebe.trackacow.domain.utils.Constants;
import com.trevorwiebe.trackacow.domain.utils.SyncDatabase;
import com.trevorwiebe.trackacow.domain.utils.Utility;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements
        SyncDatabase.OnDatabaseSynced,
        QueryUserEntity.OnUserLoaded {

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private final NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
    private SyncDatabase mSyncDatabase;

    private BottomNavigationView mBottomNavigationView;
    private ProgressBar mMainProgressBar;
    private Toolbar mToolBar;
    private FrameLayout mMainLayout;

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
                mBottomNavigationView.getMenu().setGroupCheckable(0, true, true);
                int id = menuItem.getItemId();
                setSelectedFragment(Utility.getFragmentIdFromResourceID(id));
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
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem upload_menu = menu.findItem(R.id.action_upload_data);
        upload_menu.setVisible(Utility.isThereNewDataToUpload(MainActivity.this));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_move_pens){
            setSelectedFragment(Constants.MOVE);
            mBottomNavigationView.getMenu().setGroupCheckable(0, false, true);
        }else if(id == R.id.action_sync_data){
            mMainProgressBar.setVisibility(View.VISIBLE);
            mSyncDatabase = new SyncDatabase(MainActivity.this, MainActivity.this);
            mSyncDatabase.beginSync();
        }else{
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
                    mSyncDatabase.beginSync();
                }
            });
            data_to_upload_dialog.show();
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
            mSyncDatabase.beginSync();
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresDeviceIdle(true)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(SyncDatabaseService.class, 24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork("sync_database_job_tag", ExistingPeriodicWorkPolicy.KEEP, request);

        int lastUsedScreenFragId = Utility.getLastUsedScreen(MainActivity.this);

        mBottomNavigationView.setVisibility(View.VISIBLE);
        mBottomNavigationView.setSelectedItemId(Utility.getResourceIdFromFragmentId(lastUsedScreenFragId));

        setSelectedFragment(lastUsedScreenFragId);

        mBottomNavigationView.getMenu().setGroupCheckable(0, lastUsedScreenFragId != Constants.MOVE, true);
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
                setSelectedFragment(Utility.getLastUsedScreen(MainActivity.this));
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

    private void setSelectedFragment(int fragmentId) {
        // save the fragment id
        Utility.saveLastUsedScreen(this, fragmentId);

        // check if the activity has been destroyed
        if(this.isDestroyed()) return;

        // open the correct fragment
        switch (fragmentId) {
            case Constants.FEED:
                setTitle("Feed");
                FeedContainerFragment feedContainerFragment = new FeedContainerFragment();
                FragmentTransaction feedTransactionManager = getSupportFragmentManager().beginTransaction();
                feedTransactionManager.replace(R.id.main_fragment_container, feedContainerFragment);
                feedTransactionManager.commit();
                break;
            case Constants.REPORTS:
                setTitle("Reports");
                ReportsFragment reportsFragment = new ReportsFragment();
                FragmentTransaction reportsTransactionManager = getSupportFragmentManager().beginTransaction();
                reportsTransactionManager.replace(R.id.main_fragment_container, reportsFragment);
                reportsTransactionManager.commit();
                break;
            case Constants.MORE:
                setTitle("More");
                MoreFragment moreFragment = new MoreFragment();
                FragmentTransaction moreTransactionManager = getSupportFragmentManager().beginTransaction();
                moreTransactionManager.replace(R.id.main_fragment_container, moreFragment);
                moreTransactionManager.commit();
                break;
            case Constants.MOVE:
                setTitle("Move");
                MoveFragment moveFragment = new MoveFragment();
                FragmentTransaction moveTransactionManager = getSupportFragmentManager().beginTransaction();
                moveTransactionManager.replace(R.id.main_fragment_container, moveFragment);
                moveTransactionManager.commit();
                break;
            default:
                setTitle("Work");
                MedicateFragment medicateFragmentDefault = new MedicateFragment();
                FragmentTransaction medicateTransactionManagerDefault = getSupportFragmentManager().beginTransaction();
                medicateTransactionManagerDefault.replace(R.id.main_fragment_container, medicateFragmentDefault);
                medicateTransactionManagerDefault.commit();
                break;
        }
    }
}
