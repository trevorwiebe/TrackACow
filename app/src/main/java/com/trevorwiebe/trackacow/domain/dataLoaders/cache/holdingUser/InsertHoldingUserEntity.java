package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingUser;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.cacheEntities.CacheUserEntity;
import com.trevorwiebe.trackacow.data.local.AppDatabase;

public class InsertHoldingUserEntity extends AsyncTask<Context, Void, Void> {

    private CacheUserEntity cacheUserEntity;

    public InsertHoldingUserEntity(CacheUserEntity cacheUserEntity) {
        this.cacheUserEntity = cacheUserEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).cacheUserDao().insertHoldingUser(cacheUserEntity);
        return null;
    }
}
