package com.trevorwiebe.trackacow.db;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import android.content.Context;

import androidx.annotation.NonNull;

import com.trevorwiebe.trackacow.db.dao.ArchivedLotDao;
import com.trevorwiebe.trackacow.db.dao.CallDao;
import com.trevorwiebe.trackacow.db.dao.CowDao;
import com.trevorwiebe.trackacow.db.dao.DrugDao;
import com.trevorwiebe.trackacow.db.dao.DrugsGivenDao;
import com.trevorwiebe.trackacow.db.dao.FeedDao;
import com.trevorwiebe.trackacow.db.dao.LoadDao;
import com.trevorwiebe.trackacow.db.dao.LotDao;
import com.trevorwiebe.trackacow.db.dao.PenDao;
import com.trevorwiebe.trackacow.db.dao.UserDao;
import com.trevorwiebe.trackacow.db.entities.ArchivedLotEntity;
import com.trevorwiebe.trackacow.db.entities.CallEntity;
import com.trevorwiebe.trackacow.db.entities.CowEntity;
import com.trevorwiebe.trackacow.db.entities.DrugEntity;
import com.trevorwiebe.trackacow.db.entities.DrugsGivenEntity;
import com.trevorwiebe.trackacow.db.entities.FeedEntity;
import com.trevorwiebe.trackacow.db.entities.LoadEntity;
import com.trevorwiebe.trackacow.db.entities.LotEntity;
import com.trevorwiebe.trackacow.db.entities.PenEntity;
import com.trevorwiebe.trackacow.db.entities.UserEntity;
import com.trevorwiebe.trackacow.db.holdingDao.HoldingArchivedLotDao;
import com.trevorwiebe.trackacow.db.holdingDao.HoldingCowDao;
import com.trevorwiebe.trackacow.db.holdingDao.HoldingDrugDao;
import com.trevorwiebe.trackacow.db.holdingDao.HoldingDrugsGivenDao;
import com.trevorwiebe.trackacow.db.holdingDao.HoldingLoadDao;
import com.trevorwiebe.trackacow.db.holdingDao.HoldingLotDao;
import com.trevorwiebe.trackacow.db.holdingDao.HoldingPenDao;
import com.trevorwiebe.trackacow.db.holdingDao.HoldingUserDao;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingArchivedLotEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingCowEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingDrugsGivenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLoadEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingLotEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingPenEntity;
import com.trevorwiebe.trackacow.db.holdingUpdateEntities.HoldingUserEntity;
import com.trevorwiebe.trackacow.utils.Utility;

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
        LoadEntity.class,
        HoldingPenEntity.class,
        HoldingCowEntity.class,
        HoldingDrugsGivenEntity.class,
        HoldingDrugEntity.class,
        HoldingLotEntity.class,
        HoldingArchivedLotEntity.class,
        HoldingUserEntity.class,
        HoldingLoadEntity.class
},
        version = 2, exportSchema = false)
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
    public abstract LoadDao loadDao();

    public abstract HoldingPenDao holdingPenDao();
    public abstract HoldingCowDao holdingCowDao();
    public abstract HoldingDrugsGivenDao holdingDrugsGivenDao();
    public abstract HoldingDrugDao holdingDrugDao();
    public abstract HoldingLotDao holdingLotDao();
    public abstract HoldingUserDao holdingUserDao();
    public abstract HoldingArchivedLotDao holdingArchivedLotDao();
    public abstract HoldingLoadDao holdingLoadDao();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            // create the load tables
            database.execSQL("CREATE TABLE load (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, numberOfHead INTEGER NOT NULL, date INTEGER NOT NULL, description TEXT, lotId TEXT, loadId TEXT)");
            database.execSQL("CREATE TABLE holdingLoad (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, numberOfHead INTEGER NOT NULL, date INTEGER NOT NULL, description TEXT, lotId TEXT, loadId TEXT, whatHappened INTEGER NOT NULL)");
            // insert the relevant data from the lot table and archiveLot table into the load tables
            database.execSQL("INSERT INTO load (numberOfHead, date, lotId, loadId) SELECT totalHead, date, lotId, lotId FROM lot");
            database.execSQL("INSERT INTO holdingLoad (numberOfHead, date, lotId, loadId, whatHappened) SELECT totalHead, date, lotId, lotId, 1 FROM lot");
            database.execSQL("INSERT INTO load (numberOfHead, date, lotId, loadId) SELECT totalHead, dateStarted, lotId, lotId FROM archivedLot");
            database.execSQL("INSERT INTO holdingLoad (numberOfHead, date, lotId, loadId, whatHappened) SELECT totalHead, dateStarted, lotId, lotId, 1 FROM archivedLot");

            // create new lot tables
            database.execSQL("CREATE TABLE lot_new (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, lotName TEXT, lotId TEXT, customerName TEXT, notes TEXT, date INTEGER NOT NULL, penId TEXT)");
            database.execSQL("CREATE TABLE holdingLot_new (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, lotName TEXT, lotId TEXT, customerName TEXT, notes TEXT, date INTEGER NOT NULL, penId TEXT, whatHappened INTEGER NOT NULL)");
            // insert the needed data from the old lot tables into the new lot tables
            database.execSQL("INSERT INTO lot_new (lotName, lotId, customerName, notes, date, penId) SELECT lotName, lotId, customerName, notes, date, penId FROM lot");
            database.execSQL("INSERT INTO holdingLot_new (lotName, lotId, customerName, notes, date, penId, whatHappened) SELECT lotName, lotId, customerName, notes, date, penId, whatHappened FROM holdingLot");
            // delete the old lot tables
            database.execSQL("DROP TABLE lot");
            database.execSQL("DROP TABLE holdingLot");
            // rename the new lot tables to the old names
            database.execSQL("ALTER TABLE lot_new RENAME TO lot");
            database.execSQL("ALTER TABLE holdingLot_new RENAME TO holdingLot");

            // create new archive lot tables
            database.execSQL("CREATE TABLE archivedLot_new (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, lotName TEXT, lotId TEXT, customerName TEXT, notes TEXT, dateStarted INTEGER NOT NULL, dateEnded INTEGER NOT NULL)");
            database.execSQL("CREATE TABLE holdingArchivedLot_new (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, lotName TEXT, lotId TEXT, customerName TEXT, notes TEXT, dateStarted INTEGER NOT NULL, dateEnded INTEGER NOT NULL, whatHappened INTEGER NOT NULL)");
            // insert the needed data from the old archiveLot tables into the new archivedLot tables
            database.execSQL("INSERT INTO archivedLot_new (lotName, lotId, customerName, notes, dateStarted, dateEnded) SELECT lotName, lotId, customerName, notes, dateStarted, dateEnded FROM archivedLot");
            database.execSQL("INSERT INTO holdingArchivedLot_new (lotName, lotId, customerName, notes, dateStarted, dateEnded, whatHappened) SELECT lotName, lotId, customerName, notes, dateStarted, dateEnded, whatHappened FROM holdingArchivedLot");
            // delete the old lot tables
            database.execSQL("DROP TABLE archivedLot");
            database.execSQL("DROP TABLE holdingArchivedLot");
            // rename the new archiveLot tables to the old names
            database.execSQL("ALTER TABLE archivedLot_new RENAME TO archivedLot");
            database.execSQL("ALTER TABLE holdingArchivedLot_new RENAME TO holdingArchivedLot");

            // create the user tables
            database.execSQL("CREATE TABLE user (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, dateCreated INTEGER NOT NULL, accountType INTEGER NOT NULL, name TEXT, email TEXT, renewalDate INTEGER NOT NULL, uid TEXT)");
            database.execSQL("CREATE TABLE holdingUser (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, dateCreated INTEGER NOT NULL, accountType INTEGER NOT NULL, name TEXT, email TEXT, renewalDate INTEGER NOT NULL, uid TEXT, whatHappened INTEGER NOT NULL)");
        }
    };

    public static AppDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class,
                    "track_a_cow_db")
                    .addMigrations(MIGRATION_1_2)
                    .build();

            Utility.setNewDataToUpload(context, true);
        }
        return INSTANCE;
    }
}
