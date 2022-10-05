package com.trevorwiebe.trackacow.domain.dataLoaders.main.lot;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.LotEntity;

@Deprecated(since="Use use-cases instead")
public class UpdateLot extends AsyncTask<Context, Void, Void> {

    private LotEntity lotEntity;

    public UpdateLot(LotEntity lotEntity) {
        this.lotEntity = lotEntity;
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        String lotName = lotEntity.getLotName();
        String customerName = lotEntity.getCustomerName();
        String notes = lotEntity.getNotes();
        long date = lotEntity.getDate();
        String lotId = lotEntity.getLotId();
        AppDatabase.getAppDatabase(contexts[0]).lotDao().updateLotByFields(lotName, customerName, notes, date, lotId);
        return null;
    }
}
