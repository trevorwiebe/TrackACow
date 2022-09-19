package com.trevorwiebe.trackacow.domain.dataLoaders.main.lot;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.db.AppDatabase;

public class UpdateLotWithNewPenId extends AsyncTask<Context, Void, Void> {

    private String lotId;
    private String penId;

    public UpdateLotWithNewPenId(String lotId, String penId) {
        this.lotId = lotId;
        this.penId = penId;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).lotDao().updateLotWithNewPenId(lotId, penId);
        return null;
    }
}
