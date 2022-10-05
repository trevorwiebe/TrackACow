package com.trevorwiebe.trackacow.domain.dataLoaders.main.drugsGiven;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;

@Deprecated(since="Use use-cases instead")
public class UpdateDrugsGivenDateByCowId extends AsyncTask<Context, Void, Void> {

    private OnDrugsGivenByCowIdUpdated onDrugsGivenByCowIdUpdated;
    private String cowId;
    private long date;

    public UpdateDrugsGivenDateByCowId(OnDrugsGivenByCowIdUpdated onDrugsGivenByCowIdUpdated, String cowId, long date) {
        this.onDrugsGivenByCowIdUpdated = onDrugsGivenByCowIdUpdated;
        this.cowId = cowId;
        this.date = date;
    }

    public interface OnDrugsGivenByCowIdUpdated {
        void onDrugsGivenByCowIdUpdated();
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).drugsGivenDao().updateDrugsGivenDateByCowId(date, cowId);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onDrugsGivenByCowIdUpdated.onDrugsGivenByCowIdUpdated();
    }
}
