package com.trevorwiebe.trackacow.domain.di

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.local.PenRepository
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.PenRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.RationRepositoryRemote
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.ReadLotsByPenId
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.UpdateLotWithNewPenIdUC
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
        context: Application
    ): RationUseCases {
        return RationUseCases(
            createRationUC = CreateRationUC(rationsRepository, rationsRepositoryRemote, context),
            readAllRationsUC = ReadAllRationsUC(rationsRepository),
            updateRationUC = UpdateRationUC(rationsRepository, rationsRepositoryRemote, context),
            deleteRationByIdUC = DeleteRationByIdUC(rationsRepository, context)
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
        context: Application
    ): PenUseCases {
        return PenUseCases(
            readPens = ReadPens(penRepository),
            readPenByPenId = ReadPenByPenId(penRepository),
            readPenAndLotModelUC = ReadPenAndLotModelUC(penRepository),
            createPenUC = CreatePenUC(penRepository, penRepositoryRemote, context),
            deletePenUC = DeletePenUC(penRepository)
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
            updateLotWithNewPenIdUC = UpdateLotWithNewPenIdUC(lotRepository, lotRepositoryRemote, context)
        )
    }
}