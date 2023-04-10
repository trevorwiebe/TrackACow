package com.trevorwiebe.trackacow.data.local

import android.content.Context
import androidx.room.Database
import com.trevorwiebe.trackacow.data.entities.PenEntity
import com.trevorwiebe.trackacow.data.entities.CowEntity
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity
import com.trevorwiebe.trackacow.data.entities.DrugEntity
import com.trevorwiebe.trackacow.data.entities.LotEntity
import com.trevorwiebe.trackacow.data.entities.CallEntity
import com.trevorwiebe.trackacow.data.entities.FeedEntity
import com.trevorwiebe.trackacow.data.entities.UserEntity
import com.trevorwiebe.trackacow.data.entities.LoadEntity
import com.trevorwiebe.trackacow.data.entities.RationEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CachePenEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheCowEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugsGivenEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLotEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheUserEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLoadEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheCallEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheFeedEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheRationEntity
import androidx.room.RoomDatabase
import com.trevorwiebe.trackacow.data.local.dao.PenDao
import com.trevorwiebe.trackacow.data.local.dao.CowDao
import com.trevorwiebe.trackacow.data.local.dao.DrugsGivenDao
import com.trevorwiebe.trackacow.data.local.dao.DrugDao
import com.trevorwiebe.trackacow.data.local.dao.LotDao
import com.trevorwiebe.trackacow.data.local.dao.CallDao
import com.trevorwiebe.trackacow.data.local.dao.FeedDao
import com.trevorwiebe.trackacow.data.local.dao.UserDao
import com.trevorwiebe.trackacow.data.local.dao.LoadDao
import com.trevorwiebe.trackacow.data.local.dao.RationDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CachePenDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheCowDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheDrugsGivenDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheDrugDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheLotDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheUserDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheLoadDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheCallDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheFeedDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheRationDao
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Room
import androidx.room.migration.Migration

