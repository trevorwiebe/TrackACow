package com.trevorwiebe.trackacow.domain.di

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.*
import com.trevorwiebe.trackacow.domain.repository.remote.*
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.CreateDrug
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.DrugUseCases
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.ReadDrugsUC
import com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases.UpdateDrug
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
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelDomainModule {

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
        context: Application
    ): CallUseCases {
        return CallUseCases(
            readCallsByLotIdAndDateUC = ReadCallByLotIdAndDateUC(callRepository),
            readCallsAndRationsByLotId = ReadCallsAndRationsByLotIdUC(callRepository),
            createCallUC = CreateCallUC(callRepository, callRepositoryRemote, context),
            updateCallUC = UpdateCallUC(callRepository, callRepositoryRemote, context)
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
            readPens = ReadPens(penRepository),
            readPenByPenId = ReadPenByPenId(penRepository),
            readPenAndLotModelUC = ReadPenAndLotModelUC(penRepository),
            createPenUC = CreatePenUC(penRepository, penRepositoryRemote, getCloudDatabaseId, context),
            deletePenUC = DeletePenUC(penRepository, penRepositoryRemote, context),
            updatePenUC = UpdatePenUC(penRepository, penRepositoryRemote, context)
        )
    }

    @ViewModelScoped
    @Provides
    fun provideLotUseCases(
        lotRepository: LotRepository,
        lotRepositoryRemote: LotRepositoryRemote,
        context: Application
    ): LotUseCases {
        return LotUseCases(
            readLotsByPenId = ReadLotsByPenId(lotRepository),
            readLots = ReadLots(lotRepository),
            readLotsByLotId = ReadLotsByLotId(lotRepository),
            updateLotWithNewPenIdUC = UpdateLotWithNewPenIdUC(lotRepository, lotRepositoryRemote, context),
            updateLotWithLotId = UpdateLotWithLotId(lotRepository, lotRepositoryRemote, context)
        )
    }

    @ViewModelScoped
    @Provides
    fun provideDrugUseCases(
        drugRepository: DrugRepository,
        drugRepositoryRemote: DrugRepositoryRemote,
        getCloudDatabaseId: GetCloudDatabaseId,
        context: Application
    ): DrugUseCases{
        return DrugUseCases(
            readDrugsUC = ReadDrugsUC(drugRepository),
            createDrug = CreateDrug(drugRepository, drugRepositoryRemote, getCloudDatabaseId, context),
            updateDrug = UpdateDrug(drugRepository, drugRepositoryRemote, context)
        )
    }

}