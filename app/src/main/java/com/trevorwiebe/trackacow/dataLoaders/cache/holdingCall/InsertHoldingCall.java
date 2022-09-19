package com.trevorwiebe.trackacow.dataLoaders.cache.holdingCall;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingCallEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingCowEntity;

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
