package com.trevorwiebe.trackacow.data.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.trevorwiebe.trackacow.data.local.AppDatabase
import com.trevorwiebe.trackacow.data.local.repository.*
import com.trevorwiebe.trackacow.data.preferences.AppPreferencesImpl
import com.trevorwiebe.trackacow.data.remote.repository.*
import com.trevorwiebe.trackacow.domain.preferences.AppPreferences
import com.trevorwiebe.trackacow.domain.repository.local.*
import com.trevorwiebe.trackacow.domain.repository.remote.*
import com.trevorwiebe.trackacow.domain.use_cases.CalculateDayStartAndDayEnd
import com.trevorwiebe.trackacow.domain.use_cases.CalculateDrugsGiven
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
        val migration5to6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {

                // update call table
                database.execSQL("CREATE TABLE call_new (callPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, callAmount INTEGER NOT NULL, date INTEGER NOT NULL, lotId TEXT NOT NULL, callRationId INTEGER, callCloudDatabaseId TEXT)")
                database.execSQL("INSERT INTO call_new (callPrimaryKey, callAmount, date, lotId, callRationId, callCloudDatabaseId) SELECT primaryKey, callAmount, date, lotId, -1, id FROM call")
                database.execSQL("DROP TABLE call")
                database.execSQL("ALTER TABLE call_new RENAME TO call")

                // update cow table
                database.execSQL("CREATE TABLE cow_new (primaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, isAlive INTEGER NOT NULL, cowId TEXT NOT NULL, tagNumber INTEGER NOT NULL, date INTEGER NOT NULL, notes TEXT, lotId TEXT NOT NULL)")
                database.execSQL("INSERT INTO cow_new (primaryKey, isAlive, cowId, tagNumber, date, notes, lotId) SELECT primaryKey, isAlive, cowId, tagNumber, date, notes, lotId FROM Cow")
                database.execSQL("DROP TABLE Cow")
                database.execSQL("ALTER TABLE cow_new RENAME TO cow")

                // update drug table
                database.execSQL("CREATE TABLE drug_new (drugPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, defaultAmount INTEGER NOT NULL, drugCloudDatabaseId TEXT NOT NULL, drugName TEXT NOT NULL)")
                database.execSQL("INSERT INTO drug_new (drugPrimaryKey, defaultAmount, drugCloudDatabaseId, drugName) SELECT primaryKey, defaultAmount, drugId, drugName FROM Drug")
                database.execSQL("DROP TABLE Drug")
                database.execSQL("ALTER TABLE drug_new RENAME TO drug")

                // update drugsGiven table
                database.execSQL("CREATE TABLE drugsGiven_new (drugsGivenPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, drugsGivenId TEXT, drugsGivenDrugId TEXT, drugsGivenAmountGiven INTEGER NOT NULL, drugsGivenCowId TEXT, drugsGivenLotId TEXT, drugsGivenDate INTEGER NOT NULL)")
                database.execSQL("INSERT INTO drugsGiven_new (drugsGivenPrimaryKey, drugsGivenId, drugsGivenDrugId, drugsGivenAmountGiven, drugsGivenCowId, drugsGivenLotId, drugsGivenDate) SELECT primaryKey, drugGivenId, drugId, amountGiven, cowId, lotId, date FROM DrugsGiven")
                database.execSQL("DROP TABLE DrugsGiven")
                database.execSQL("ALTER TABLE drugsGiven_new RENAME TO drugs_given")

                // update feed table

                // update load table

                // update lot table
                database.execSQL("CREATE TABLE lot_new (lotPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, lotName TEXT NOT NULL, lotCloudDatabaseId TEXT NOT NULL, customerName TEXT, notes TEXT, date INTEGER NOT NULL, archived INTEGER NOT NULL, dateArchived INTEGER, lotPenCloudDatabaseId TEXT NOT NULL)")
                database.execSQL("INSERT INTO lot_new(lotPrimaryKey, lotName, lotCloudDatabaseId, customerName, notes, date, archived, dateArchived, lotPenCloudDatabaseId) SELECT primaryKey, lotName, lotId, customerName, notes, date, 0, 0, penId FROM lot")
                database.execSQL("DROP TABLE lot")
                database.execSQL("ALTER TABLE lot_new RENAME TO lot")

                // update pen table
                database.execSQL("CREATE TABLE pen_new (penPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, penCloudDatabaseId TEXT, penName TEXT NOT NULL)")
                database.execSQL("INSERT INTO pen_new(penPrimaryKey, penCloudDatabaseId, penName) SELECT primaryKey, penId, penName FROM Pen")
                database.execSQL("DROP TABLE Pen")
                database.execSQL("ALTER TABLE pen_new RENAME TO pen")

                // create ration and holdingRation table
                database.execSQL("CREATE TABLE ration (rationPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, rationCloudDatabaseId TEXT NOT NULL, rationName TEXT NOT NULL)")
                database.execSQL("CREATE TABLE holdingRation(rationPrimaryKey INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, rationCloudDatabaseId TEXT, rationName TEXT NOT NULL, whatHappened INTEGER NOT NULL)")

            }
        }
        return Room
                .databaseBuilder(app, AppDatabase::class.java, "track_a_cow_db")
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