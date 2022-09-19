package com.trevorwiebe.trackacow.domain.dataLoaders.main.cow;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.db.AppDatabase;
import com.trevorwiebe.trackacow.data.db.entities.CowEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryMedicatedCowsByLotIds extends AsyncTask<Context, Void, ArrayList<CowEntity>> {

    private ArrayList<String> mLotIds;
    private OnCowsByLotIdLoaded mOnCowsByLotIdLoaded;

    public QueryMedicatedCowsByLotIds(OnCowsByLotIdLoaded onCowsByLotIdLoaded, ArrayList<String> lotIds) {
        this.mLotIds = lotIds;
        this.mOnCowsByLotIdLoaded = onCowsByLotIdLoaded;
    }

    public interface OnCowsByLotIdLoaded {
        void onCowsByLotIdLoaded(ArrayList<CowEntity> cowObjectList);
    }

    @Override
    protected ArrayList<CowEntity> doInBackground(Context... contexts) {
        List<CowEntity> cowEntities = AppDatabase.getAppDatabase(contexts[0]).cowDao().getCowEntitiesByLotIds(mLotIds);
        return (ArrayList<CowEntity>) cowEntities;
    }

    @Override
    protected void onPostExecute(ArrayList<CowEntity> cowEntities) {
        super.onPostExecute(cowEntities);
        mOnCowsByLotIdLoaded.onCowsByLotIdLoaded(cowEntities);
    }
}
