package com.trevorwiebe.trackacow.domain.di

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.local.PenRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.ReadLotsByPenId
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.PenUseCases
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.ReadPenByPenId
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.ReadPens
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
    fun provideCallUseCases(
        callRepository: CallRepository,
        callRepositoryRemote: CallRepositoryRemote,
        context: Application
    ): CallUseCases {
        return CallUseCases(
            readCallsByLotIdAndDateUC = ReadCallByLotIdAndDateUC(callRepository),
            readCallsByLotId = ReadCallsByLotIdUC(callRepository),
            createCallUC = CreateCallUC(callRepository, callRepositoryRemote, context),
            updateCallUC = UpdateCallUC(callRepository, callRepositoryRemote, context)
        )
    }

    @ActivityScoped
    @Provides
    fun providePenUseCases(
        penRepository: PenRepository
    ): PenUseCases {
        return PenUseCases(
            readPens = ReadPens(penRepository),
            readPenByPenId = ReadPenByPenId(penRepository)
        )
    }

    @ActivityScoped
    @Provides
    fun provideLotUseCases(
        lotRepository: LotRepository
    ): LotUseCases{
        return LotUseCases(
            readLotsByPenId = ReadLotsByPenId(lotRepository)
        )
    }
}