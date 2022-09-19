package com.trevorwiebe.trackacow.dataLoaders.main.drug;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;

public class InsertDrug extends AsyncTask<Context, Void, Void> {

    private DrugEntity drugEntity;

    public InsertDrug(DrugEntity drugEntity){
        this.drugEntity = drugEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        AppDatabase.getAppDatabase(contexts[0]).drugDao().insertDrug(drugEntity);
        return null;
    }
}
