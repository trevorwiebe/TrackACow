package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingUser;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.local.holdingUpdateEntities.HoldingUserEntity;

public class InsertHoldingUserEntity extends AsyncTask<Context, Void, Void> {

    private HoldingUserEntity holdingUserEntity;

    public InsertHoldingUserEntity(HoldingUserEntity holdingUserEntity) {
        this.holdingUserEntity = holdingUserEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).holdingUserDao().insertHoldingUser(holdingUserEntity);
        return null;
    }
}
