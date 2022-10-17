package com.trevorwiebe.trackacow.domain.dataLoaders.misc;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheCowEntity;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugsGivenEntity;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLotEntity;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheUserEntity;
import com.trevorwiebe.trackacow.data.local.AppDatabase;
import com.trevorwiebe.trackacow.data.entities.ArchivedLotEntity;
import com.trevorwiebe.trackacow.data.entities.CowEntity;
import com.trevorwiebe.trackacow.data.entities.DrugEntity;
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.data.entities.FeedEntity;
import com.trevorwiebe.trackacow.data.entities.LoadEntity;
import com.trevorwiebe.trackacow.data.entities.LotEntity;
import com.trevorwiebe.trackacow.data.entities.PenEntity;
import com.trevorwiebe.trackacow.data.entities.UserEntity;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheArchivedLotEntity;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugEntity;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheFeedEntity;
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLoadEntity;
import com.trevorwiebe.trackacow.data.cacheEntities.CachePenEntity;
import com.trevorwiebe.trackacow.domain.utils.Constants;
import com.trevorwiebe.trackacow.domain.utils.Utility;

import java.util.List;

@Deprecated(since="Use use-cases instead")
public class InsertAllLocalChangeToCloud extends AsyncTask<Context, Void, Integer> {

    private static final String TAG = "InsertAllLocalChangeToC";

    private DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    private OnAllLocalDbInsertedToCloud mOnAllLocalDbInsertedToCloud;

    public InsertAllLocalChangeToCloud(OnAllLocalDbInsertedToCloud onAllLocalDbInsertedToCloud) {
        this.mOnAllLocalDbInsertedToCloud = onAllLocalDbInsertedToCloud;
    }

    public interface OnAllLocalDbInsertedToCloud{
        void onAllLocalDbInsertedToCloud(int resultCode);
    }

