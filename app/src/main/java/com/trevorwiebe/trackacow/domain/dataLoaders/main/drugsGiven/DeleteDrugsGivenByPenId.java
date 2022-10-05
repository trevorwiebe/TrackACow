package com.trevorwiebe.trackacow.domain.dataLoaders.main.drugsGiven;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;

@Deprecated(since="Use use-cases instead")
public class DeleteDrugsGivenByPenId extends AsyncTask<Context, Void, Void> {

    private String penId;

    public DeleteDrugsGivenByPenId(String penId){
        this.penId = penId;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().deleteDrugsGivenByLotId(penId);
        return null;
    }
}
