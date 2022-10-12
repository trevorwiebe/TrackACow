package com.trevorwiebe.trackacow.data.di

import android.app.Application
import androidx.room.Room
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trevorwiebe.trackacow.data.local.AppDatabase
import com.trevorwiebe.trackacow.data.local.repository.*
import com.trevorwiebe.trackacow.data.remote.repository.CallRepositoryRemoteImpl
import com.trevorwiebe.trackacow.data.remote.repository.LotRepositoryRemoteImpl
import com.trevorwiebe.trackacow.data.remote.repository.PenRepositoryRemoteImpl
import com.trevorwiebe.trackacow.data.remote.repository.RationRepositoryRemoteImpl
import com.trevorwiebe.trackacow.domain.repository.local.*
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.PenRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.RationRepositoryRemote
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
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.RATIONS
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
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.CALLS
        )
    }

    // Drug
    @Provides
    @Singleton
    fun provideDrugRepository(
        db: AppDatabase
    ): DrugRepository {
        return DrugRepositoryImpl(
            drugDao = db.drugDao()
        )
    }

    @Provides
    @Singleton
    fun providePenRepository(
        db: AppDatabase
    ): PenRepository{
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
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.DATABASE_PENS
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