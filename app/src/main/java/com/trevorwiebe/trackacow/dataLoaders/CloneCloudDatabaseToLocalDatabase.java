package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;

import java.util.ArrayList;

public class CloneCloudDatabaseToLocalDatabase extends AsyncTask<Context, Void, Void> {

    private ArrayList<CowEntity> mCowEntityUpdateList;
    private ArrayList<DrugEntity> mDrugEntityUpdateList;
    private ArrayList<DrugsGivenEntity> mDrugsGivenEntityUpdateList;
    private ArrayList<PenEntity> mPenEntityUpdateList;
    private ArrayList<LotEntity> mLotEntityList;
    private OnDatabaseCloned onDatabaseCloned;

    public interface OnDatabaseCloned{
        void onDatabaseCloned();
    }

    public CloneCloudDatabaseToLocalDatabase(OnDatabaseCloned onDatabaseCloned, ArrayList<CowEntity> cowEntities, ArrayList<DrugEntity> drugEntities, ArrayList<DrugsGivenEntity> drugsGivenEntities, ArrayList<PenEntity> penEntities, ArrayList<LotEntity> lotEntities) {
        this.onDatabaseCloned = onDatabaseCloned;
        this.mCowEntityUpdateList = cowEntities;
        this.mDrugEntityUpdateList = drugEntities;
        this.mDrugsGivenEntityUpdateList = drugsGivenEntities;
        this.mPenEntityUpdateList = penEntities;
        this.mLotEntityList = lotEntities;
    }

    @Override
    protected Void doInBackground(Context... contexts) {

        AppDatabase db = AppDatabase.getAppDatabase(contexts[0]);

        db.cowDao().deleteCowTable();
        db.drugDao().deleteDrugTable();
        db.drugsGivenDao().deleteDrugsGivenTable();
        db.penDao().deletePenTable();
        db.lotDao().deleteLotEntityList();

        db.cowDao().insertCowList(mCowEntityUpdateList);
        db.drugDao().insertListDrug(mDrugEntityUpdateList);
        db.drugsGivenDao().insertDrugsGivenList(mDrugsGivenEntityUpdateList);
        db.penDao().insertPenList(mPenEntityUpdateList);
        db.lotDao().insertLotEntityList(mLotEntityList);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onDatabaseCloned.onDatabaseCloned();
    }
}
