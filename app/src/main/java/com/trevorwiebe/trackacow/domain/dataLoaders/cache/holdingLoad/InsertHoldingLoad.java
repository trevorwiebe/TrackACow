package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingLoad;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.db.AppDatabase;
import com.trevorwiebe.trackacow.data.db.holdingUpdateEntities.HoldingLoadEntity;

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
