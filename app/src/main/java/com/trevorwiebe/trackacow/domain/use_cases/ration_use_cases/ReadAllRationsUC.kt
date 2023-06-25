package com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases

import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import com.trevorwiebe.trackacow.domain.repository.remote.RationRepositoryRemote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ReadAllRationsUC(
    private val rationsRepository: RationsRepository,
    private val rationRepositoryRemote: RationRepositoryRemote
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<RationModel>> {
        val localFlow = rationsRepository.getRations()
        val rationCloudFlow = rationRepositoryRemote.readAllRations()

        return localFlow.flatMapLatest { localData ->
            rationCloudFlow.onStart {
                emit(localData)
            }.map { rationList ->
                rationsRepository.insertOrUpdateRationList(rationList)
                rationList
            }
        }
    }
}