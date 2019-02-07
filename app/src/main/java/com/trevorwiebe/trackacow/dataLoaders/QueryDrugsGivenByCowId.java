package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryDrugsGivenByCowId extends AsyncTask<Context, Void, ArrayList<DrugsGivenEntity>> {

    private String cowId;
    private OnDrugsGivenByCowIdLoaded mOnDrugsGivenByCowIdLoaded;

    public QueryDrugsGivenByCowId(OnDrugsGivenByCowIdLoaded onDrugsGivenByCowIdLoaded, String cowId){
        this.mOnDrugsGivenByCowIdLoaded = onDrugsGivenByCowIdLoaded;
        this.cowId = cowId;
    }

    public interface OnDrugsGivenByCowIdLoaded{
        void onDrugsLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities);
    }

    @Override
    protected ArrayList<DrugsGivenEntity> doInBackground(Context... contexts) {
        List<DrugsGivenEntity> drugsGivenEntities = AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().getDrugsGivenByCowId(cowId);
        return (ArrayList<DrugsGivenEntity>) drugsGivenEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        super.onPostExecute(drugsGivenEntities);
        mOnDrugsGivenByCowIdLoaded.onDrugsLoaded(drugsGivenEntities);
    }
}
