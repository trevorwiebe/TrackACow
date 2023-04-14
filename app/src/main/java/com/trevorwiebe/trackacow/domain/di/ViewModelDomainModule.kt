package com.trevorwiebe.trackacow.domain.di

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.repository.local.*
import com.trevorwiebe.trackacow.domain.repository.remote.*
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.CreateAndUpdateFeedList
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.FeedUseCases
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.ReadFeedsByLotIdAndDate
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.ReadFeedsByLotId
import com.trevorwiebe.trackacow.domain.use_cases.load_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.ReadPenAndLotModelIncludeEmptyPens
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.ReadPenByPenId
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases.*
import com.trevorwiebe.trackacow.domain.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelDomainModule {

    @ViewModelScoped
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

    @ViewModelScoped
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

    @ViewModelScoped
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

    @ViewModelScoped
    @Provides
    fun provideLoadUseCases(
        loadRepository: LoadRepository,
        loadRemoteRepository: LoadRemoteRepository,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): LoadUseCases {
        return LoadUseCases(
            createLoad = CreateLoad(
                loadRepository,
                loadRemoteRepository,
                getCloudDatabaseId,
                context
            ),
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

    @ViewModelScoped
    @Provides
    fun providePenUseCases(
        penRepository: PenRepository,
        penRepositoryRemote: PenRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): PenUseCases {
        return PenUseCases(
            readPenByPenId = ReadPenByPenId(penRepository),
            readPenAndLotModelIncludeEmptyPens = ReadPenAndLotModelIncludeEmptyPens(penRepository),
            readPenAndLotModelExcludeEmptyPens = ReadPenAndLotModelExcludeEmptyPens(penRepository),
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

    @ViewModelScoped
    @Provides
    fun provideLotUseCases(
        lotRepository: LotRepository,
        lotRepositoryRemote: LotRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): LotUseCases {
        return LotUseCases(
            createLot = CreateLot(lotRepository, lotRepositoryRemote, getCloudDatabaseId, context),
            readLotsByPenId = ReadLotsByPenId(lotRepository),
            readLots = ReadLots(lotRepository),
            readArchivedLots = ReadArchivedLots(lotRepository),
            readLotsByLotId = ReadLotsByLotId(lotRepository),
            archiveLot = ArchiveLot(lotRepository, lotRepositoryRemote, context),
            updateLotWithNewPenIdUC = UpdateLotWithNewPenIdUC(
                lotRepository,
                lotRepositoryRemote,
                context
            ),
            updateLotWithLotId = UpdateLotWithLotId(lotRepository, lotRepositoryRemote, context),
            deleteLot = DeleteLot(lotRepository, lotRepositoryRemote, context)
        )
    }

    @ViewModelScoped
    @Provides
    fun provideDrugUseCases(
        drugRepository: DrugRepository,
        drugRepositoryRemote: DrugRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        firebaseDatabase: FirebaseDatabase,
        context: Application
    ): DrugUseCases{
        return DrugUseCases(
            readDrugsUC = ReadDrugsUC(
                drugRepository,
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_DRUGS
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

    @ViewModelScoped
    @Provides
    fun provideDrugsGivenUseCases(
        drugsGivenRepository: DrugsGivenRepository,
        drugsGivenRemoteRepository: DrugsGivenRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): DrugsGivenUseCases {
        return DrugsGivenUseCases(
            createDrugsGivenList = CreateDrugsGivenList(
                drugsGivenRepository, drugsGivenRemoteRepository, getCloudDatabaseId, context
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

    @ViewModelScoped
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
            readFeedsByLotId = ReadFeedsByLotId(feedRepository),
            readFeedsByLotIdAndDate = ReadFeedsByLotIdAndDate(feedRepository)
        )
    }
}