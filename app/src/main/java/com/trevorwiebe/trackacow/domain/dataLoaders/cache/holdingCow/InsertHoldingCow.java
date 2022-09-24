package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingCow;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.local.holdingUpdateEntities.HoldingCowEntity;

public class InsertHoldingCow extends AsyncTask<Context, Void, Void> {

    private HoldingCowEntity holdingCowEntity;

    public InsertHoldingCow(HoldingCowEntity holdingCowEntity){
        this.holdingCowEntity = holdingCowEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).holdingCowDao().insertHoldingCow(holdingCowEntity);
        return null;
    }
}
