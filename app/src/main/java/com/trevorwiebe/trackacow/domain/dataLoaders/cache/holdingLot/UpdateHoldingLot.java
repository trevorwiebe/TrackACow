package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingLot;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.db.AppDatabase;
import com.trevorwiebe.trackacow.data.db.entities.LotEntity;
import com.trevorwiebe.trackacow.data.db.holdingUpdateEntities.HoldingLotEntity;
import com.trevorwiebe.trackacow.domain.utils.Constants;

public class UpdateHoldingLot extends AsyncTask<Context, Void, Void> {

    private String lotId;
    private String penId;

    public UpdateHoldingLot(String lotId, String penId) {
        this.lotId = lotId;
        this.penId = penId;
    }

    @Override
    protected Void doInBackground(Context... contexts) {

        AppDatabase db = AppDatabase.getAppDatabase(contexts[0]);

        LotEntity lotEntity = db.lotDao().getLotEntityById(lotId);
        lotEntity.setPenId(penId);

        HoldingLotEntity holdingLotEntity = new HoldingLotEntity(lotEntity, Constants.INSERT_UPDATE);

        db.holdingLotDao().insertHoldingLot(holdingLotEntity);

        return null;
    }
}
