package com.trevorwiebe.trackacow.data.di

import android.app.Application
import androidx.room.Room
import com.trevorwiebe.trackacow.data.local.AppDatabase
import com.trevorwiebe.trackacow.data.repository.CallRepositoryImpl
import com.trevorwiebe.trackacow.data.repository.RationRepositoryImpl
import com.trevorwiebe.trackacow.domain.repository.CallRepository
import com.trevorwiebe.trackacow.domain.repository.RationsRepository
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
    fun provideRationRepository(
        db: AppDatabase
    ): RationsRepository{
        return RationRepositoryImpl(
            rationDao = db.rationDao(),
            holdingRationDao = db.holdingRationDao()
        )
    }

    @Provides
    @Singleton
    fun provideCallRepository(
        db: AppDatabase
    ): CallRepository{
        return CallRepositoryImpl(
            callDao = db.callDao(),
            holdingCallDao = db.holdingCallDao()
        )
    }
}