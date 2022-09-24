package com.trevorwiebe.trackacow.domain.dataLoaders.main.call;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.db.AppDatabase;

public class UpdateCallById extends AsyncTask<Context, Void, Void> {

    private int call;
    private String callId;

    public UpdateCallById(int call, String callId) {
        this.call = call;
        this.callId = callId;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
//        AppDatabase.getAppDatabase(contexts[0]).callDao().updateCallByCallId(callId, call);
        return null;
    }

}
