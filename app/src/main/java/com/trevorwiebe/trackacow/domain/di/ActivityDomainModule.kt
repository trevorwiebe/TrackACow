package com.trevorwiebe.trackacow.domain.di

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.*
import com.trevorwiebe.trackacow.domain.repository.remote.*
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.CreateAndUpdateFeedList
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.FeedUseCases
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.ReadFeedsAndRationsTotalsByLotIdAndDate
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.ReadFeedsByLotIdAndDate
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.ReadFeedsByLotId
import com.trevorwiebe.trackacow.domain.use_cases.load_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.ReadPenAndLotModelIncludeEmptyPens
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
        cowRepository: CowRepository,
        cowRepositoryRemote: CowRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application,
    ): CowUseCases {
        return CowUseCases(
            createCow = CreateCow(cowRepository, cowRepositoryRemote, getCloudDatabaseId, context),
            readDeadCowsByLotId = ReadDeadCowsByLotId(
                    cowRepository,
                    cowRepositoryRemote,
                    context
            ),
            readCowsByLotId = ReadCowsByLotId(
                    cowRepository,
                    cowRepositoryRemote,
                    context
            ),
            updateCow = UpdateCow(cowRepository, cowRepositoryRemote, context),
            deleteCow = DeleteCow(cowRepository, cowRepositoryRemote, context),
            readCowByCowId = ReadCowByCowId(
                    cowRepository,
                    cowRepositoryRemote,
                    context
            )
        )
    }

    @ActivityScoped
    @Provides
    fun provideRationUseCases(
        rationsRepository: RationsRepository,
        rationsRepositoryRemote: RationRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application,
    ): RationUseCases {
        return RationUseCases(
            createRationUC = CreateRationUC(
                rationsRepository,
                rationsRepositoryRemote,
                getCloudDatabaseId,
                context
            ),
            readAllRationsUC = ReadAllRationsUC(
                    rationsRepository,
                    rationsRepositoryRemote,
                    context
            ),
            updateRationUC = UpdateRationUC(rationsRepository, rationsRepositoryRemote, context),
            deleteRationByIdUC = DeleteRationByIdUC(
                rationsRepository,
                rationsRepositoryRemote,
                context
            )
        )
    }

    @ActivityScoped
    @Provides
    fun provideCallUseCases(
        rationsRepository: RationsRepository,
        callRepository: CallRepository,
        callRepositoryRemote: CallRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): CallUseCases {
        return CallUseCases(
            readCallsByLotIdAndDateUC = ReadCallByLotIdAndDateUC(
                    rationsRepository,
                    callRepository,
                    callRepositoryRemote,
                    context
            ),
            readCallsAndRationsByLotId = ReadCallsAndRationsByLotIdUC(
                    rationsRepository,
                    callRepository,
                    callRepositoryRemote,
                    context
            ),
            createCallUC = CreateCallUC(
                callRepository,
                callRepositoryRemote,
                getCloudDatabaseId,
                context
            ),
            updateCallUC = UpdateCallUC(callRepository, callRepositoryRemote, context)
        )
    }

    @ActivityScoped
    @Provides
    fun providePenUseCases(
        penRepository: PenRepository,
        lotRepository: LotRepository,
        lotRepositoryRemote: LotRepositoryRemote,
        penRepositoryRemote: PenRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): PenUseCases {
        return PenUseCases(
            readPenAndLotModelIncludeEmptyPens = ReadPenAndLotModelIncludeEmptyPens(
                    penRepository,
                    lotRepository,
                    lotRepositoryRemote,
                    context
            ),
            readPenAndLotModelExcludeEmptyPens = ReadPenAndLotModelExcludeEmptyPens(
                    penRepository,
                    lotRepository,
                    lotRepositoryRemote,
                    context
            ),
            createPenUC = CreatePenUC(
                penRepository,
                penRepositoryRemote,
                getCloudDatabaseId,
                context
            ),
            deletePenUC = DeletePenUC(penRepository, penRepositoryRemote, context),
            updatePenUC = UpdatePenUC(penRepository, penRepositoryRemote, context)
        )
    }

    @ActivityScoped
    @Provides
    fun provideLotUseCases(
        lotRepository: LotRepository,
        lotRepositoryRemote: LotRepositoryRemote,
        callRepository: CallRepository,
        callRepositoryRemote: CallRepositoryRemote,
        cowRepository: CowRepository,
        cowRepositoryRemote: CowRepositoryRemote,
        drugsGivenRepository: DrugsGivenRepository,
        drugsGivenRepositoryRemote: DrugsGivenRepositoryRemote,
        feedRepository: FeedRepository,
        feedRepositoryRemote: FeedRepositoryRemote,
        loadRepository: LoadRepository,
        loadRemoteRepository: LoadRemoteRepository,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): LotUseCases{
        return LotUseCases(
            createLot = CreateLot(lotRepository, lotRepositoryRemote, getCloudDatabaseId, context),
            readArchivedLots = ReadArchivedLots(
                lotRepository,
                lotRepositoryRemote,
                    context
            ),
            readLots = ReadLots(
                    lotRepository,
                    lotRepositoryRemote,
                    context
            ),
            readLotsByLotId = ReadLotsByLotId(
                    lotRepository,
                    lotRepositoryRemote,
                    context
            ),
            archiveLot = ArchiveLot(lotRepository, lotRepositoryRemote, context),
            updateLot = UpdateLot(lotRepository, lotRepositoryRemote, context),
            deleteLot = DeleteLot(lotRepository, lotRepositoryRemote, context),
            mergeLots = MergeLots(
                lotRepository, lotRepositoryRemote,
                callRepository, callRepositoryRemote,
                cowRepository, cowRepositoryRemote,
                drugsGivenRepository, drugsGivenRepositoryRemote,
                feedRepository, feedRepositoryRemote,
                loadRepository, loadRemoteRepository,
                context
            )
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
            createLoad = CreateLoad(
                loadRepository,
                loadRemoteRepository,
                getCloudDatabaseId,
                context
            ),
            readLoadsByLotId = ReadLoadsByLotId(
                    loadRepository,
                    loadRemoteRepository,
                    context
            ),
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
        feedRepository: FeedRepository,
        feedRepositoryRemote: FeedRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): FeedUseCases {
        return FeedUseCases(
                createAndUpdateFeedList = CreateAndUpdateFeedList(
                        feedRepository,
                        feedRepositoryRemote,
                        getCloudDatabaseId,
                        context
                ),
                readFeedsByLotId = ReadFeedsByLotId(
                        feedRepository,
                        feedRepositoryRemote,
                        context
                ),
                readFeedsByLotIdAndDate = ReadFeedsByLotIdAndDate(
                        feedRepository,
                        feedRepositoryRemote,
                        context
                ),
                readFeedsAndRationsTotalsByLotIdAndDate = ReadFeedsAndRationsTotalsByLotIdAndDate(
                        feedRepository,
                        feedRepositoryRemote,
                        context
                )
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
            readDrugsUC = ReadDrugsUC(
                    drugRepository,
                    drugRepositoryRemote,
                    context
            ),
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
        drugRepository: DrugRepository,
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
            readDrugsGivenAndDrugsByLotId = ReadDrugsGivenAndDrugsByLotId(
                    drugsGivenRepository,
                    drugRepository,
                    drugsGivenRemoteRepository,
                    context
            ),
            readDrugsGivenAndDrugsByLotIdAndDate = ReadDrugsGivenAndDrugsByLotIdAndDate(
                    drugRepository,
                    drugsGivenRepository,
                    drugsGivenRemoteRepository,
                    context
            ),
            deleteDrugsGivenByCowId = DeleteDrugsGivenByCowId(
                drugsGivenRepository,
                drugsGivenRemoteRepository,
                context
            ),
            readDrugsGivenAndDrugsByCowId = ReadDrugsGivenAndDrugsByCowId(
                    drugsGivenRepository,
                    drugRepository,
                    drugsGivenRemoteRepository,
                    context
            ),
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