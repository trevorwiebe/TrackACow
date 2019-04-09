package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.FeedEntity;

import java.util.ArrayList;

public class InsertFeedEntities extends AsyncTask<Context, Void, Void> {

    private ArrayList<FeedEntity> feedEntities;

    public InsertFeedEntities(ArrayList<FeedEntity> feedEntities) {
        this.feedEntities = feedEntities;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).feedDao().insertFeedEntityList(feedEntities);
        return null;
    }
}
