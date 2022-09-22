package com.trevorwiebe.trackacow.domain.di

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.RationsRepository
import com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object TrackACowDomainModule {

    @ViewModelScoped
    @Provides
    fun provideRationUseCases(
        rationsRepository: RationsRepository,
        context: Application
    ): RationUseCases {
        return RationUseCases(
            addRationUC = AddRationUC(rationsRepository, context),
            getAllRationsUC = GetAllRationsUC(rationsRepository),
            editRationUC = EditRationUC(rationsRepository),
            deleteRationByIdUC = DeleteRationByIdUC(rationsRepository)
        )
    }
}