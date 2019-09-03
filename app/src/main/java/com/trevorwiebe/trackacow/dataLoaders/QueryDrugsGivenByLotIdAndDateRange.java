package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;

import java.util.ArrayList;

public class QueryDrugsGivenByLotIdAndDateRange extends AsyncTask<Context, Void, ArrayList<DrugsGivenEntity>> {

    @Override
    protected ArrayList<DrugsGivenEntity> doInBackground(Context... contexts) {
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<DrugsGivenEntity> drugsGivenEntities) {
        super.onPostExecute(drugsGivenEntities);
    }
}

