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

data class ReadLots(
        private var lotRepository: LotRepository,
        private val lotRepositoryRemote: LotRepositoryRemote,
        private val context: Application
){
    @OptIn(FlowPreview::class)
    operator fun invoke(): Flow<List<LotModel>> {

        val localLotFlow = lotRepository.readLots()
        val cloudLotFlow = lotRepositoryRemote.readLots()

        return if (Utility.haveNetworkConnection(context)) {
            localLotFlow.flatMapConcat { localData ->
                cloudLotFlow.onStart {
                    emit(localData)
                }.map { lotList ->
                    lotRepository.insertOrUpdateLotList(lotList)
                    lotList
                }
            }
        } else {
            localLotFlow
        }
    }
}