    @Override
    protected Integer doInBackground(Context... contexts) {

        if (!Utility.haveNetworkConnection(contexts[0])) {
            return Constants.NO_NETWORK_CONNECTION;
        }

        AppDatabase db = AppDatabase.getAppDatabase(contexts[0]);

        try {
            // update drug entities
            List<CacheDrugEntity> holdingDrugEntities = db.cacheDrugDao().getHoldingDrugList();
            for (int a = 0; a < holdingDrugEntities.size(); a++) {
                CacheDrugEntity cacheDrugEntity = holdingDrugEntities.get(a);

                DrugEntity drugEntity = new DrugEntity(cacheDrugEntity.getPrimaryKey(), cacheDrugEntity.getDefaultAmount(), cacheDrugEntity.getDrugCloudDatabaseId(), cacheDrugEntity.getDrugName());

                switch (cacheDrugEntity.getWhatHappened()) {
                    case Constants.INSERT_UPDATE:
                        baseRef.child(Constants.DRUG).child(drugEntity.getDrugCloudDatabaseId()).setValue(drugEntity);
                        break;
                    case Constants.DELETE:
                        baseRef.child(Constants.DRUG).child(drugEntity.getDrugCloudDatabaseId()).removeValue();
                        break;
                    default:
                        break;
                }
            }
            db.cacheDrugDao().deleteHoldingDrugTable();

            //update pen entities
            List<CachePenEntity> holdingPenEntities = db.cachePenDao().getHoldingPenList();
            for (int b = 0; b < holdingPenEntities.size(); b++) {
                CachePenEntity cachePenEntity = holdingPenEntities.get(b);

                PenEntity penEntity = new PenEntity(0,cachePenEntity.getPenCloudDatabaseId(), cachePenEntity.getPenName());

                switch (cachePenEntity.getWhatHappened()) {
                    case Constants.INSERT_UPDATE:
                        baseRef.child("pens").child(penEntity.getPenCloudDatabaseId()).setValue(penEntity);
                        break;
                    case Constants.DELETE:
                        baseRef.child("pens").child(penEntity.getPenCloudDatabaseId()).removeValue();
                        break;
                    default:
                        break;
                }
            }
            db.cachePenDao().deleteHoldingPenTable();

            //update cow entities
            List<CacheCowEntity> holdingCowEntities = db.cacheCowDao().getHoldingCowEntityList();
            for (int c = 0; c < holdingCowEntities.size(); c++) {

                CacheCowEntity cacheCowEntity = holdingCowEntities.get(c);

                CowEntity cowEntity = new CowEntity(cacheCowEntity.isAlive(), cacheCowEntity.getCowId(), cacheCowEntity.getTagNumber(), cacheCowEntity.getDate(), cacheCowEntity.getNotes(), cacheCowEntity.getLotId());

                switch (cacheCowEntity.getWhatHappened()) {
                    case Constants.INSERT_UPDATE:
                        baseRef.child(CowEntity.COW).child(cowEntity.getCowId()).setValue(cowEntity);
                        break;
                    case Constants.DELETE:
                        baseRef.child(CowEntity.COW).child(cowEntity.getCowId()).removeValue();
                        break;
                    default:
                        break;
                }
            }
            db.cacheCowDao().deleteHoldingCowTable();

            // update drugs given entities
            List<CacheDrugsGivenEntity> holdingDrugsGivenEntities = db.cacheDrugsGivenDao().getHoldingDrugsGivenList();
            for (int d = 0; d < holdingDrugsGivenEntities.size(); d++) {

                CacheDrugsGivenEntity cacheDrugsGivenEntity = holdingDrugsGivenEntities.get(d);

                DrugsGivenEntity drugsGivenEntity = new DrugsGivenEntity(cacheDrugsGivenEntity.getDrugGivenId(), cacheDrugsGivenEntity.getDrugId(), cacheDrugsGivenEntity.getAmountGiven(), cacheDrugsGivenEntity.getCowId(), cacheDrugsGivenEntity.getLotId(), cacheDrugsGivenEntity.getDate());

                switch (cacheDrugsGivenEntity.getWhatHappened()) {
                    case Constants.INSERT_UPDATE:
                        baseRef.child(DrugsGivenEntity.DRUGS_GIVEN).child(drugsGivenEntity.getDrugGivenId()).setValue(drugsGivenEntity);
                        break;
                    case Constants.DELETE:
                        baseRef.child(DrugsGivenEntity.DRUGS_GIVEN).child(drugsGivenEntity.getDrugGivenId()).removeValue();
                        break;
                }
            }
            db.cacheDrugsGivenDao().deleteHoldingDrugsGivenTable();

            // update lot entities
            List<CacheLotEntity> holdingLotEntities = db.cacheLotDao().getHoldingLotList();
            for (int e = 0; e < holdingLotEntities.size(); e++) {

                CacheLotEntity cacheLotEntity = holdingLotEntities.get(e);

                LotEntity lotEntity = new LotEntity(
                        cacheLotEntity.getLotPrimaryKey(),
                        cacheLotEntity.getLotName(),
                        cacheLotEntity.getLotCloudDatabaseId(),
                        cacheLotEntity.getCustomerName(),
                        cacheLotEntity.getNotes(),
                        cacheLotEntity.getDate(),
                        cacheLotEntity.getLotPenCloudDatabaseId()
                );
                switch (cacheLotEntity.getWhatHappened()) {
                    case Constants.INSERT_UPDATE:
                        baseRef.child(Constants.LOTS).child(lotEntity.getLotCloudDatabaseId()).setValue(lotEntity);
                        break;
                    case Constants.DELETE:
                        baseRef.child(Constants.LOTS).child(lotEntity.getLotCloudDatabaseId()).removeValue();
                        break;
                }
            }
            db.cacheLotDao().deleteHoldingLotTable();

            // update archive lot entities
            List<CacheArchivedLotEntity> holdingArchivedLotEntities = db.cacheArchivedLotDao().getHoldingArchivedLotList();
            for (int f = 0; f < holdingArchivedLotEntities.size(); f++) {

                CacheArchivedLotEntity cacheArchivedLotEntity = holdingArchivedLotEntities.get(f);

                ArchivedLotEntity archivedLotEntity = new ArchivedLotEntity(cacheArchivedLotEntity);
                switch (cacheArchivedLotEntity.getWhatHappened()) {
                    case Constants.INSERT_UPDATE:
                        baseRef.child(ArchivedLotEntity.ARCHIVED_LOT).child(archivedLotEntity.getLotId()).setValue(archivedLotEntity);
                        break;
                    case Constants.DELETE:
                        baseRef.child(ArchivedLotEntity.ARCHIVED_LOT).child(archivedLotEntity.getLotId()).removeValue();
                        break;
                }
            }
            db.cacheArchivedLotDao().deleteHoldingArchivedLotTable();

            // update load entities
            List<CacheLoadEntity> holdingLoadEntities = db.cacheLoadDao().getHoldingLoadList();
            for (int h = 0; h < holdingLoadEntities.size(); h++) {

                CacheLoadEntity cacheLoadEntity = holdingLoadEntities.get(h);

                LoadEntity loadEntity = new LoadEntity(cacheLoadEntity);
                switch (cacheLoadEntity.getWhatHappened()) {
                    case Constants.INSERT_UPDATE:
                        baseRef.child(LoadEntity.LOAD).child(loadEntity.getLoadId()).setValue(loadEntity);
                        break;
                    case Constants.DELETE:
                        baseRef.child(LoadEntity.LOAD).child(loadEntity.getLoadId()).removeValue();
                        break;
                }
            }
            db.cacheLoadDao().deleteHoldingLoadTable();

//             update callEntity node
//            List<HoldingCallEntity> holdingCallEntities = db.holdingCallDao().getHoldingCallEntities();
//            for (int j=0; j<holdingCallEntities.size(); j++){
//                HoldingCallEntity holdingCallEntity = holdingCallEntities.get(j);
//                CallEntity callEntity = new CallEntity(holdingCallEntity);
//
//                switch (holdingCallEntity.getWhatHappened()){
//                    case Constants.INSERT_UPDATE:
//                        baseRef.child(CallEntity.CALL).child(callEntity.getId()).setValue(callEntity);
//                        break;
//                    case Constants.DELETE:
//                        baseRef.child(CallEntity.CALL).child(callEntity.getId()).removeValue();
//                        break;
//                }
//            }
//            db.holdingCallDao().deleteCallTable();

            // update feedEntity node
            List<CacheFeedEntity> holdingFeedEntities = db.cacheFeedDao().getHoldingFeedEntities();
            for(int f=0; f<holdingFeedEntities.size(); f++){
                CacheFeedEntity cacheFeedEntity = holdingFeedEntities.get(f);
                FeedEntity feedEntity = new FeedEntity(
                        cacheFeedEntity.getPrimaryKey(),
                        cacheFeedEntity.getFeed(),
                        cacheFeedEntity.getDate(),
                        cacheFeedEntity.getId(),
                        cacheFeedEntity.getLotId()
                );

                switch (cacheFeedEntity.getWhatHappened()){
                    case Constants.INSERT_UPDATE:
                        baseRef.child(Constants.FEEDS).child(feedEntity.getId()).setValue(feedEntity);
                        break;
                    case Constants.DELETE:
                        baseRef.child(Constants.FEEDS).child(feedEntity.getId()).removeValue();
                        break;
                }
            }
            db.cacheFeedDao().deleteHoldingFeedTable();

            // update user node
            List<CacheUserEntity> holdingUserEntities = db.cacheUserDao().getHoldingUserList();
            for (int g = 0; g < holdingUserEntities.size(); g++) {

                CacheUserEntity cacheUserEntity = holdingUserEntities.get(g);

                UserEntity userEntity = new UserEntity(cacheUserEntity);
                switch (cacheUserEntity.getWhatHappened()) {
                    case Constants.INSERT_UPDATE:
                        baseRef.child(UserEntity.USER).setValue(userEntity);
                        break;
                    case Constants.DELETE:
                        baseRef.child(UserEntity.USER).removeValue();
                        break;
                }
            }
            db.cacheUserDao().deleteHoldingUserTable();

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return Constants.ERROR_PUSHING_DATA_TO_CLOUD;
        }

        return Constants.SUCCESS;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        super.onPostExecute(resultCode);
        mOnAllLocalDbInsertedToCloud.onAllLocalDbInsertedToCloud(resultCode);
    }
}
