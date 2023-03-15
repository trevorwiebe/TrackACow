package com.trevorwiebe.trackacow.data.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trevorwiebe.trackacow.data.local.AppDatabase
import com.trevorwiebe.trackacow.data.local.repository.*
import com.trevorwiebe.trackacow.data.preferences.AppPreferencesImpl
import com.trevorwiebe.trackacow.data.remote.repository.*
import com.trevorwiebe.trackacow.domain.preferences.AppPreferences
import com.trevorwiebe.trackacow.domain.repository.local.*
import com.trevorwiebe.trackacow.domain.repository.remote.*
import com.trevorwiebe.trackacow.domain.use_cases.CalculateDrugsGiven
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.utils.Constants
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
    fun provideAppDatabase(app: Application): AppDatabase{
        return Room.databaseBuilder(app, AppDatabase::class.java, "track_a_cow_db")
            .build()
    }

    @Provides
    @Singleton
    fun provideRemoteDatabase(): FirebaseDatabase{
        return Firebase.database
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
    fun provideCloudDatabaseId(
        databaseReference: FirebaseDatabase
    ): GetCloudDatabaseId {
        return GetCloudDatabaseId(databaseReference)
    }

    @Provides
    @Singleton
    fun provideCalculateDrugsGiven(): CalculateDrugsGiven {
        return CalculateDrugsGiven()
    }

    // Ration
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
    fun provideRationRepositoryRemote(
        remoteDb: FirebaseDatabase
    ): RationRepositoryRemote {
        return RationRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_RATIONS
        )
    }

    // Call
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
    fun provideCallRepositoryRemote(
        remoteDb: FirebaseDatabase
    ): CallRepositoryRemote{
        return CallRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_CALLS
        )
    }

    // Cow
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
    fun provideCowRemoteRepository(
        remoteDb: FirebaseDatabase
    ): CowRepositoryRemote {
        return CowRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_COW
        )
    }

    // Drug
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
    fun provideDrugRepositoryRemote(
        remoteDb: FirebaseDatabase
    ): DrugRepositoryRemote{
        return DrugRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_DRUGS
        )
    }

    // DrugsGiven
    @Provides
    @Singleton
    fun provideDrugsGivenRepository(
        db: AppDatabase
    ): DrugsGivenRepository {
        return DrugsGivenRepositoryImpl(
            drugsGivenDao = db.drugsGivenDao()
        )
    }

    @Provides
    @Singleton
    fun provideDrugsGivenRepositoryRemote(
        remoteDb: FirebaseDatabase
    ): DrugsGivenRepositoryRemote {
        return DrugsGivenRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_DRUGS_GIVEN
        )
    }

    // Pen
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
    fun providePenRepositoryRemote(
        remoteDb: FirebaseDatabase
    ): PenRepositoryRemote{
        return PenRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_PENS
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
    fun providesLoadRemoteRepository(
        remoteDb: FirebaseDatabase,
    ): LoadRemoteRepository {
        return LoadRemoteRepositoryImpl(
            firebaseDatabase = remoteDb,
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_LOAD
        )
    }

    @Provides
    @Singleton
    fun provideLotRepositoryRemote(
        remoteDb: FirebaseDatabase
    ): LotRepositoryRemote {
        return LotRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_LOT
        )
    }

    @Provides
    @Singleton
    fun provideFeedRepository(
        db: AppDatabase
    ): FeedRepository {
        return FeedRepositoryImpl(
            feedDao = db.feedDao()
        )
    }
}