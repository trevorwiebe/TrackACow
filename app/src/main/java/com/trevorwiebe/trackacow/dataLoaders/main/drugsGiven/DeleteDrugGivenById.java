package com.trevorwiebe.trackacow.dataLoaders.main.drugsGiven;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;

public class DeleteDrugGivenById extends AsyncTask<Context, Void, Void> {

    private String drugGivenId;
    private OnDrugDelete mOnDrugDeleted;

    public DeleteDrugGivenById(OnDrugDelete onDrugDelete, String drugGivenId){
        this.mOnDrugDeleted = onDrugDelete;
        this.drugGivenId = drugGivenId;
    }

    public interface OnDrugDelete{
        void onDrugDeleted();
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().deleteDrugGivenById(drugGivenId);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mOnDrugDeleted.onDrugDeleted();
    }
}
