package com.trevorwiebe.trackacow.domain.dataLoaders.main.drugsGiven;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryDrugsGivenByCowId extends AsyncTask<Context, Void, ArrayList<DrugsGivenEntity>> {

    private String cowId;
    private int id;
    private OnDrugsGivenByCowIdLoaded mOnDrugsGivenByCowIdLoaded;

    public QueryDrugsGivenByCowId(OnDrugsGivenByCowIdLoaded onDrugsGivenByCowIdLoaded, int id, String cowId) {
        this.mOnDrugsGivenByCowIdLoaded = onDrugsGivenByCowIdLoaded;
        this.id = id;
        this.cowId = cowId;
    }

    public interface OnDrugsGivenByCowIdLoaded{
        void onDrugsLoadedByCowId(ArrayList<DrugsGivenEntity> drugsGivenEntities, int id);
    }

    @Override
    protected ArrayList<DrugsGivenEntity> doInBackground(Context... contexts) {
        List<DrugsGivenEntity> drugsGivenEntities = AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().getDrugsGivenByCowId(cowId);
        return (ArrayList<DrugsGivenEntity>) drugsGivenEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        super.onPostExecute(drugsGivenEntities);
        mOnDrugsGivenByCowIdLoaded.onDrugsLoadedByCowId(drugsGivenEntities, id);
    }
}
