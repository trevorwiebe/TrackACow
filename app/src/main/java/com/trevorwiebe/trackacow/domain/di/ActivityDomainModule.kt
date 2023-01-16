package com.trevorwiebe.trackacow.domain.di

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.*
import com.trevorwiebe.trackacow.domain.repository.remote.*
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases.CowUseCases
import com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases.ReadCowsByLotId
import com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases.ReadDeadCowsByLotId
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.CreateDrug
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.DrugUseCases
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.ReadDrugsUC
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.UpdateDrug
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.DrugsGivenUseCases
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.ReadDrugsGivenAndDrugsByLotId
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.FeedUseCases
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.ReadFeedsByLotId
import com.trevorwiebe.trackacow.domain.use_cases.load_use_cases.DeleteLoad
import com.trevorwiebe.trackacow.domain.use_cases.load_use_cases.LoadUseCases
import com.trevorwiebe.trackacow.domain.use_cases.load_use_cases.ReadLoadsByLotId
import com.trevorwiebe.trackacow.domain.use_cases.load_use_cases.UpdateLoad
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.ReadPenAndLotModelUC
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.ReadPenByPenId
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.ReadPens
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object ActivityDomainModule {

    @ActivityScoped
    @Provides
    fun provideCowUseCases(
        cowRepository: CowRepository
    ): CowUseCases {
        return CowUseCases(
            readDeadCowsByLotId = ReadDeadCowsByLotId(cowRepository),
            readCowsByLotId = ReadCowsByLotId(cowRepository)
        )
    }

    @ActivityScoped
    @Provides
    fun provideRationUseCases(
        rationsRepository: RationsRepository,
        rationsRepositoryRemote: RationRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): RationUseCases {
        return RationUseCases(
            createRationUC = CreateRationUC(rationsRepository, rationsRepositoryRemote, getCloudDatabaseId, context),
            readAllRationsUC = ReadAllRationsUC(rationsRepository),
            updateRationUC = UpdateRationUC(rationsRepository, rationsRepositoryRemote, context),
            deleteRationByIdUC = DeleteRationByIdUC(rationsRepository, rationsRepositoryRemote, context)
        )
    }

    @ActivityScoped
    @Provides
    fun provideCallUseCases(
            callRepository: CallRepository,
            callRepositoryRemote: CallRepositoryRemote,
            getCloudDatabaseId: GetCloudDatabaseId,
            context: Application
    ): CallUseCases {
        return CallUseCases(
                readCallsByLotIdAndDateUC = ReadCallByLotIdAndDateUC(callRepository),
                readCallsAndRationsByLotId = ReadCallsAndRationsByLotIdUC(callRepository),
                createCallUC = CreateCallUC(callRepository, callRepositoryRemote, getCloudDatabaseId, context),
                updateCallUC = UpdateCallUC(callRepository, callRepositoryRemote, context)
        )
    }

    @ActivityScoped
    @Provides
    fun providePenUseCases(
        penRepository: PenRepository,
        penRepositoryRemote: PenRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): PenUseCases {
        return PenUseCases(
            readPens = ReadPens(penRepository),
            readPenByPenId = ReadPenByPenId(penRepository),
            readPenAndLotModelUC = ReadPenAndLotModelUC(penRepository),
            createPenUC = CreatePenUC(penRepository, penRepositoryRemote, getCloudDatabaseId, context),
            deletePenUC = DeletePenUC(penRepository, penRepositoryRemote, context),
            updatePenUC = UpdatePenUC(penRepository, penRepositoryRemote, context)
        )
    }

    @ActivityScoped
    @Provides
    fun provideLotUseCases(
        lotRepository: LotRepository,
        lotRepositoryRemote: LotRepositoryRemote,
        context: Application
    ): LotUseCases{
        return LotUseCases(
            readLotsByPenId = ReadLotsByPenId(lotRepository),
            readLots = ReadLots(lotRepository),
            readLotsByLotId = ReadLotsByLotId(lotRepository),
            updateLotWithNewPenIdUC = UpdateLotWithNewPenIdUC(lotRepository, lotRepositoryRemote, context),
            updateLotWithLotId = UpdateLotWithLotId(lotRepository, lotRepositoryRemote, context)
        )
    }

    @ActivityScoped
    @Provides
    fun provideLoadUseCases(
        loadRepository: LoadRepository,
        loadRemoteRepository: LoadRemoteRepository,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): LoadUseCases{
        return LoadUseCases(
            readLoadsByLotId = ReadLoadsByLotId(loadRepository),
            updateLoad = UpdateLoad(loadRepository),
            deleteLoad = DeleteLoad(
                loadRepository,
                loadRemoteRepository,
                getCloudDatabaseId,
                context
            )
        )
    }

    @ActivityScoped
    @Provides
    fun provideFeedUseCases(
        feedRepository: FeedRepository
    ): FeedUseCases {
        return FeedUseCases(
            readFeedsByLotId = ReadFeedsByLotId(feedRepository)
        )
    }

    @ActivityScoped
    @Provides
    fun provideDrugUseCases(
        drugRepository: DrugRepository,
        drugRepositoryRemote: DrugRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): DrugUseCases {
        return DrugUseCases(
            readDrugsUC = ReadDrugsUC(drugRepository),
            createDrug = CreateDrug(drugRepository, drugRepositoryRemote, getCloudDatabaseId, context),
            updateDrug = UpdateDrug(
                drugRepository,
                drugRepositoryRemote,
                getCloudDatabaseId,
                context
            )
        )
    }

    @ActivityScoped
    @Provides
    fun provideDrugsGivenUseCases(
        drugsGivenRepository: DrugsGivenRepository
    ): DrugsGivenUseCases{
        return DrugsGivenUseCases(
            readDrugsGivenAndDrugsByLotId = ReadDrugsGivenAndDrugsByLotId(drugsGivenRepository)
        )
    }
}