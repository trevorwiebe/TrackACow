package com.trevorwiebe.trackacow.data.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.firebase.functions.FirebaseFunctions
import com.trevorwiebe.trackacow.data.local.repository.*
import com.trevorwiebe.trackacow.data.preferences.AppPreferencesImpl
import com.trevorwiebe.trackacow.data.remote.repository.*
import com.trevorwiebe.trackacow.domain.preferences.AppPreferences
import com.trevorwiebe.trackacow.domain.repository.local.*
import com.trevorwiebe.trackacow.domain.repository.remote.*
import com.trevorwiebe.trackacow.domain.use_cases.CalculateDayStartAndDayEnd
import com.trevorwiebe.trackacow.domain.use_cases.CalculateDrugsGiven
import com.trevorwiebe.trackacow.domain.use_cases.InitiateCloudDatabaseMigration5to6
import com.trevorwiebe.trackacow.domain.use_cases.InitiateCloudDatabaseMigration6to7
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
    fun provideInitiateCloudDatabaseMigration6to7(
        firebaseFunctions: FirebaseFunctions
    ): InitiateCloudDatabaseMigration6to7 {
        return InitiateCloudDatabaseMigration6to7(firebaseFunctions)
    }
}