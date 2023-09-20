package com.trevorwiebe.trackacow.test_utils.di

import android.app.Application
import androidx.room.Room
import com.trevorwiebe.trackacow.data.di.RoomPersistenceModule
import com.trevorwiebe.trackacow.data.local.AppDatabase
import com.trevorwiebe.trackacow.data.local.repository.CallRepositoryImpl
import com.trevorwiebe.trackacow.data.local.repository.CowRepositoryImpl
import com.trevorwiebe.trackacow.data.local.repository.DrugRepositoryImpl
import com.trevorwiebe.trackacow.data.local.repository.DrugsGivenRepositoryImpl
import com.trevorwiebe.trackacow.data.local.repository.FeedRepositoryImpl
import com.trevorwiebe.trackacow.data.local.repository.LoadRepositoryImpl
import com.trevorwiebe.trackacow.data.local.repository.LotRepositoryImpl
import com.trevorwiebe.trackacow.data.local.repository.PenRepositoryImpl
import com.trevorwiebe.trackacow.data.local.repository.RationRepositoryImpl
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import com.trevorwiebe.trackacow.domain.repository.local.DrugRepository
import com.trevorwiebe.trackacow.domain.repository.local.DrugsGivenRepository
import com.trevorwiebe.trackacow.domain.repository.local.FeedRepository
import com.trevorwiebe.trackacow.domain.repository.local.LoadRepository
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.local.PenRepository
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.CowRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.DrugRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.DrugsGivenRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.FeedRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.LoadRemoteRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.PenRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.RationRepositoryRemote
import com.trevorwiebe.trackacow.domain.use_cases.DatabaseVersionHelper
import com.trevorwiebe.trackacow.domain.use_cases.DeleteAllLocalData
import com.trevorwiebe.trackacow.domain.use_cases.UploadCache
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RoomPersistenceModule::class]
)
object TestRoomPersistenceModule {

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.inMemoryDatabaseBuilder(app.applicationContext, AppDatabase::class.java).build()
    }

    @Provides
    @Singleton
    fun provideDatabaseVersionHelper(
        roomDatabase: AppDatabase
    ): DatabaseVersionHelper {
        return DatabaseVersionHelper(roomDatabase)
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