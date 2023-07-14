package com.trevorwiebe.trackacow.data.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.functions.FirebaseFunctions
import com.trevorwiebe.trackacow.data.local.AppDatabase
import com.trevorwiebe.trackacow.data.local.repository.*
import com.trevorwiebe.trackacow.data.preferences.AppPreferencesImpl
import com.trevorwiebe.trackacow.data.remote.repository.*
import com.trevorwiebe.trackacow.domain.preferences.AppPreferences
import com.trevorwiebe.trackacow.domain.repository.local.*
import com.trevorwiebe.trackacow.domain.repository.remote.*
import com.trevorwiebe.trackacow.domain.use_cases.CalculateDayStartAndDayEnd
import com.trevorwiebe.trackacow.domain.use_cases.CalculateDrugsGiven
import com.trevorwiebe.trackacow.domain.use_cases.DeleteAllLocalData
import com.trevorwiebe.trackacow.domain.use_cases.InitiateCloudDatabaseMigration5to6
import com.trevorwiebe.trackacow.domain.use_cases.UploadCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrackACowDataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        val migration1to2: Migration = object : Migration(1, 2) {
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
        val migration2to3: Migration = object : Migration(2, 3) {
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
        val migration3to4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {}
        }
        val migration4to5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE call")
                database.execSQL("CREATE TABLE call (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, callAmount INTEGER NOT NULL, date INTEGER NOT NULL, id TEXT, lotId TEXT)")
                database.execSQL("CREATE TABLE holdingCall (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, callAmount INTEGER NOT NULL, date INTEGER NOT NULL, id TEXT, lotId TEXT, whatHappened INTEGER NOT NULL)")
                database.execSQL("CREATE TABLE holdingFeed (primaryKey Integer NOT NULL PRIMARY KEY AUTOINCREMENT, feed INTEGER NOT NULL, date INTEGER NOT NULL, id TEXT, lotId TEXT, whatHappened INTEGER NOT NULL)")
            }
        }
        val migration5to6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {

                // update call table
                database.execSQL("DROP TABLE call")
                database.execSQL("CREATE TABLE call (callPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, callAmount INTEGER NOT NULL, date INTEGER NOT NULL, lotId TEXT NOT NULL, callRationId INTEGER, callCloudDatabaseId TEXT)")

                // update cow table
                database.execSQL("DROP TABLE Cow")
                database.execSQL("CREATE TABLE cow (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, alive INTEGER NOT NULL, cowId TEXT NOT NULL, tagNumber INTEGER NOT NULL, date INTEGER NOT NULL, notes TEXT, lotId TEXT NOT NULL)")

                // update drug table
                database.execSQL("DROP TABLE Drug")
                database.execSQL("CREATE TABLE drug (drugPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, defaultAmount INTEGER NOT NULL, drugCloudDatabaseId TEXT NOT NULL, drugName TEXT NOT NULL)")

                // update drugsGiven table
                database.execSQL("DROP TABLE DrugsGiven")
                database.execSQL("CREATE TABLE drugsGiven (drugsGivenPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, drugsGivenId TEXT, drugsGivenDrugId TEXT, drugsGivenAmountGiven INTEGER NOT NULL, drugsGivenCowId TEXT, drugsGivenLotId TEXT, drugsGivenDate INTEGER NOT NULL)")

                // update feed table
                database.execSQL("DROP TABLE feed")
                database.execSQL("CREATE TABLE feed (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, feed INTEGER NOT NULL, date INTEGER NOT NULL, id TEXT NOT NULL, lotId TEXT NOT NULL)")

                // update load table
                // don't need to update load

                // update lot table
                database.execSQL("DROP TABLE lot")
                database.execSQL("CREATE TABLE lot (lotPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, lotName TEXT NOT NULL, lotCloudDatabaseId TEXT NOT NULL, customerName TEXT, notes TEXT, date INTEGER NOT NULL, archived INTEGER NOT NULL, dateArchived INTEGER, lotPenCloudDatabaseId TEXT NOT NULL)")

                // TODO: Fix issue with archive migration
                // save archives to lot table
                database.execSQL("INSERT INTO lot (lotPrimaryKey, lotName, lotCloudDatabaseId, customerName, notes, date, archived, dateArchived, lotPenCloudDatabaseId) SELECT 0, lotName, lotId, customerName, notes, dateStarted, 1, dateEnded, '' FROM archivedLot")
                database.execSQL("DROP TABLE archivedLot")

                // update pen table
                database.execSQL("DROP TABLE Pen")
                database.execSQL("CREATE TABLE pen (penPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, penCloudDatabaseId TEXT, penName TEXT NOT NULL)")

                // create ration table
                database.execSQL("CREATE TABLE ration (rationPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, rationCloudDatabaseId TEXT NOT NULL, rationName TEXT NOT NULL)")


                /*
                Cache DB migrations
                 */

                // cacheCall
                database.execSQL("CREATE TABLE cache_call_new (callPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, callAmount INTEGER NOT NULL, date INTEGER NOT NULL, lotId TEXT NOT NULL, callRationId INTEGER, callCloudDatabaseId TEXT, whatHappened INTEGER NOT NULL)")
                database.execSQL("INSERT INTO cache_call_new (callPrimaryKey, callAmount, date, lotId, callRationId, callCloudDatabaseId, whatHappened) SELECT primaryKey, callAmount, date, lotId, NULL, id, whatHappened FROM holdingCall")
                database.execSQL("DROP TABLE holdingCall")
                database.execSQL("ALTER TABLE cache_call_new RENAME TO cache_call")

                // cacheCow
                database.execSQL("CREATE TABLE cache_cow_new (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, alive INTEGER NOT NULL, cowId TEXT NOT NULL, tagNumber INTEGER NOT NULL, date INTEGER NOT NULL, notes TEXT, lotId TEXT NOT NULL, whatHappened INTEGER NOT NULL)")
                database.execSQL("INSERT INTO cache_cow_new (primaryKey, alive, cowId, tagNumber, date, notes, lotId, whatHappened) SELECT primaryKey, isAlive, cowId, tagNumber, date, notes, lotId, whatHappened FROM HoldingCow")
                database.execSQL("DROP TABLE HoldingCow")
                database.execSQL("ALTER TABLE cache_cow_new RENAME TO cache_cow")

                // cacheDrug
                database.execSQL("CREATE TABLE cache_drug_new (drugPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, defaultAmount INTEGER NOT NULl, drugCloudDatabaseId TEXT NOT NULL, drugName TEXT NOT NULL, whatHappened INTEGER NOT NULL)")
                database.execSQL("INSERT INTO cache_drug_new (drugPrimaryKey, defaultAmount, drugCloudDatabaseId, drugName, whatHappened) SELECT primaryKey, defaultAmount, drugId, drugName, whatHappened FROM HoldingDrug")
                database.execSQL("DROP TABLE HoldingDrug")
                database.execSQL("ALTER TABLE cache_drug_new RENAME TO cache_drug")

                // cacheDrugsGiven
                database.execSQL("CREATE TABLE cache_drugs_given_new (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, drugGivenId TEXT, drugId TEXT, amountGiven INTEGER NOT NULL, cowId TEXT, lotId TEXT NOT NULL, date INTEGER NOT NULL, whatHappened INTEGER NOT NULL )")
                database.execSQL("INSERT INTO cache_drugs_given_new (primaryKey, drugGivenId, drugId, amountGiven, cowId, lotId, date, whatHappened) SELECT primaryKey, drugGivenId, drugId, amountGiven, cowId, lotId, date, whatHappened FROM HoldingDrugsGiven")
                database.execSQL("DROP TABLE HoldingDrugsGiven")
                database.execSQL("ALTER TABLE cache_drugs_given_new RENAME TO cache_drugs_given")

                // cacheFeed
                database.execSQL("CREATE TABLE feed_cache_new (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, feed INTEGER NOT NULL, date INTEGER NOT NULL, id TEXT NOT NULL, lotId TEXT NOT NULL, whatHappened INTEGER NOT NULL)")
                database.execSQL("INSERT INTO feed_cache_new (primaryKey, feed, date, id, lotId, whatHappened) SELECT primaryKey, feed, date, id, lotId, whatHappened FROM holdingFeed")
                database.execSQL("DROP TABLE holdingFeed")
                database.execSQL("ALTER TABLE feed_cache_new RENAME TO cache_feed")

                // cacheLoad
                database.execSQL("ALTER TABLE holdingLoad RENAME TO cache_load")

                // cacheLot
                database.execSQL("CREATE TABLE cache_lot_new (lotPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, lotName TEXT NOT NULL, lotCloudDatabaseId TEXT NOT NULL, customerName TEXT, notes TEXT, date INTEGER NOT NULL, archived INTEGER NOT NULL, dateArchived INTEGER NOT NULL, lotPenCloudDatabaseId TEXT NOT NULL, whatHappened INTEGER NOT NULL)")
                database.execSQL("INSERT INTO cache_lot_new (lotPrimaryKey, lotName, lotCloudDatabaseId, customerName, notes, date, archived, dateArchived, lotPenCloudDatabaseId, whatHappened) SELECT primaryKey, lotName, lotId, customerName, notes, date, 0, 0, penId, whatHappened FROM holdingLot")
                database.execSQL("DROP TABLE holdingLot")
                database.execSQL("ALTER TABLE cache_lot_new RENAME TO cache_lot")

                // save cacheArchivedLot to cacheLot table
                database.execSQL("INSERT INTO cache_lot (lotPrimaryKey, lotName, lotCloudDatabaseId, customerName, notes, date, archived, dateArchived, lotPenCloudDatabaseId, whatHappened) SELECT 0, lotName, lotId, customerName, notes, dateStarted, 1, dateEnded, '', whatHappened FROM holdingArchivedLot")
                database.execSQL("DROP TABLE holdingArchivedLot")

                // cachePen
                database.execSQL("CREATE TABLE cache_pen_new (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, penCloudDatabaseId TEXT, penName TEXT NOT NULL, whatHappened INTEGER NOT NULL)")
                database.execSQL("INSERT INTO cache_pen_new (primaryKey, penCloudDatabaseId, penName, whatHappened) SELECT primaryKey, penId, penName, whatHappened FROM HoldingPen")
                database.execSQL("DROP TABLE HoldingPen")
                database.execSQL("ALTER TABLE cache_pen_new RENAME TO cache_pen")

                // cacheRation
                database.execSQL("CREATE TABLE cache_ration (rationPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, rationCloudDatabaseId TEXT NOT NULL, rationName TEXT NOT NULL, whatHappened INTEGER NOT NULL)")

                // cacheUser
                database.execSQL("ALTER TABLE holdingUser RENAME TO cache_user")

            }
        }
        return Room
                .databaseBuilder(app, AppDatabase::class.java, "track_a_cow_db")
                .addMigrations(migration1to2)
                .addMigrations(migration2to3)
                .addMigrations(migration3to4)
                .addMigrations(migration4to5)
                .addMigrations(migration5to6)
                .build()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(
        app: Application
    ): SharedPreferences{
        return app.getSharedPreferences("shared_pref", MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providePreferences(sharedPreferences: SharedPreferences): AppPreferences {
        return AppPreferencesImpl(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideCalculateDrugsGiven(): CalculateDrugsGiven {
        return CalculateDrugsGiven()
    }

    @Provides
    @Singleton
    fun provideCalculateDayStartAndDayEnd(): CalculateDayStartAndDayEnd {
        return CalculateDayStartAndDayEnd()
    }

    @Provides
    @Singleton
    fun provideInitiateCloudDatabaseMigration5to6(
        firebaseFunctions: FirebaseFunctions
    ): InitiateCloudDatabaseMigration5to6 {
        return InitiateCloudDatabaseMigration5to6(firebaseFunctions)
    }

    @Provides
    @Singleton
    fun providesUploadCache(
        callRepository: CallRepository,
        callRepositoryRemote: CallRepositoryRemote,
        cowRepository: CowRepository,
        cowRepositoryRemote: CowRepositoryRemote,
        drugsGivenRepository: DrugsGivenRepository,
        drugsGivenRepositoryRemote: DrugsGivenRepositoryRemote,
        drugRepository: DrugRepository,
        drugRepositoryRemote: DrugRepositoryRemote,
        feedRepository: FeedRepository,
        feedRepositoryRemote: FeedRepositoryRemote,
        loadRepository: LoadRepository,
        loadRemoteRepository: LoadRemoteRepository,
        lotRepository: LotRepository,
        lotRepositoryRemote: LotRepositoryRemote,
        penRepository: PenRepository,
        penRepositoryRemote: PenRepositoryRemote,
        rationsRepository: RationsRepository,
        rationRepositoryRemote: RationRepositoryRemote,
        context: Application
    ): UploadCache {
        return UploadCache(
            callRepository, callRepositoryRemote,
            cowRepository, cowRepositoryRemote,
            drugRepository, drugRepositoryRemote,
            drugsGivenRepository, drugsGivenRepositoryRemote,
            feedRepository, feedRepositoryRemote,
            loadRepository, loadRemoteRepository,
            lotRepository, lotRepositoryRemote,
            penRepository, penRepositoryRemote,
            rationsRepository, rationRepositoryRemote,
            context
        )
    }

    @Provides
    @Singleton
    fun providesDeleteAllLocalData(
        callRepository: CallRepository,
        cowRepository: CowRepository,
        drugRepository: DrugRepository,
        drugsGivenRepository: DrugsGivenRepository,
        feedRepository: FeedRepository,
        loadRepository: LoadRepository,
            lotRepository: LotRepository,
            penRepository: PenRepository,
            rationsRepository: RationsRepository
    ): DeleteAllLocalData {
        return DeleteAllLocalData(
                callRepository,
                cowRepository,
                drugRepository,
                drugsGivenRepository,
                feedRepository,
                loadRepository,
                lotRepository,
                penRepository,
                rationsRepository
        )
    }

    @Provides
    @Singleton
    fun provideRationRepository(
            db: AppDatabase
    ): RationsRepository {
        return RationRepositoryImpl(
                rationDao = db.rationDao(),
                cacheRationDao = db.cacheRationDao()
        )
    }

    @Provides
    @Singleton
    fun provideCallRepository(
        db: AppDatabase
    ): CallRepository {
        return CallRepositoryImpl(
            callDao = db.callDao(),
            cacheCallDao = db.cacheCallDao()
        )
    }

    @Provides
    @Singleton
    fun provideCowRepository(
        db: AppDatabase
    ): CowRepository {
        return CowRepositoryImpl(
            cowDao = db.cowDao(),
            cacheCowDao = db.cacheCowDao()
        )
    }

    @Provides
    @Singleton
    fun provideDrugRepository(
        db: AppDatabase
    ): DrugRepository {
        return DrugRepositoryImpl(
            drugDao = db.drugDao(),
            cacheDrugDao = db.cacheDrugDao()
        )
    }

    @Provides
    @Singleton
    fun provideDrugsGivenRepository(
        db: AppDatabase
    ): DrugsGivenRepository {
        return DrugsGivenRepositoryImpl(
            drugsGivenDao = db.drugsGivenDao(),
            cacheDrugsGivenDao = db.cacheDrugsGivenDao()
        )
    }

    @Provides
    @Singleton
    fun providePenRepository(
        db: AppDatabase
    ): PenRepository {
        return PenRepositoryImpl(
            penDao = db.penDao(),
            cachePenDao = db.cachePenDao()
        )
    }

    @Provides
    @Singleton
    fun provideLotRepository(
        db: AppDatabase
    ): LotRepository {
        return LotRepositoryImpl(
            lotDao = db.lotDao(),
            cacheLotDao = db.cacheLotDao()
        )
    }

    @Provides
    @Singleton
    fun provideLoadRepository(
        db: AppDatabase
    ): LoadRepository {
        return LoadRepositoryImpl(
            loadDao = db.loadDao(),
            cacheLoadDao = db.cacheLoadDao()
        )
    }

    @Provides
    @Singleton
    fun provideFeedRepository(
        db: AppDatabase
    ): FeedRepository {
        return FeedRepositoryImpl(
            feedDao = db.feedDao(),
            cacheFeedDao = db.cacheFeedDao()
        )
    }
}