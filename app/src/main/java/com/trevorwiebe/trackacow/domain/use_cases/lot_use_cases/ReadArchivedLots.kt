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

data class ReadArchivedLots(
        private var lotRepository: LotRepository,
        private val lotRepositoryRemote: LotRepositoryRemote,
        private val context: Application
) {

    // TODO: update this with data source identification

    @OptIn(FlowPreview::class)
    operator fun invoke(): Flow<List<LotModel>> {
        val localFlow = lotRepository.readArchivedLots()
        val cloudFlow = lotRepositoryRemote.readArchivedLots()

        return if (Utility.haveNetworkConnection(context)) {
            localFlow.flatMapConcat { localData ->
                cloudFlow.onStart {
                    emit(localData)
                }.map { lotList ->
                    lotRepository.insertOrUpdateLotList(lotList)
                    lotList
                }
            }
        } else {
            localFlow
        }
    }
}