package com.trevorwiebe.trackacow.data.di

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trevorwiebe.trackacow.data.remote.repository.*
import com.trevorwiebe.trackacow.domain.repository.remote.*
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideRemoteDatabase(): FirebaseDatabase {
        return Firebase.database
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
    fun provideRationRepositoryRemote(
        remoteDb: FirebaseDatabase
    ): RationRepositoryRemote {
        return RationRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_RATIONS
        )
    }

    @Provides
    @Singleton
    fun provideCallRepositoryRemote(
        remoteDb: FirebaseDatabase
    ): CallRepositoryRemote {
        return CallRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_CALLS
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

    @Provides
    @Singleton
    fun provideDrugRepositoryRemote(
        remoteDb: FirebaseDatabase
    ): DrugRepositoryRemote {
        return DrugRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_DRUGS
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

    @Provides
    @Singleton
    fun providePenRepositoryRemote(
        remoteDb: FirebaseDatabase
    ): PenRepositoryRemote {
        return PenRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_PENS
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
    fun provideFeedRepositoryRemote(
        remoteDb: FirebaseDatabase
    ): FeedRepositoryRemote {
        return FeedRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            databasePath = Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_FEEDS
        )
    }
}