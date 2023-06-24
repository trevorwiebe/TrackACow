package com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases

import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CowRepositoryRemote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import javax.inject.Provider

class ReadCowByCowId(
    private val cowRepository: CowRepository,
    private val cowRepositoryRemote: CowRepositoryRemote
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(cowId: String): Flow<CowModel?> {
        val localFlow = cowRepository.getCowByCowId(cowId)
        val cowCloudFlow = cowRepositoryRemote.readCowByCowIdRemote(cowId)

        return localFlow
            .flatMapLatest { localData ->
                cowCloudFlow.onStart {
                    if (localData != null) {
                        emit(localData)
                    }
                }
            }
    }
}