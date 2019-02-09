package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingCowEntity;

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
