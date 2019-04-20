package com.trevorwiebe.trackacow.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.firebase.ui.auth.data.model.User;
import com.trevorwiebe.trackacow.db.dao.ArchivedLotDao;
import com.trevorwiebe.trackacow.db.dao.CallDao;
import com.trevorwiebe.trackacow.db.dao.CowDao;
import com.trevorwiebe.trackacow.db.dao.DrugDao;
import com.trevorwiebe.trackacow.db.dao.DrugsGivenDao;
import com.trevorwiebe.trackacow.db.dao.FeedDao;
import com.trevorwiebe.trackacow.db.dao.LotDao;
import com.trevorwiebe.trackacow.db.dao.PenDao;
import com.trevorwiebe.trackacow.db.dao.UserDao;
import com.trevorwiebe.trackacow.db.entities.ArchivedLotEntity;
import com.trevorwiebe.trackacow.db.entities.CallEntity;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.FeedEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.db.entities.UserEntity;
import com.trevorwiebe.trackacow.db.holdingDao.HoldingArchivedLotDao;
import com.trevorwiebe.trackacow.db.holdingDao.HoldingCowDao;
import com.trevorwiebe.trackacow.db.holdingDao.HoldingDrugDao;
import com.trevorwiebe.trackacow.db.holdingDao.HoldingDrugsGivenDao;
import com.trevorwiebe.trackacow.db.holdingDao.HoldingLotDao;
import com.trevorwiebe.trackacow.db.holdingDao.HoldingPenDao;
import com.trevorwiebe.trackacow.db.holdingDao.HoldingUserDao;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingArchivedLotEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingCowEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugsGivenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLotEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingPenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingUserEntity;

@Database(entities = {
        PenEntity.class,
        CowEntity.class,
        DrugsGivenEntity.class,
        DrugEntity.class,
        LotEntity.class,
        ArchivedLotEntity.class,
        CallEntity.class,
        FeedEntity.class,
        UserEntity.class,
        HoldingPenEntity.class,
        HoldingCowEntity.class,
        HoldingDrugsGivenEntity.class,
        HoldingDrugEntity.class,
        HoldingLotEntity.class,
        HoldingArchivedLotEntity.class,
        HoldingUserEntity.class
                    }, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract PenDao penDao();
    public abstract CowDao cowDao();
    public abstract DrugsGivenDao drugsGivenDao();
    public abstract DrugDao drugDao();
    public abstract LotDao lotDao();
    public abstract ArchivedLotDao archivedLotDao();
    public abstract CallDao callDao();
    public abstract FeedDao feedDao();
    public abstract UserDao userDao();

    public abstract HoldingPenDao holdingPenDao();
    public abstract HoldingCowDao holdingCowDao();
    public abstract HoldingDrugsGivenDao holdingDrugsGivenDao();
    public abstract HoldingDrugDao holdingDrugDao();
    public abstract HoldingLotDao holdingLotDao();

    public abstract HoldingUserDao holdingUserDao();

    public abstract HoldingArchivedLotDao holdingArchivedLotDao();

    public static AppDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class,
                    "track_a_cow_db")
                    .build();
        }
        return INSTANCE;
    }
}
