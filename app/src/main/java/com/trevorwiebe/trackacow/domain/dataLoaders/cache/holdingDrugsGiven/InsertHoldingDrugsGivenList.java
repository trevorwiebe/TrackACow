package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingDrugsGiven;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugsGivenEntity;
import com.trevorwiebe.trackacow.data.local.AppDatabase;

import java.util.ArrayList;

public class InsertHoldingDrugsGivenList extends AsyncTask<Context, Void, Void> {

    private ArrayList<CacheDrugsGivenEntity> holdingDrugsGivenEntities;

    public InsertHoldingDrugsGivenList(ArrayList<CacheDrugsGivenEntity> holdingDrugsGivenEntities){
        this.holdingDrugsGivenEntities = holdingDrugsGivenEntities;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).cacheDrugsGivenDao().insertHoldingDrugsGivenList(holdingDrugsGivenEntities);
        return null;
    }
}
