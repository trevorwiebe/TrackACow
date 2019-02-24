package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;

public class DeleteDrugsGivenByDrugsGivenId extends AsyncTask<Context, Void, Void> {

    private String drugsGivenId;

    public DeleteDrugsGivenByDrugsGivenId(String drugsGivenId) {
        this.drugsGivenId = drugsGivenId;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().deleteDrugGivenById(drugsGivenId);
        return null;
    }

}
