package com.trevorwiebe.trackacow.domain.dataLoaders.main.pen;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.PenEntity;

@Deprecated(since="Use use-cases instead")
public class DeletePen extends AsyncTask<Context, Void, Void> {

    private PenEntity penEntity;

    public DeletePen(PenEntity penEntity){
        this.penEntity = penEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).penDao().deletePen(penEntity);
        return null;
    }
}