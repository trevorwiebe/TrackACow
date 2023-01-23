package com.trevorwiebe.trackacow.domain.dataLoaders.main.cow;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;

@Deprecated(since="Use use-cases instead")
public class UpdateCow extends AsyncTask<Context, Void, Void> {

    private String cowEntityId;
    private int tagNumber;
    private long date;
    private String notes;
    private OnCowUpdated onCowUpdated;

    public interface OnCowUpdated {
        void onCowUpdated();
    }

    public UpdateCow(OnCowUpdated onCowUpdated, String cowEntityId, long date, int tagNumber, String notes) {
        this.onCowUpdated = onCowUpdated;
        this.cowEntityId = cowEntityId;
        this.tagNumber = tagNumber;
        this.date = date;
        this.notes = notes;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).cowDao().updateCowById2(cowEntityId, tagNumber, date, notes);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onCowUpdated.onCowUpdated();
    }
}
