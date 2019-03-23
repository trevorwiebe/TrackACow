package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.LotEntity;

public class QueryLotByLotId extends AsyncTask<Context, Void, LotEntity> {

    private String lotId;
    private OnLotByLotIdLoaded onLotByLotIdLoaded;

    public QueryLotByLotId(String lotId, OnLotByLotIdLoaded onLotByLotIdLoaded) {
        this.lotId = lotId;
        this.onLotByLotIdLoaded = onLotByLotIdLoaded;
    }

    public interface OnLotByLotIdLoaded {
        void onLotByLotIdLoaded(LotEntity lotEntity);
    }

    @Override
    protected LotEntity doInBackground(Context... contexts) {
        return AppDatabase.getAppDatabase(contexts[0]).lotDao().getLotEntityById(lotId);
    }

    @Override
    protected void onPostExecute(LotEntity lotEntity) {
        super.onPostExecute(lotEntity);
        onLotByLotIdLoaded.onLotByLotIdLoaded(lotEntity);
    }
}
