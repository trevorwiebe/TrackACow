package com.trevorwiebe.trackacow.dataLoaders;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.trackacow.db.entities.ArchivedLotEntity;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.LoadEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.db.entities.UserEntity;
import com.trevorwiebe.trackacow.utils.Constants;

import java.util.ArrayList;

public class QueryAllCloudData {

    private static final String TAG = "QueryAllCloudData";

    private DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private ArrayList<CowEntity> mCowEntityUpdateList = new ArrayList<>();
    private ArrayList<DrugEntity> mDrugEntityUpdateList = new ArrayList<>();
    private ArrayList<DrugsGivenEntity> mDrugsGivenEntityUpdateList = new ArrayList<>();
    private ArrayList<PenEntity> mPenEntityUpdateList = new ArrayList<>();
    private ArrayList<LotEntity> mLotEntityList = new ArrayList<>();
    private ArrayList<ArchivedLotEntity> mArchivedLotList = new ArrayList<>();
    private ArrayList<UserEntity> mUserEntity = new ArrayList<>();
    private ArrayList<LoadEntity> mLoadList = new ArrayList<>();

    public OnAllCloudDataLoaded onAllCloudDataLoaded;

    public QueryAllCloudData(OnAllCloudDataLoaded onAllCloudDataLoaded) {
        this.onAllCloudDataLoaded = onAllCloudDataLoaded;
    }

    public interface OnAllCloudDataLoaded {
        void onAllCloudDataLoaded(int resultCode, ArrayList<CowEntity> cowEntities, ArrayList<DrugEntity> drugEntities, ArrayList<DrugsGivenEntity> drugsGivenEntities, ArrayList<PenEntity> penEntities, ArrayList<LotEntity> lotEntities, ArrayList<ArchivedLotEntity> archivedLotEntities, ArrayList<LoadEntity> loadEntities, ArrayList<UserEntity> userEntities);
    }

    public void loadCloudData() {
        baseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (key != null) {
                        switch (key) {
                            case CowEntity.COW:
                                for (DataSnapshot cowSnapshot : snapshot.getChildren()) {
                                    CowEntity cowEntity = cowSnapshot.getValue(CowEntity.class);
                                    if (cowEntity != null) {
                                        mCowEntityUpdateList.add(cowEntity);
                                    }
                                }
                                break;
                            case DrugEntity.DRUG_OBJECT:
                                for (DataSnapshot drugsSnapshot : snapshot.getChildren()) {
                                    DrugEntity drugEntity = drugsSnapshot.getValue(DrugEntity.class);
                                    if (drugEntity != null) {
                                        mDrugEntityUpdateList.add(drugEntity);
                                    }
                                }
                                break;
                            case PenEntity.PEN_OBJECT:
                                for (DataSnapshot penSnapshot : snapshot.getChildren()) {
                                    PenEntity penEntity = penSnapshot.getValue(PenEntity.class);
                                    if (penEntity != null) {
                                        mPenEntityUpdateList.add(penEntity);
                                    }
                                }
                                break;
                            case DrugsGivenEntity.DRUGS_GIVEN:
                                for (DataSnapshot drugsGivenSnapShot : snapshot.getChildren()) {
                                    DrugsGivenEntity drugsGivenEntity = drugsGivenSnapShot.getValue(DrugsGivenEntity.class);
                                    if (drugsGivenEntity != null) {
                                        mDrugsGivenEntityUpdateList.add(drugsGivenEntity);
                                    }
                                }
                                break;
                            case LotEntity.LOT:
                                for (DataSnapshot lotSnapshot : snapshot.getChildren()) {
                                    LotEntity lotEntity = lotSnapshot.getValue(LotEntity.class);
                                    if (lotEntity != null) {
                                        mLotEntityList.add(lotEntity);
                                    }
                                }
                                break;
                            case ArchivedLotEntity.ARCHIVED_LOT:
                                for (DataSnapshot archiveLotSnapShot : snapshot.getChildren()) {
                                    ArchivedLotEntity archivedLotEntity = archiveLotSnapShot.getValue(ArchivedLotEntity.class);
                                    if (archivedLotEntity != null) {
                                        mArchivedLotList.add(archivedLotEntity);
                                    }
                                }
                                break;
                            case LoadEntity.LOAD:
                                for (DataSnapshot loadSnapShot : snapshot.getChildren()) {
                                    LoadEntity loadEntity = loadSnapShot.getValue(LoadEntity.class);
                                    if (loadEntity != null) {
                                        mLoadList.add(loadEntity);
                                    }
                                }
                            case UserEntity.USER:
                                UserEntity userEntity = snapshot.getValue(UserEntity.class);
                                if (userEntity != null) {
                                    mUserEntity.add(userEntity);
                                }
                                break;
                            default:
                                Log.e(TAG, "onDataChange: unknown snapshot key " + key);
                                onAllCloudDataLoaded.onAllCloudDataLoaded(Constants.ERROR_FETCHING_DATA_FROM_CLOUD, mCowEntityUpdateList, mDrugEntityUpdateList, mDrugsGivenEntityUpdateList, mPenEntityUpdateList, mLotEntityList, mArchivedLotList, mLoadList, mUserEntity);
                                break;
                        }
                    }
                }

                onAllCloudDataLoaded.onAllCloudDataLoaded(Constants.SUCCESS, mCowEntityUpdateList, mDrugEntityUpdateList, mDrugsGivenEntityUpdateList, mPenEntityUpdateList, mLotEntityList, mArchivedLotList, mLoadList, mUserEntity);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onAllCloudDataLoaded.onAllCloudDataLoaded(Constants.ERROR_FETCHING_DATA_FROM_CLOUD, mCowEntityUpdateList, mDrugEntityUpdateList, mDrugsGivenEntityUpdateList, mPenEntityUpdateList, mLotEntityList, mArchivedLotList, mLoadList, mUserEntity);
            }
        });

    }
}
