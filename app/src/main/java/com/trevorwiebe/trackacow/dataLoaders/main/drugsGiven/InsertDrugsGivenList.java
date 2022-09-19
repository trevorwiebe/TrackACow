package com.trevorwiebe.trackacow.dataLoaders.main.drugsGiven;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;

import java.util.ArrayList;
import java.util.List;

public class InsertDrugsGivenList extends AsyncTask<Context, Void, Void> {

    private static final String TAG = "InsertDrugsGivenList";

    private List<DrugsGivenEntity> mDrugsGivenEntity;

    public InsertDrugsGivenList(List<DrugsGivenEntity> drugsGivenEntities){
        this.mDrugsGivenEntity = drugsGivenEntities;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().insertDrugsGivenList(mDrugsGivenEntity);
        return null;
    }
}