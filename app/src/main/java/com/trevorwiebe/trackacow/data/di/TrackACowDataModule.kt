package com.trevorwiebe.trackacow.data.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
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
    fun provideAppDatabase(app: Application): AppDatabase{
        return Room.databaseBuilder(app, AppDatabase::class.java, "track_a_cow_db")
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
            drugsGivenDao = db.drugsGivenDao()
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