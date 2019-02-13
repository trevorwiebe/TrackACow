package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.CowEntity;

public class UpdateCow extends AsyncTask<Context, Void, Void> {

    private String cowEntityId;
    private int tagNumber;
    private String notes;

    public UpdateCow(String cowEntityId, int tagNumber, String notes){
        this.cowEntityId = cowEntityId;
        this.tagNumber = tagNumber;
        this.notes = notes;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).cowDao().updateCowById(cowEntityId, tagNumber, notes);
        return null;
    }

}
