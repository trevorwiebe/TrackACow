package com.trevorwiebe.trackacow.domain.use_cases.load_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.LoadRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LoadRemoteRepository
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.SourceIdentifiedListFlow
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadLoadsByLotId(
        private val loadRepository: LoadRepository,
        private val loadRemoteRepository: LoadRemoteRepository,
        private val context: Application
){

    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String): SourceIdentifiedListFlow {

        val localFlow = loadRepository.readLoadsByLotId(lotId)
            .map { loadList -> loadList to DataSource.Local }
        val cloudLoadFlow = loadRemoteRepository.readLoadsByLotId(lotId)
            .map { loadList -> loadList to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)

        val resultsFlow = if (isFetchingFromCloud) {
            localFlow.flatMapConcat { (localData, fromLocalSource) ->
                cloudLoadFlow.onStart {
                    emit(localData to fromLocalSource)
                }.map { (loadList, fromCloudSource) ->
                    if (fromCloudSource == DataSource.Cloud) {
                        loadRepository.syncCloudLoadByLotId(loadList, lotId)
                    }
                    loadList to fromCloudSource
                }
            }
        } else {
            localFlow
        }

        return SourceIdentifiedListFlow(resultsFlow, isFetchingFromCloud)
    }
}
