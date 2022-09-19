package com.trevorwiebe.trackacow.dataLoaders.main.drugsGiven;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryDrugsGivenByLotIds extends AsyncTask<Context, Void, ArrayList<DrugsGivenEntity>> {

    private OnDrugsGivenByLotIdLoaded mOnDrugsGivenByLotIdLoaded;
    private ArrayList<String> mLotIds;

    public QueryDrugsGivenByLotIds(OnDrugsGivenByLotIdLoaded onDrugsGivenByLotIdLoaded, ArrayList<String> lotIds) {
        this.mOnDrugsGivenByLotIdLoaded = onDrugsGivenByLotIdLoaded;
        this.mLotIds = lotIds;
    }

    public interface OnDrugsGivenByLotIdLoaded {
        void onDrugsGivenByLotIdLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities);
    }

    @Override
    protected ArrayList<DrugsGivenEntity> doInBackground(Context... contexts) {
        List<DrugsGivenEntity> drugsGivenEntities = AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().getDrugsGivenByLotIds(mLotIds);
        return (ArrayList<DrugsGivenEntity>) drugsGivenEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        super.onPostExecute(drugsGivenEntities);
        mOnDrugsGivenByLotIdLoaded.onDrugsGivenByLotIdLoaded(drugsGivenEntities);
    }
}
