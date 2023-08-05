package com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CowRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.IdentifiedFlowReturn
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ReadCowsByLotId(
        private val cowRepository: CowRepository,
        private val cowRepositoryRemote: CowRepositoryRemote,
        private val context: Application
) {

    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String): IdentifiedFlowReturn {

        val localFlow = cowRepository.getCowsByLotId(lotId)
            .map { cowList -> cowList to DataSource.Local }
        val cowCloudFlow = cowRepositoryRemote.readCowsByLotId(lotId)
            .map { cowList -> cowList to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)

        val resultsFlow = if (isFetchingFromCloud) {
            localFlow.flatMapConcat { (localData, source) ->
                cowCloudFlow.onStart {
                    emit(localData to source)
                }.map { (cowModelList, source) ->
                    cowRepository.insertOrUpdateCowList(cowModelList)
                    cowModelList to source
                }
            }
        } else {
            localFlow
        }

        return IdentifiedFlowReturn(resultsFlow, isFetchingFromCloud)
    }
}
