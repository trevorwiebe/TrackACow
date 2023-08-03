package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.IdentifiedFlowReturn
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadLots(
        private var lotRepository: LotRepository,
        private val lotRepositoryRemote: LotRepositoryRemote,
        private val context: Application
){
    @OptIn(FlowPreview::class)
    operator fun invoke(): IdentifiedFlowReturn {

        val localLotFlow = lotRepository.readLots()
            .map { lotList -> lotList to DataSource.Local }
        val cloudLotFlow = lotRepositoryRemote.readLots()
            .map { lotList -> lotList to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)

        val resultFlow = if (isFetchingFromCloud) {
            localLotFlow.flatMapConcat { (localData, _) ->
                cloudLotFlow.onStart {
                    emit(localData to DataSource.Cloud)
                }.map { (lotList, _) ->
                    lotRepository.insertOrUpdateLotList(lotList)
                    lotList to DataSource.Cloud
                }
            }
        } else {
            localLotFlow
        }

        return IdentifiedFlowReturn(resultFlow, isFetchingFromCloud)
    }
}