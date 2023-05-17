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
        firebaseDatabase: FirebaseDatabase,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): CowUseCases {
        return CowUseCases(
            createCow = CreateCow(cowRepository, cowRepositoryRemote, getCloudDatabaseId, context),
            readDeadCowsByLotId = ReadDeadCowsByLotId(
                cowRepository,
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_COW
            ),
            readCowsByLotId = ReadCowsByLotId(
                cowRepository,
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_COW
            ),
            updateCow = UpdateCow(cowRepository, cowRepositoryRemote, context),
            deleteCow = DeleteCow(cowRepository, cowRepositoryRemote, context)
        )
    }

    @ViewModelScoped
    @Provides
    fun provideRationUseCases(
        rationsRepository: RationsRepository,
        rationsRepositoryRemote: RationRepositoryRemote,
        firebaseDatabase: FirebaseDatabase,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
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
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_RATIONS
            ),
            updateRationUC = UpdateRationUC(rationsRepository, rationsRepositoryRemote, context),
            deleteRationByIdUC = DeleteRationByIdUC(
                rationsRepository,
                rationsRepositoryRemote,
                context
            )
        )
    }

    @ViewModelScoped
    @Provides
    fun provideCallUseCases(
        rationsRepository: RationsRepository,
        callRepository: CallRepository,
        callRepositoryRemote: CallRepositoryRemote,
        firebaseDatabase: FirebaseDatabase,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): CallUseCases {
        return CallUseCases(
            readCallsByLotIdAndDateUC = ReadCallByLotIdAndDateUC(
                rationsRepository,
                callRepository,
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_CALLS,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_RATIONS
            ),
            readCallsAndRationsByLotId = ReadCallsAndRationsByLotIdUC(
                rationsRepository,
                callRepository,
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_CALLS,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_RATIONS
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

    @ViewModelScoped
    @Provides
    fun provideLoadUseCases(
        loadRepository: LoadRepository,
        loadRemoteRepository: LoadRemoteRepository,
        firebaseDatabase: FirebaseDatabase,
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
            readLoadsByLotId = ReadLoadsByLotId(
                loadRepository,
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_LOAD
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

    @ViewModelScoped
    @Provides
    fun providePenUseCases(
        penRepository: PenRepository,
        lotRepository: LotRepository,
        penRepositoryRemote: PenRepositoryRemote,
        firebaseDatabase: FirebaseDatabase,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): PenUseCases {
        return PenUseCases(
            readPenAndLotModelIncludeEmptyPens = ReadPenAndLotModelIncludeEmptyPens(
                penRepository,
                lotRepository,
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_PENS,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_LOT
            ),
            readPenAndLotModelExcludeEmptyPens = ReadPenAndLotModelExcludeEmptyPens(
                penRepository,
                lotRepository,
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_PENS,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_LOT
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

    @ViewModelScoped
    @Provides
    fun provideLotUseCases(
        lotRepository: LotRepository,
        lotRepositoryRemote: LotRepositoryRemote,
        firebaseDatabase: FirebaseDatabase,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): LotUseCases {
        return LotUseCases(
            createLot = CreateLot(lotRepository, lotRepositoryRemote, getCloudDatabaseId, context),
            readLots = ReadLots(
                lotRepository,
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_LOT
            ),
            readArchivedLots = ReadArchivedLots(
                lotRepository,
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_LOT
            ),
            readLotsByLotId = ReadLotsByLotId(
                lotRepository,
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_LOT
            ),
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
        drugRepository: DrugRepository,
        drugsGivenRepository: DrugsGivenRepository,
        drugsGivenRemoteRepository: DrugsGivenRepositoryRemote,
        firebaseDatabase: FirebaseDatabase,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): DrugsGivenUseCases {
        return DrugsGivenUseCases(
            createDrugsGivenList = CreateDrugsGivenList(
                drugsGivenRepository, drugsGivenRemoteRepository, getCloudDatabaseId, context
            ),
            readDrugsGivenAndDrugsByLotId = ReadDrugsGivenAndDrugsByLotId(
                drugsGivenRepository,
                drugRepository,
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_DRUGS,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_DRUGS_GIVEN
            ),
            readDrugsGivenAndDrugsByLotIdAndDate = ReadDrugsGivenAndDrugsByLotIdAndDate(
                drugRepository,
                drugsGivenRepository,
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_DRUGS,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_DRUGS_GIVEN
            ),
            deleteDrugsGivenByCowId = DeleteDrugsGivenByCowId(
                drugsGivenRepository,
                drugsGivenRemoteRepository,
                context
            ),
            readDrugsGivenAndDrugsByCowId = ReadDrugsGivenAndDrugsByCowId(
                drugsGivenRepository,
                drugRepository,
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_DRUGS,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_DRUGS_GIVEN
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

    @ViewModelScoped
    @Provides
    fun provideFeedUseCases(
        feedRepository: FeedRepository,
        feedRepositoryRemote: FeedRepositoryRemote,
        firebaseDatabase: FirebaseDatabase,
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
                firebaseDatabase,
                Constants.BASE_REFERENCE_STRING + Constants.DATABASE_STRING_FEEDS
            ),
            readFeedsByLotIdAndDate = ReadFeedsByLotIdAndDate(feedRepository)
        )
    }
}