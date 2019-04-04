package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.CallEntity;

public class InsertCallEntity extends AsyncTask<Context, Void, Void> {

    private CallEntity callEntity;

    public InsertCallEntity(CallEntity callEntity) {
        this.callEntity = callEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).callDao().insertCall(callEntity);
        return null;
    }
}
