package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart

data class ReadLotsByLotId(
    private val lotRepository: LotRepository,
    private val lotRepositoryRemote: LotRepositoryRemote
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(lotCloudDatabaseId: String): Flow<LotModel?> {

        val localLotFlow = lotRepository.readLotByLotId(lotCloudDatabaseId)
        val cloudLotFlow = lotRepositoryRemote.readLotByLotId(lotCloudDatabaseId)

        return localLotFlow
            .flatMapLatest { localData ->
                cloudLotFlow.onStart {
                    if (localData != null) {
                        emit(localData)
                    }
                }
            }
    }
}