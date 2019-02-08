package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;

public class DeleteDrug extends AsyncTask<Context, Void, Void> {

    private DrugEntity drugEntity;

    public DeleteDrug(DrugEntity drugEntity){
        this.drugEntity = drugEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        String drugId = drugEntity.getDrugId();
        AppDatabase.getAppDatabase(contexts[0]).drugDao().deleteDrugById(drugId);
        return null;
    }
}
