package com.trevorwiebe.trackacow.dataLoaders.main.feed;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;

public class DeleteFeedEntitiesById extends AsyncTask<Context, Void, Integer> {

    private String id;

    public DeleteFeedEntitiesById(String id){
        this.id = id;
    }

    @Override
    protected Integer doInBackground(Context... contexts) {
        return AppDatabase.getAppDatabase(contexts[0]).feedDao().deleteFeedEntityById(id);
    }

}
