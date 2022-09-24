package com.trevorwiebe.trackacow.domain.dataLoaders.main.cow;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;

public class DeleteCowsByPenId extends AsyncTask<Context, Void, Void> {

    private String penId;

    public DeleteCowsByPenId(String penId){
        this.penId = penId;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).cowDao().deleteCowsByLotId(penId);
        return null;
    }

}
