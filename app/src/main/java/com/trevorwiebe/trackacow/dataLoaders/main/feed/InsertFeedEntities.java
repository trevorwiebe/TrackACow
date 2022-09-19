package com.trevorwiebe.trackacow.dataLoaders.main.feed;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.FeedEntity;

import java.util.ArrayList;
import java.util.List;

public class InsertFeedEntities extends AsyncTask<Context, Void, Void> {

    private List<FeedEntity> feedEntities;

    public InsertFeedEntities(List<FeedEntity> feedEntities) {
        this.feedEntities = feedEntities;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).feedDao().insertFeedEntityList(feedEntities);
        return null;
    }
}
