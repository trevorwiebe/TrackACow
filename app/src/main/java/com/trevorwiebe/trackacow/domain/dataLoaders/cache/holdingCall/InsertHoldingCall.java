package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingCall;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.db.AppDatabase;
import com.trevorwiebe.trackacow.data.db.holdingUpdateEntities.HoldingCallEntity;

public class InsertHoldingCall extends AsyncTask<Context, Void, Void> {

    private HoldingCallEntity holdingCallEntity;

    public InsertHoldingCall(HoldingCallEntity holdingCallEntity){
        this.holdingCallEntity = holdingCallEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).holdingCallDao().insertHoldingCall(holdingCallEntity);
        return null;
    }
}
