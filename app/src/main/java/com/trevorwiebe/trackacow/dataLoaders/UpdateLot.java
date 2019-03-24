package com.trevorwiebe.trackacow.dataLoaders;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.db.AppDatabase;
import com.trevorwiebe.trackacow.db.entities.LotEntity;

public class UpdateLot extends AsyncTask<Context, Void, Void> {

    private LotEntity lotEntity;

    public UpdateLot(LotEntity lotEntity) {
        this.lotEntity = lotEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        String lotName = lotEntity.getLotName();
        String customerName = lotEntity.getCustomerName();
        int totalHead = lotEntity.getTotalHead();
        String notes = lotEntity.getNotes();
        String lotId = lotEntity.getLotId();
        AppDatabase.getAppDatabase(contexts[0]).lotDao().updateLotByFields(lotName, customerName, totalHead, notes, lotId);
        return null;
    }
}
