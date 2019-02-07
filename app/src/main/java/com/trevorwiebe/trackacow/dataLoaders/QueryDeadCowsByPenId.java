package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.CowEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryDeadCowsByPenId extends AsyncTask<Context, Void, ArrayList<CowEntity>> {

    public OnDeadCowsLoaded mOnDeadCowsLoaded;
    public String mPenId;

    public QueryDeadCowsByPenId(OnDeadCowsLoaded onDeadCowsLoaded, String penId){
        this.mOnDeadCowsLoaded = onDeadCowsLoaded;
        this.mPenId = penId;
    }

    public interface OnDeadCowsLoaded{
        void onDeadCowsLoaded(ArrayList<CowEntity> cowEntities);
    }

    @Override
    protected ArrayList<CowEntity> doInBackground(Context... contexts) {
        List<CowEntity> cowEntities = AppDatabase.getAppDatabase(contexts[0]).cowDao().getDeadCowEntitiesByPenId(mPenId);
        return (ArrayList<CowEntity>) cowEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<CowEntity> cowEntities) {
        super.onPostExecute(cowEntities);
        mOnDeadCowsLoaded.onDeadCowsLoaded(cowEntities);
    }
}
