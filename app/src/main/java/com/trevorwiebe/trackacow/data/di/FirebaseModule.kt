package com.trevorwiebe.trackacow.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.trevorwiebe.trackacow.data.remote.repository.*
import com.trevorwiebe.trackacow.domain.repository.remote.*
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
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
    fun provideFirebaseFunction(): FirebaseFunctions {
        return FirebaseFunctions.getInstance()
    }


    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun provideUserId(auth: FirebaseAuth): String {
        return auth.currentUser?.uid ?: ""
    }

    @Provides
    @Singleton
    fun provideCloudDatabaseId(
        databaseReference: FirebaseDatabase,
        firebaseUserId: Provider<String>
    ): GetCloudDatabaseId {
        return GetCloudDatabaseId(databaseReference, firebaseUserId)
    }

    @Provides
    @Singleton
    fun provideRationRepositoryRemote(
        remoteDb: FirebaseDatabase,
        firebaseUserId: Provider<String>
    ): RationRepositoryRemote {
        return RationRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            firebaseUserId = firebaseUserId
        )
    }

    @Provides
    @Singleton
    fun provideCallRepositoryRemote(
        remoteDb: FirebaseDatabase,
        firebaseUserId: Provider<String>
    ): CallRepositoryRemote {
        return CallRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            firebaseUserId = firebaseUserId
        )
    }

    @Provides
    @Singleton
    fun provideCowRemoteRepository(
        remoteDb: FirebaseDatabase,
        firebaseUserId: Provider<String>
    ): CowRepositoryRemote {
        return CowRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            firebaseUserId = firebaseUserId
        )
    }

    @Provides
    @Singleton
    fun provideDrugRepositoryRemote(
        remoteDb: FirebaseDatabase,
        firebaseUserId: Provider<String>
    ): DrugRepositoryRemote {
        return DrugRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            firebaseUserId = firebaseUserId
        )
    }

    @Provides
    @Singleton
    fun provideDrugsGivenRepositoryRemote(
        remoteDb: FirebaseDatabase,
        firebaseUserId: Provider<String>
    ): DrugsGivenRepositoryRemote {
        return DrugsGivenRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            firebaseUserId = firebaseUserId
        )
    }

    @Provides
    @Singleton
    fun providePenRepositoryRemote(
        remoteDb: FirebaseDatabase,
        firebaseUserId: Provider<String>
    ): PenRepositoryRemote {
        return PenRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            firebaseUserId = firebaseUserId
        )
    }

    @Provides
    @Singleton
    fun providesLoadRemoteRepository(
        remoteDb: FirebaseDatabase,
        firebaseUserId: Provider<String>
    ): LoadRemoteRepository {
        return LoadRemoteRepositoryImpl(
            firebaseDatabase = remoteDb,
            firebaseUserId = firebaseUserId
        )
    }

    @Provides
    @Singleton
    fun provideLotRepositoryRemote(
        remoteDb: FirebaseDatabase,
        firebaseUserId: Provider<String>
    ): LotRepositoryRemote {
        return LotRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            firebaseUserId = firebaseUserId
        )
    }

    @Provides
    @Singleton
    fun provideFeedRepositoryRemote(
        remoteDb: FirebaseDatabase,
        firebaseUserId: Provider<String>
    ): FeedRepositoryRemote {
        return FeedRepositoryRemoteImpl(
            firebaseDatabase = remoteDb,
            firebaseUserId = firebaseUserId
        )
    }
}