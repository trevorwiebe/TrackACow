package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryDrugsGivenByCowIdList extends AsyncTask<Context, Void, ArrayList<DrugsGivenEntity>> {

    private ArrayList<String> cowIdList;
    public OnDrugsGivenByCowIdListLoaded mOnDrugsGivenByCowIdListLoaded;

    public QueryDrugsGivenByCowIdList(OnDrugsGivenByCowIdListLoaded onDrugsGivenByCowIdListLoaded, ArrayList<String> cowIdList){
        this.cowIdList = cowIdList;
        this.mOnDrugsGivenByCowIdListLoaded = onDrugsGivenByCowIdListLoaded;
    }

    public interface OnDrugsGivenByCowIdListLoaded{
        void onDrugsGivenByCowIdListLoaded(ArrayList<DrugsGivenEntity> drugsGivenEntities);
    }

    @Override
    protected ArrayList<DrugsGivenEntity> doInBackground(Context... contexts) {
        List<DrugsGivenEntity> drugsGivenEntityList = AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().getDrugsGivenByCowIdList(cowIdList);
        return (ArrayList<DrugsGivenEntity>) drugsGivenEntityList;
    }

    @Override
    protected void onPostExecute(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        super.onPostExecute(drugsGivenEntities);
        mOnDrugsGivenByCowIdListLoaded.onDrugsGivenByCowIdListLoaded(drugsGivenEntities);
    }
}
