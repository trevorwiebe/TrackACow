package com.trevorwiebe.trackacow.domain.dataLoaders.main.lot;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.LotEntity;

@Deprecated(since="Use use-cases instead")
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
