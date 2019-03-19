package com.trevorwiebe.trackacow.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.dataLoaders.CloneCloudDatabaseToLocalDatabase;
import com.trevorwiebe.trackacow.dataLoaders.InsertAllLocalChangeToCloud;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.SyncDatabase;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;

public class SyncDatabaseService extends JobService implements SyncDatabase.OnDatabaseSynced {

    private static final String TAG = "SyncDatabaseService";

    @Override
    public boolean onStartJob(@NonNull JobParameters job) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            new SyncDatabase(this, this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onStopJob(@NonNull JobParameters job) {
        return false;
    }

    @Override
    public void onDatabaseSynced(int resultCode) {
        String channelId = getResources().getString(R.string.sync_notif_channel_id);
        switch (resultCode) {
            case Constants.SUCCESS:
                Utility.showNotification(this, channelId, "Synced successfully", "Data synced successfully");
                break;
            case Constants.ERROR_FETCHING_DATA_FROM_CLOUD:
                Utility.showNotification(this, channelId, "Sync failed", "Failed to fetch data from the cloud");
                break;
            case Constants.NO_NETWORK_CONNECTION:
                Utility.showNotification(this, channelId, "Sync failed", "Tried to sync with no network");
                break;
        }
    }
}
