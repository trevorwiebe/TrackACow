package com.trevorwiebe.trackacow.dataLoaders.main.lot;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.LotEntity;

public class InsertLotEntity extends AsyncTask<Context, Void, Void> {

    private LotEntity lotEntity;

    public InsertLotEntity(LotEntity lotEntity) {
        this.lotEntity = lotEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).lotDao().insertLot(lotEntity);
        return null;
    }
}
