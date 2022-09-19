package com.trevorwiebe.trackacow.dataLoaders.main.drugsGiven;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;

public class DeleteDrugsGivenByCowId extends AsyncTask<Context, Void, Void> {

    private String cowId;

    public DeleteDrugsGivenByCowId(String cowId){
        this.cowId = cowId;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().deleteDrugsGivenByCowId(cowId);
        return null;
    }

}
