package com.trevorwiebe.trackacow.dataLoaders.cache.holdingLoad;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLoadEntity;

public class InsertHoldingLoad extends AsyncTask<Context, Void, Void> {

    private HoldingLoadEntity holdingLoadEntity;

    public InsertHoldingLoad(HoldingLoadEntity holdingLoadEntity) {
        this.holdingLoadEntity = holdingLoadEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).holdingLoadDao().insertHoldingLoad(holdingLoadEntity);
        return null;
    }
}
