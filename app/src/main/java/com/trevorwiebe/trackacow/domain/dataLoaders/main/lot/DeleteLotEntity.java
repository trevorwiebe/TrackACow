package com.trevorwiebe.trackacow.domain.dataLoaders.main.lot;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;

@Deprecated(since="Use use-cases instead")
public class DeleteLotEntity extends AsyncTask<Context, Void, Void> {

    private String lotId;

    public DeleteLotEntity(String lotId) {
        this.lotId = lotId;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).lotDao().deleteLotEntity(lotId);
        return null;
    }

}
