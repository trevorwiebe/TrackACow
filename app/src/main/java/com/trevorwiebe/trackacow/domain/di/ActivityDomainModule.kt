package com.trevorwiebe.trackacow.domain.di

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.*
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.PenRepositoryRemote
import com.trevorwiebe.trackacow.domain.repository.remote.RationRepositoryRemote
import com.trevorwiebe.trackacow.domain.use_cases.call_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.FeedUseCases
import com.trevorwiebe.trackacow.domain.use_cases.feed_use_cases.ReadFeedsByLotId
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.LotUseCases
import com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases.ReadLotsByPenId
import com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases.*
import com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ActivityComponent::class)
object ActivityDomainModule {

    @ActivityScoped
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

    @ActivityScoped
    @Provides
    fun provideLotUseCases(
        lotRepository: LotRepository
    ): LotUseCases{
        return LotUseCases(
            readLotsByPenId = ReadLotsByPenId(lotRepository)
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
}