package com.trevorwiebe.trackacow.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.trevorwiebe.trackacow.dataLoaders.QueryAllCloudData;
import com.trevorwiebe.trackacow.dataLoaders.CloneCloudDatabaseToLocalDatabase;
import com.trevorwiebe.trackacow.dataLoaders.InsertAllLocalChangeToCloud;
import com.trevorwiebe.trackacow.db.entities.ArchivedLotEntity;
import com.trevorwiebe.trackacow.db.entities.CallEntity;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.FeedEntity;
import com.trevorwiebe.trackacow.db.entities.LoadEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.db.entities.UserEntity;

import java.util.ArrayList;

public class SyncDatabase implements
        InsertAllLocalChangeToCloud.OnAllLocalDbInsertedToCloud,
        QueryAllCloudData.OnAllCloudDataLoaded,
        CloneCloudDatabaseToLocalDatabase.OnDatabaseCloned {

    private static final String TAG = "SyncDatabase";

    private OnDatabaseSynced onDatabaseSynced;
    private Context context;
    private boolean mIsSyncAvailable;

    public SyncDatabase(OnDatabaseSynced onDatabaseSynced, Context context) {
        this.onDatabaseSynced = onDatabaseSynced;
        this.context = context;
    }

    public interface OnDatabaseSynced {
        void onDatabaseSynced(int resultCode);
    }

    public void beginSync() {

        if (Utility.haveNetworkConnection(context)) {
//            if (mIsSyncAvailable) {
                if (Utility.isThereNewDataToUpload(context)) {
                    new InsertAllLocalChangeToCloud(SyncDatabase.this).execute(context);
                } else {
                    new QueryAllCloudData(SyncDatabase.this).loadCloudData();
                }
//            } else {
//                onDatabaseSynced.onDatabaseSynced(Constants.ERROR_ACTIVITY_DESTROYED_BEFORE_LOADED);
//            }
        } else {
            onDatabaseSynced.onDatabaseSynced(Constants.NO_NETWORK_CONNECTION);
        }

    }

    @Override
    public void onAllLocalDbInsertedToCloud(int resultCode) {
        if (resultCode == Constants.SUCCESS) {
//            if (mIsSyncAvailable) {
                new QueryAllCloudData(this).loadCloudData();
//            } else {
//                onDatabaseSynced.onDatabaseSynced(Constants.ERROR_ACTIVITY_DESTROYED_BEFORE_LOADED);
//            }
        } else {
            onDatabaseSynced.onDatabaseSynced(resultCode);
        }
    }

    @Override
    public void onAllCloudDataLoaded(int resultCode, ArrayList<CowEntity> cowEntities, ArrayList<DrugEntity> drugEntities, ArrayList<DrugsGivenEntity> drugsGivenEntities, ArrayList<PenEntity> penEntities, ArrayList<LotEntity> lotEntities, ArrayList<ArchivedLotEntity> archivedLotEntities, ArrayList<LoadEntity> loadEntities, ArrayList<CallEntity> callEntities, ArrayList<FeedEntity> feedEntities, ArrayList<UserEntity> userEntities) {
        if (resultCode == Constants.SUCCESS) {
//            if (mIsSyncAvailable) {
                new CloneCloudDatabaseToLocalDatabase(this, cowEntities, drugEntities, drugsGivenEntities, penEntities, lotEntities, archivedLotEntities, loadEntities, callEntities, feedEntities, userEntities).execute(context);
//            } else {
//                onDatabaseSynced.onDatabaseSynced(Constants.ERROR_ACTIVITY_DESTROYED_BEFORE_LOADED);
//            }
        } else {
            onDatabaseSynced.onDatabaseSynced(resultCode);
        }
    }

    @Override
    public void onDatabaseCloned() {
//        if (mIsSyncAvailable) {
            onDatabaseSynced.onDatabaseSynced(Constants.SUCCESS);
//        } else {
//            onDatabaseSynced.onDatabaseSynced(Constants.ERROR_ACTIVITY_DESTROYED_BEFORE_LOADED);
//        }
    }

    public void setSyncAvailability(boolean isSyncAvailable) {
        mIsSyncAvailable = isSyncAvailable;
    }
}
