package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadLotsByLotId(
        private val lotRepository: LotRepository,
        private val lotRepositoryRemote: LotRepositoryRemote,
        private val context: Application
) {

    @OptIn(FlowPreview::class)
    operator fun invoke(lotCloudDatabaseId: String): Flow<LotModel?> {

        val localLotFlow = lotRepository.readLotByLotId(lotCloudDatabaseId)
        val cloudLotFlow = lotRepositoryRemote.readLotByLotId(lotCloudDatabaseId)

        return if (Utility.haveNetworkConnection(context)) {
            localLotFlow.flatMapConcat { localData ->
                cloudLotFlow.onStart {
                    if (localData != null) {
                        emit(localData)
                    }
                }.map { lotModel ->
                    val lotList = listOf(lotModel)
                    lotRepository.insertOrUpdateLotList(lotList)
                    lotModel
                }
            }
        } else {
            localLotFlow
        }
    }
}