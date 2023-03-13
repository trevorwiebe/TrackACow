package com.trevorwiebe.trackacow.domain.di

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.*
import com.trevorwiebe.trackacow.domain.repository.remote.*
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.use_cases.archive_lot_use_cases.ArchiveLotUseCases
import com.trevorwiebe.trackacow.domain.use_cases.archive_lot_use_cases.CreateArchiveLot
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.FeedUseCases
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.ReadFeedsByDate
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
    fun provideArchiveLotUseCases(
        archiveLotRepository: ArchiveLotRepository,
        archiveLotRepositoryRemote: ArchiveLotRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): ArchiveLotUseCases {
        return ArchiveLotUseCases(
            createArchiveLot = CreateArchiveLot(
                archiveLotRepository,
                archiveLotRepositoryRemote,
                getCloudDatabaseId,
                context
            )
        )
    }

    @ActivityScoped
    @Provides
    fun provideCowUseCases(
        cowRepository: CowRepository,
        cowRepositoryRemote: CowRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): CowUseCases {
        return CowUseCases(
            createCow = CreateCow(cowRepository, cowRepositoryRemote, getCloudDatabaseId, context),
            readDeadCowsByLotId = ReadDeadCowsByLotId(cowRepository),
            readCowsByLotId = ReadCowsByLotId(cowRepository),
            updateCow = UpdateCow(cowRepository, cowRepositoryRemote, context),
            deleteCow = DeleteCow(cowRepository, cowRepositoryRemote, context)
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
            updateLotWithNewPenIdUC = UpdateLotWithNewPenIdUC(
                lotRepository,
                lotRepositoryRemote,
                context
            ),
            updateLotWithLotId = UpdateLotWithLotId(lotRepository, lotRepositoryRemote, context),
            deleteLot = DeleteLot(lotRepository, lotRepositoryRemote, context)
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
            updateLoad = UpdateLoad(loadRepository, loadRemoteRepository, context),
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
            readFeedsByLotId = ReadFeedsByLotId(feedRepository),
            readFeedsByDate = ReadFeedsByDate(feedRepository)
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
            createDrug = CreateDrug(
                drugRepository,
                drugRepositoryRemote,
                getCloudDatabaseId,
                context
            ),
            updateDrug = UpdateDrug(drugRepository, drugRepositoryRemote, context),
            deleteDrug = DeleteDrug(drugRepository, drugRepositoryRemote, context)
        )
    }

    @ActivityScoped
    @Provides
    fun provideDrugsGivenUseCases(
        drugsGivenRepository: DrugsGivenRepository,
        drugsGivenRemoteRepository: DrugsGivenRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): DrugsGivenUseCases{
        return DrugsGivenUseCases(
            createDrugsGivenList = CreateDrugsGivenList(
                drugsGivenRepository,
                drugsGivenRemoteRepository,
                getCloudDatabaseId,
                context
            ),
            readDrugsGivenAndDrugsByLotId = ReadDrugsGivenAndDrugsByLotId(drugsGivenRepository),
            readDrugsGivenAndDrugsByLotIdAndDate = ReadDrugsGivenAndDrugsByLotIdAndDate(
                drugsGivenRepository
            ),
            deleteDrugsGivenByCowId = DeleteDrugsGivenByCowId(
                drugsGivenRepository,
                drugsGivenRemoteRepository,
                context
            ),
            readDrugsGivenAndDrugsByCowId = ReadDrugsGivenAndDrugsByCowId(drugsGivenRepository),
            deleteDrugGivenByDrugGivenId = DeleteDrugGivenByDrugGivenId(
                drugsGivenRepository,
                drugsGivenRemoteRepository,
                context
            ),
            updateDrugGiven = UpdateDrugGiven(
                drugsGivenRepository,
                drugsGivenRemoteRepository,
                context
            )
        )
    }
}