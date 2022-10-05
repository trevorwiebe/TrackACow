package com.trevorwiebe.trackacow.domain.dataLoaders.main.drugsGiven;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity;

import java.util.ArrayList;
import java.util.List;

@Deprecated(since="Use use-cases instead")
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