@Database(
    entities = [
        PenEntity::class,
        CowEntity::class,
        DrugsGivenEntity::class,
        DrugEntity::class,
        LotEntity::class,
        CallEntity::class,
        FeedEntity::class,
        UserEntity::class,
        LoadEntity::class,
        RationEntity::class,
        CachePenEntity::class,
        CacheCowEntity::class,
        CacheDrugsGivenEntity::class,
        CacheDrugEntity::class,
        CacheLotEntity::class,
        CacheUserEntity::class,
        CacheLoadEntity::class,
        CacheCallEntity::class,
        CacheFeedEntity::class,
        CacheRationEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun penDao(): PenDao
    abstract fun cowDao(): CowDao
    abstract fun drugsGivenDao(): DrugsGivenDao
    abstract fun drugDao(): DrugDao
    abstract fun lotDao(): LotDao
    abstract fun callDao(): CallDao
    abstract fun feedDao(): FeedDao
    abstract fun userDao(): UserDao
    abstract fun loadDao(): LoadDao
    abstract fun rationDao(): RationDao
    abstract fun cachePenDao(): CachePenDao
    abstract fun cacheCowDao(): CacheCowDao
    abstract fun cacheDrugsGivenDao(): CacheDrugsGivenDao
    abstract fun cacheDrugDao(): CacheDrugDao
    abstract fun cacheLotDao(): CacheLotDao
    abstract fun cacheUserDao(): CacheUserDao
    abstract fun cacheLoadDao(): CacheLoadDao
    abstract fun cacheCallDao(): CacheCallDao
    abstract fun cacheFeedDao(): CacheFeedDao
    abstract fun cacheRationDao(): CacheRationDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

                // create the load tables
                database.execSQL("CREATE TABLE load (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, numberOfHead INTEGER NOT NULL, date INTEGER NOT NULL, description TEXT, lotId TEXT, loadId TEXT)")
                database.execSQL("CREATE TABLE holdingLoad (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, numberOfHead INTEGER NOT NULL, date INTEGER NOT NULL, description TEXT, lotId TEXT, loadId TEXT, whatHappened INTEGER NOT NULL)")
                // insert the relevant data from the lot table and archiveLot table into the load tables
                database.execSQL("INSERT INTO load (numberOfHead, date, lotId, loadId) SELECT totalHead, date, lotId, lotId FROM lot")
                database.execSQL("INSERT INTO holdingLoad (numberOfHead, date, lotId, loadId, whatHappened) SELECT totalHead, date, lotId, lotId, 1 FROM lot")
                database.execSQL("INSERT INTO load (numberOfHead, date, lotId, loadId) SELECT totalHead, dateStarted, lotId, lotId FROM archivedLot")
                database.execSQL("INSERT INTO holdingLoad (numberOfHead, date, lotId, loadId, whatHappened) SELECT totalHead, dateStarted, lotId, lotId, 1 FROM archivedLot")

                // create new lot tables
                database.execSQL("CREATE TABLE lot_new (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, lotName TEXT, lotId TEXT, customerName TEXT, notes TEXT, date INTEGER NOT NULL, penId TEXT)")
                database.execSQL("CREATE TABLE holdingLot_new (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, lotName TEXT, lotId TEXT, customerName TEXT, notes TEXT, date INTEGER NOT NULL, penId TEXT, whatHappened INTEGER NOT NULL)")
                // insert the needed data from the old lot tables into the new lot tables
                database.execSQL("INSERT INTO lot_new (lotName, lotId, customerName, notes, date, penId) SELECT lotName, lotId, customerName, notes, date, penId FROM lot")
                database.execSQL("INSERT INTO holdingLot_new (lotName, lotId, customerName, notes, date, penId, whatHappened) SELECT lotName, lotId, customerName, notes, date, penId, whatHappened FROM holdingLot")
                // delete the old lot tables
                database.execSQL("DROP TABLE lot")
                database.execSQL("DROP TABLE holdingLot")
                // rename the new lot tables to the old names
                database.execSQL("ALTER TABLE lot_new RENAME TO lot")
                database.execSQL("ALTER TABLE holdingLot_new RENAME TO holdingLot")

                // create new archive lot tables
                database.execSQL("CREATE TABLE archivedLot_new (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, lotName TEXT, lotId TEXT, customerName TEXT, notes TEXT, dateStarted INTEGER NOT NULL, dateEnded INTEGER NOT NULL)")
                database.execSQL("CREATE TABLE holdingArchivedLot_new (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, lotName TEXT, lotId TEXT, customerName TEXT, notes TEXT, dateStarted INTEGER NOT NULL, dateEnded INTEGER NOT NULL, whatHappened INTEGER NOT NULL)")
                // insert the needed data from the old archiveLot tables into the new archivedLot tables
                database.execSQL("INSERT INTO archivedLot_new (lotName, lotId, customerName, notes, dateStarted, dateEnded) SELECT lotName, lotId, customerName, notes, dateStarted, dateEnded FROM archivedLot")
                database.execSQL("INSERT INTO holdingArchivedLot_new (lotName, lotId, customerName, notes, dateStarted, dateEnded, whatHappened) SELECT lotName, lotId, customerName, notes, dateStarted, dateEnded, whatHappened FROM holdingArchivedLot")
                // delete the old lot tables
                database.execSQL("DROP TABLE archivedLot")
                database.execSQL("DROP TABLE holdingArchivedLot")
                // rename the new archiveLot tables to the old names
                database.execSQL("ALTER TABLE archivedLot_new RENAME TO archivedLot")
                database.execSQL("ALTER TABLE holdingArchivedLot_new RENAME TO holdingArchivedLot")

                // create the user tables
                database.execSQL("CREATE TABLE user (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, dateCreated INTEGER NOT NULL, accountType INTEGER NOT NULL, name TEXT, email TEXT, renewalDate INTEGER NOT NULL, uid TEXT)")
                database.execSQL("CREATE TABLE holdingUser (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, dateCreated INTEGER NOT NULL, accountType INTEGER NOT NULL, name TEXT, email TEXT, renewalDate INTEGER NOT NULL, uid TEXT, whatHappened INTEGER NOT NULL)")
            }
        }
        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {

                // create new drugGiven tables
                database.execSQL("CREATE TABLE DrugsGiven_new (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, drugGivenId TEXT, drugId TEXT, amountGiven INTEGER NOT NULL, cowId TEXT, lotId TEXT, date INTEGER NOT NULL)")
                database.execSQL("CREATE TABLE HoldingDrugsGiven_new (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, drugGivenId TEXT, drugId TEXT, amountGiven INTEGER NOT NULL, cowId TEXT, lotId TEXT, date INTEGER NOT NULL, whatHappened INTEGER NOT NULL)")
                // insert the old data into the new table
                database.execSQL("INSERT INTO DrugsGiven_new (drugGivenId, drugId, amountGiven, cowId, lotId, date) SELECT drugGiveId, drugId, amountGiven, cowId, lotId, 0 FROM DrugsGiven")
                database.execSQL("INSERT INTO HoldingDrugsGiven_new (drugGivenId, drugId, amountGiven, cowId, lotId, date, whatHappened) SELECT drugGiveId, drugId, amountGiven, cowId, lotId, 0, 1 FROM DrugsGiven")
                // delete the old drugGiven tables
                database.execSQL("DROP TABLE DrugsGiven")
                database.execSQL("DROP TABLE HoldingDrugsGiven")
                // rename the new drugGiven tables to the old names
                database.execSQL("ALTER TABLE DrugsGiven_new RENAME TO DrugsGiven")
                database.execSQL("ALTER TABLE HoldingDrugsGiven_new RENAME TO HoldingDrugsGiven")
            }
        }
        private val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {}
        }
        private val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE call")
                database.execSQL("CREATE TABLE call (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, callAmount INTEGER NOT NULL, date INTEGER NOT NULL, id TEXT, lotId TEXT)")
                database.execSQL("CREATE TABLE holdingCall (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, callAmount INTEGER NOT NULL, date INTEGER NOT NULL, id TEXT, lotId TEXT, whatHappened INTEGER NOT NULL)")
                database.execSQL("CREATE TABLE holdingFeed (primaryKey Integer NOT NULL PRIMARY KEY AUTOINCREMENT, feed INTEGER NOT NULL, date INTEGER NOT NULL, id TEXT, lotId TEXT, whatHappened INTEGER NOT NULL)")
            }
        }
        private val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {

                // create ration and holdingRation tables
                database.execSQL("CREATE TABLE ration (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, rationId TEXT NOT NULL, rationName TEXT NOT NULL)")
                database.execSQL("CREATE TABLE holdingRation(primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, rationId TEXT NOT NULL, rationName TEXT NOT NULL, whatHappened INTEGER NOT NULL)")

                // update call table
                database.execSQL("CREATE TABLE call_new (callPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, callAmount INTEGER, date INTEGER, lotId TEXT, callRationId INTEGER, callCloudDatabaseId TEXT)")
                database.execSQL("INSERT INTO call_new (callPrimaryKey, callAmount, date, lotId, callRationId, callCloudDatabaseId) SELECT primaryKey, callAmount, date, lotId, -1, id FROM call")
                database.execSQL("DROP TABLE call")
                database.execSQL("ALTER TABLE call_new RENAME TO call")

                // update drug table
                database.execSQL("CREATE TABLE drug_new (drugPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, defaultAmount INTEGER, drugCloudDatabaseId TEXT, drugName TEXT)")
                database.execSQL("INSERT INTO drug_new (drugPrimaryKey, defaultAmount, drugCloudDatabaseId, drugName) SELECT primaryKey, defaultAmount, drugId, drugName FROM Drug")
                database.execSQL("DROP TABLE Drug")
                database.execSQL("ALTER TABLE drug_new RENAME TO drug")
            }
        }

        @JvmStatic
        fun getAppDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "track_a_cow_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    .addMigrations(MIGRATION_4_5)
                    .addMigrations(MIGRATION_5_6)
                    .build()
            }
            return INSTANCE
        }
    }
}