package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingDrug;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugEntity;
import com.trevorwiebe.trackacow.data.local.AppDatabase;

@Deprecated(since="Use use-cases instead")
public class InsertHoldingDrug extends AsyncTask<Context, Void, Void> {

    private CacheDrugEntity cacheDrugEntity;

    public InsertHoldingDrug(CacheDrugEntity cacheDrugEntity){
        this.cacheDrugEntity = cacheDrugEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).cacheDrugDao().insertCacheDrug2(cacheDrugEntity);
        return null;
    }

}
