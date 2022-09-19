package com.trevorwiebe.trackacow.dataLoaders.main.drug;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;

public class UpdateDrug extends AsyncTask<Context, Void, Void> {

    private DrugEntity drugEntity;

    public UpdateDrug(DrugEntity drugEntity){
        this.drugEntity = drugEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        int defaultAmount = drugEntity.getDefaultAmount();
        String drugName = drugEntity.getDrugName();
        String drugId = drugEntity.getDrugId();
        AppDatabase.getAppDatabase(contexts[0]).drugDao().updateDrugById(defaultAmount, drugName, drugId);
        return null;
    }

}
