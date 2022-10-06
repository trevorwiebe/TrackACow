package com.trevorwiebe.trackacow.domain.utils;

import android.content.Context;

import com.trevorwiebe.trackacow.data.entities.RationEntity;
import com.trevorwiebe.trackacow.domain.dataLoaders.misc.QueryAllCloudData;
import com.trevorwiebe.trackacow.domain.dataLoaders.misc.CloneCloudDatabaseToLocalDatabase;
import com.trevorwiebe.trackacow.domain.dataLoaders.misc.InsertAllLocalChangeToCloud;
import com.trevorwiebe.trackacow.data.entities.ArchivedLotEntity;
import com.trevorwiebe.trackacow.data.entities.CallEntity;
import com.trevorwiebe.trackacow.data.entities.CowEntity;
import com.trevorwiebe.trackacow.data.entities.DrugEntity;
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.data.entities.FeedEntity;
import com.trevorwiebe.trackacow.data.entities.LoadEntity;
import com.trevorwiebe.trackacow.data.entities.LotEntity;
import com.trevorwiebe.trackacow.data.entities.PenEntity;
import com.trevorwiebe.trackacow.data.entities.UserEntity;

import java.util.ArrayList;

@Deprecated(since = "must build new syncing mechanism")
public class SyncDatabase implements
        InsertAllLocalChangeToCloud.OnAllLocalDbInsertedToCloud,
        QueryAllCloudData.OnAllCloudDataLoaded,
        CloneCloudDatabaseToLocalDatabase.OnDatabaseCloned {

    private final OnDatabaseSynced onDatabaseSynced;
    private final Context context;

    public SyncDatabase(OnDatabaseSynced onDatabaseSynced, Context context) {
        this.onDatabaseSynced = onDatabaseSynced;
        this.context = context;
    }

    public interface OnDatabaseSynced {
        void onDatabaseSynced(int resultCode);
    }

    public void beginSync() {

        if (Utility.haveNetworkConnection(context)) {
            if (Utility.isThereNewDataToUpload(context)) {
                new InsertAllLocalChangeToCloud(SyncDatabase.this).execute(context);
            } else {
                new QueryAllCloudData(SyncDatabase.this).loadCloudData();
            }
        } else {
            onDatabaseSynced.onDatabaseSynced(Constants.NO_NETWORK_CONNECTION);
        }

    }

    @Override
    public void onAllLocalDbInsertedToCloud(int resultCode) {
        if (resultCode == Constants.SUCCESS) {
            new QueryAllCloudData(this).loadCloudData();
        } else {
            onDatabaseSynced.onDatabaseSynced(resultCode);
        }
    }

    @Override
    public void onAllCloudDataLoaded(
            int resultCode,
            ArrayList<CowEntity> cowEntities,
            ArrayList<DrugEntity> drugEntities,
            ArrayList<DrugsGivenEntity> drugsGivenEntities,
            ArrayList<PenEntity> penEntities,
            ArrayList<LotEntity> lotEntities,
            ArrayList<ArchivedLotEntity> archivedLotEntities,
            ArrayList<LoadEntity> loadEntities,
            ArrayList<CallEntity> callEntities,
            ArrayList<FeedEntity> feedEntities,
            ArrayList<UserEntity> userEntities,
            ArrayList<RationEntity> rationEntities
    ) {
        if (resultCode == Constants.SUCCESS) {
                new CloneCloudDatabaseToLocalDatabase(
                    this,
                    cowEntities,
                    drugEntities,
                    drugsGivenEntities,
                    penEntities,
                    lotEntities,
                    archivedLotEntities,
                    loadEntities,
                    callEntities,
                    feedEntities,
                    userEntities
                ).execute(context);
        } else {
            onDatabaseSynced.onDatabaseSynced(resultCode);
        }
    }

    @Override
    public void onDatabaseCloned() {
        onDatabaseSynced.onDatabaseSynced(Constants.SUCCESS);
        Utility.setLastSync(context, System.currentTimeMillis());
    }
}
