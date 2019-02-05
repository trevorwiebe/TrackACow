package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;

import java.util.ArrayList;
import java.util.List;

public class LoadDrugsGiven extends AsyncTask<Context, Void, ArrayList<DrugsGivenEntity>> {

    private static final String TAG = "LoadDrugsGiven";

    private ArrayList<DrugsGivenEntity> mDrugsGivenEntity;
    private OnDrugsLoaded mOnDrugsLoaded;

    public LoadDrugsGiven(OnDrugsLoaded onDrugsLoaded, ArrayList<DrugsGivenEntity> drugsGivenEntities){
        this.mOnDrugsLoaded = onDrugsLoaded;
        this.mDrugsGivenEntity = drugsGivenEntities;
    }

    public interface OnDrugsLoaded{
        void onDrugsLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities);
    }

    @Override
    protected ArrayList<DrugsGivenEntity> doInBackground(Context... contexts) {
        List<DrugsGivenEntity> drugsGivenEntities = AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().getDrugsGivenList();
        return (ArrayList<DrugsGivenEntity>) drugsGivenEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        super.onPostExecute(drugsGivenEntities);
        mOnDrugsLoaded.onDrugsLoaded(mDrugsGivenEntity);
    }
}
