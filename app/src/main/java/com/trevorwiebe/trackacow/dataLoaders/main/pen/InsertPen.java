package com.trevorwiebe.trackacow.dataLoaders.main.pen;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.PenEntity;

public class InsertPen extends AsyncTask<Context, Void, Void> {

    private PenEntity penEntity;
    private OnPenInserted onPenInserted;

    public InsertPen(PenEntity penEntity, OnPenInserted onPenInserted){
        this.penEntity = penEntity;
        this.onPenInserted = onPenInserted;
    }

    public interface OnPenInserted{
        void onPenInserted();
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).penDao().insertPen(penEntity);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onPenInserted.onPenInserted();
    }
}
