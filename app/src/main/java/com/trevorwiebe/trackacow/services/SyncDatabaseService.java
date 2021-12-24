package com.trevorwiebe.trackacow.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.auth.FirebaseAuth;
import com.trevorwiebe.trackacow.R;
import com.trevorwiebe.trackacow.utils.Constants;
import com.trevorwiebe.trackacow.utils.SyncDatabase;

public class SyncDatabaseService extends Worker implements SyncDatabase.OnDatabaseSynced {

    private static final String TAG = "SyncDatabaseService";

    private Context mContext;
    private WorkerParameters mParams;

    public SyncDatabaseService(@NonNull Context context, @NonNull WorkerParameters params){
        super(context, params);
    }

//    @Override
//    public boolean onStartJob(@NonNull JobParameters job) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        if (auth.getCurrentUser() != null) {
//            new SyncDatabase(this, this).beginSync();
//            return true;
//        } else {
//            return false;
//        }
//    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            new SyncDatabase(this, mContext).beginSync();
        }
        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
    }

    //    @Override
//    public boolean onStopJob(@NonNull JobParameters job) {
//        return false;
//    }

    @Override
    public void onDatabaseSynced(int resultCode) {
        String channelId = mContext.getResources().getString(R.string.sync_notif_channel_id);
        switch (resultCode) {
            case Constants.SUCCESS:
//                Utility.showNotification(this, channelId, "Synced successfully", "Data synced successfully");
                break;
            case Constants.ERROR_FETCHING_DATA_FROM_CLOUD:
//                Utility.showNotification(this, channelId, "Sync failed", "Failed to fetch data from the cloud");
                break;
            case Constants.NO_NETWORK_CONNECTION:
//                Utility.showNotification(this, channelId, "Sync failed", "Tried to sync with no network");
                break;
            default:
//                Utility.showNotification(this, channelId, "Sync Failed", "Unknown error occurred");
        }
    }
}
