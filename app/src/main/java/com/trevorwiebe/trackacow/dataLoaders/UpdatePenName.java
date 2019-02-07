package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;

public class UpdatePenName extends AsyncTask<Context, Void, Void> {

    private String penId;
    private String penName;
    private OnPenNameUpdated mOnPenNameUpdated;

    public UpdatePenName(String penId, String penName, OnPenNameUpdated onPenNameUpdated){
        this.penId = penId;
        this.penName = penName;
        this.mOnPenNameUpdated = onPenNameUpdated;
    }

    public interface OnPenNameUpdated{
        void onPenNameUpdated();
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).penDao().updatePenNameById(penName, penId);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mOnPenNameUpdated.onPenNameUpdated();
    }
}
