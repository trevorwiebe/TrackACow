package com.trevorwiebe.trackacow.domain.di

import com.trevorwiebe.trackacow.domain.repository.RationsRepository
import com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases.AddRationUC
import com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases.RationUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object TrackACowDomainModule {

}