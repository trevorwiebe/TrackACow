package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;

public class QueryDrugsGivenByDrugsGivenId extends AsyncTask<Context, Void, DrugsGivenEntity> {

    private String drugGivenId;
    private OnDrugsGivenByDrugsGivenIdLoaded onDrugsGivenByDrugsGivenIdLoaded;

    public QueryDrugsGivenByDrugsGivenId(String drugGivenId, OnDrugsGivenByDrugsGivenIdLoaded onDrugsGivenByDrugsGivenIdLoaded) {
        this.drugGivenId = drugGivenId;
        this.onDrugsGivenByDrugsGivenIdLoaded = onDrugsGivenByDrugsGivenIdLoaded;
    }

    public interface OnDrugsGivenByDrugsGivenIdLoaded {
        void onDrugsGivenByDrugsGivenIdLoaded(DrugsGivenEntity drugsGivenEntity);
    }

    @Override
    protected DrugsGivenEntity doInBackground(Context... contexts) {
        return AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().getDrugGivenByDrugGivenId(drugGivenId);
    }

    @Override
    protected void onPostExecute(DrugsGivenEntity drugsGivenEntity) {
        super.onPostExecute(drugsGivenEntity);
        onDrugsGivenByDrugsGivenIdLoaded.onDrugsGivenByDrugsGivenIdLoaded(drugsGivenEntity);
    }
}
