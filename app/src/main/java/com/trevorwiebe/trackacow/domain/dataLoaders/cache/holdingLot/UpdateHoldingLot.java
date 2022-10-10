package com.trevorwiebe.trackacow.domain.dataLoaders.cache.holdingLot;

import android.content.Context;
import android.os.AsyncTask;

import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.LotEntity;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLotEntity;
import com.trevorwiebe.trackacow.domain.utils.Constants;

@Deprecated(since="Use use-cases instead")
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
        lotEntity.setLotPenCloudDatabaseId(penId);

        CacheLotEntity cacheLotEntity = new CacheLotEntity(
                lotEntity.getLotPrimaryKey(),
                lotEntity.getLotName(),
                lotEntity.getLotCloudDatabaseId(),
                lotEntity.getCustomerName(),
                lotEntity.getNotes(),
                lotEntity.getDate(),
                lotEntity.getLotPenCloudDatabaseId(),
                Constants.INSERT_UPDATE);

        db.cacheLotDao().insertHoldingLot(cacheLotEntity);

        return null;
    }
}
