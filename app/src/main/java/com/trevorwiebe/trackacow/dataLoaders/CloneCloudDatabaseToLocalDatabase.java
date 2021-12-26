package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.ui.auth.data.model.User;
import com.trevorwiebe.trackacow.db.AppDatabase;
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

public class CloneCloudDatabaseToLocalDatabase extends AsyncTask<Context, Void, Void> {

    private ArrayList<CowEntity> mCowEntityUpdateList;
    private ArrayList<DrugEntity> mDrugEntityUpdateList;
    private ArrayList<DrugsGivenEntity> mDrugsGivenEntityUpdateList;
    private ArrayList<PenEntity> mPenEntityUpdateList;
    private ArrayList<LotEntity> mLotEntityList;
    private ArrayList<ArchivedLotEntity> mArchivedLotEntityList;
    private ArrayList<LoadEntity> mLoadEntityList;
    private ArrayList<CallEntity> mCallEntityList;
    private ArrayList<FeedEntity> mFeedEntityList;
    private ArrayList<UserEntity> mUserEntityList;
    private OnDatabaseCloned onDatabaseCloned;

    public interface OnDatabaseCloned{
        void onDatabaseCloned();
    }

    public CloneCloudDatabaseToLocalDatabase(OnDatabaseCloned onDatabaseCloned, ArrayList<CowEntity> cowEntities, ArrayList<DrugEntity> drugEntities, ArrayList<DrugsGivenEntity> drugsGivenEntities, ArrayList<PenEntity> penEntities, ArrayList<LotEntity> lotEntities, ArrayList<ArchivedLotEntity> archivedLotEntities, ArrayList<LoadEntity> loadEntities, ArrayList<CallEntity> callEntities, ArrayList<FeedEntity> feedEntities, ArrayList<UserEntity> userEntities) {
        this.onDatabaseCloned = onDatabaseCloned;
        this.mCowEntityUpdateList = cowEntities;
        this.mDrugEntityUpdateList = drugEntities;
        this.mDrugsGivenEntityUpdateList = drugsGivenEntities;
        this.mPenEntityUpdateList = penEntities;
        this.mLotEntityList = lotEntities;
        this.mArchivedLotEntityList = archivedLotEntities;
        this.mLoadEntityList = loadEntities;
        this.mCallEntityList = callEntities;
        this.mFeedEntityList = feedEntities;
        this.mUserEntityList = userEntities;
    }

    @Override
    protected Void doInBackground(Context... contexts) {

        AppDatabase db = AppDatabase.getAppDatabase(contexts[0]);

        db.cowDao().deleteCowTable();
        db.drugDao().deleteDrugTable();
        db.drugsGivenDao().deleteDrugsGivenTable();
        db.penDao().deletePenTable();
        db.lotDao().deleteLotEntityTable();
        db.archivedLotDao().deleteArchivedLotTable();
        db.loadDao().deleteLoadTable();
        db.callDao().deleteCallTable();
        db.feedDao().deleteFeedTable();
        db.userDao().deleteUserTable();

        db.cowDao().insertCowList(mCowEntityUpdateList);
        db.drugDao().insertListDrug(mDrugEntityUpdateList);
        db.drugsGivenDao().insertDrugsGivenList(mDrugsGivenEntityUpdateList);
        db.penDao().insertPenList(mPenEntityUpdateList);
        db.lotDao().insertLotEntityList(mLotEntityList);
        db.archivedLotDao().insertArchivedLotEntityList(mArchivedLotEntityList);
        db.loadDao().insertLoadList(mLoadEntityList);
        db.callDao().insertCallList(mCallEntityList);
        db.feedDao().insertFeedEntityList(mFeedEntityList);
        db.userDao().insertUserList(mUserEntityList);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onDatabaseCloned.onDatabaseCloned();
    }
}
