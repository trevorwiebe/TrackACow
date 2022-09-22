package com.trevorwiebe.trackacow.data.di

import android.app.Application
import androidx.room.Room
import com.trevorwiebe.trackacow.data.db.AppDatabase
import com.trevorwiebe.trackacow.data.repository.RationRepositoryImpl
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
}