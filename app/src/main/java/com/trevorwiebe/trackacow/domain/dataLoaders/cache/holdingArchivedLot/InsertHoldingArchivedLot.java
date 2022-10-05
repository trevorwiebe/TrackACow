package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingArchivedLot;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.cacheEntities.CacheArchivedLotEntity;
import com.trevorwiebe.trackacow.data.local.AppDatabase;

@Deprecated(since="Use use-cases instead")
public class InsertHoldingArchivedLot extends AsyncTask<Context, Void, Void> {

    private CacheArchivedLotEntity cacheArchivedLotEntity;

    public InsertHoldingArchivedLot(CacheArchivedLotEntity cacheArchivedLotEntity) {
        this.cacheArchivedLotEntity = cacheArchivedLotEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).cacheArchivedLotDao().insertHoldingArchivedLot(cacheArchivedLotEntity);
        return null;
    }
}
