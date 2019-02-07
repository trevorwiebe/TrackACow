package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.PenEntity;

public class InsertPen extends AsyncTask<Context, Void, Void> {

    private PenEntity penEntity;

    public InsertPen(PenEntity penEntity){
        this.penEntity = penEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).penDao().insertPen(penEntity);
        return null;
    }

}
