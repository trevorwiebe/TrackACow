package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryDrugsGivenByPenId extends AsyncTask<Context, Void, ArrayList<DrugsGivenEntity>> {

    private OnDrugsGivenLoaded mOnDrugsGivenLoaded;
    private String mPenId;

    public QueryDrugsGivenByPenId(OnDrugsGivenLoaded onDrugsGivenLoaded, String penId){
        this.mOnDrugsGivenLoaded = onDrugsGivenLoaded;
        this.mPenId = penId;
    }

    public interface OnDrugsGivenLoaded {
        void onDrugsGivenLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities);
    }

    @Override
    protected ArrayList<DrugsGivenEntity> doInBackground(Context... contexts) {
        List<DrugsGivenEntity> drugsGivenEntities = AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().getDrugsGivenByPenId(mPenId);
        return (ArrayList<DrugsGivenEntity>) drugsGivenEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        super.onPostExecute(drugsGivenEntities);
        mOnDrugsGivenLoaded.onDrugsGivenLoaded(drugsGivenEntities);
    }
}
