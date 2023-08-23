package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.SourceIdentifiedSingleFlow
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadLotsByLotId(
        private val lotRepository: LotRepository,
        private val lotRepositoryRemote: LotRepositoryRemote,
        private val context: Application
) {

    @OptIn(FlowPreview::class)
    operator fun invoke(lotCloudDatabaseId: String): SourceIdentifiedSingleFlow {

        val localLotFlow = lotRepository.readLotByLotId(lotCloudDatabaseId)
            .map { lot -> lot to DataSource.Local }
        val cloudLotFlow = lotRepositoryRemote.readLotByLotId(lotCloudDatabaseId)
            .map { lot -> lot to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)

        val resultsFlow = if (isFetchingFromCloud) {
            localLotFlow.flatMapConcat { (localData, source) ->
                cloudLotFlow.onStart {
                    if (localData != null) {
                        emit(localData to source)
                    }
                }.map { (lotModel, source) ->
                    val lotList = listOf(lotModel)
                    lotRepository.syncCloudLotsByLotId(lotList, lotCloudDatabaseId)
                    lotModel to source
                }
            }
        } else {
            localLotFlow
        }

        return SourceIdentifiedSingleFlow(resultsFlow, isFetchingFromCloud)
    }
}