package com.trevorwiebe.trackacow.domain.dataLoaders.main.drug;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.db.AppDatabase;
import com.trevorwiebe.trackacow.data.db.entities.DrugEntity;

public class QueryDrugByDrugId extends AsyncTask<Context, Void, DrugEntity> {

    private String drugId;
    private OnDrugByDrugIdLoaded onDrugByDrugIdLoaded;

    public QueryDrugByDrugId(String drugId, OnDrugByDrugIdLoaded onDrugByDrugIdLoaded) {
        this.drugId = drugId;
        this.onDrugByDrugIdLoaded = onDrugByDrugIdLoaded;
    }

    public interface OnDrugByDrugIdLoaded {
        void onDrugByDrugIdLoaded(DrugEntity drugEntity);
    }

    @Override
    protected DrugEntity doInBackground(Context... contexts) {
        return AppDatabase.getAppDatabase(contexts[0]).drugDao().getDrugById(drugId);
    }

    @Override
    protected void onPostExecute(DrugEntity drugEntity) {
        super.onPostExecute(drugEntity);
        onDrugByDrugIdLoaded.onDrugByDrugIdLoaded(drugEntity);
    }
}
