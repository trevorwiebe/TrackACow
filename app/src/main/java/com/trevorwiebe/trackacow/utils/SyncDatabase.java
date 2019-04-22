package com.trevorwiebe.trackacow.utils;

import android.content.Context;
import android.util.Log;

import com.trevorwiebe.trackacow.dataLoaders.QueryAllCloudData;
import com.trevorwiebe.trackacow.dataLoaders.CloneCloudDatabaseToLocalDatabase;
import com.trevorwiebe.trackacow.dataLoaders.InsertAllLocalChangeToCloud;
import com.trevorwiebe.trackacow.db.entities.ArchivedLotEntity;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.LoadEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;

import java.util.ArrayList;

public class SyncDatabase implements
        InsertAllLocalChangeToCloud.OnAllLocalDbInsertedToCloud,
        QueryAllCloudData.OnAllCloudDataLoaded,
        CloneCloudDatabaseToLocalDatabase.OnDatabaseCloned {

    private OnDatabaseSynced onDatabaseSynced;
    private Context context;

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
                new InsertAllLocalChangeToCloud(this).execute(context);
            } else {
                new QueryAllCloudData(this).loadCloudData();
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
    public void onAllCloudDataLoaded(int resultCode, ArrayList<CowEntity> cowEntities, ArrayList<DrugEntity> drugEntities, ArrayList<DrugsGivenEntity> drugsGivenEntities, ArrayList<PenEntity> penEntities, ArrayList<LotEntity> lotEntities, ArrayList<ArchivedLotEntity> archivedLotEntities, ArrayList<LoadEntity> loadEntities) {
        if (resultCode == Constants.SUCCESS) {
            new CloneCloudDatabaseToLocalDatabase(this, cowEntities, drugEntities, drugsGivenEntities, penEntities, lotEntities, archivedLotEntities, loadEntities).execute(context);
        } else {
            onDatabaseSynced.onDatabaseSynced(resultCode);
        }
    }

    @Override
    public void onDatabaseCloned() {
        onDatabaseSynced.onDatabaseSynced(Constants.SUCCESS);
    }
}
