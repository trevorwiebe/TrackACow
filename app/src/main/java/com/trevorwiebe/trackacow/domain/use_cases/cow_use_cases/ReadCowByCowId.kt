package com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CowRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.SourceIdentifiedSingleFlow
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ReadCowByCowId(
        private val cowRepository: CowRepository,
        private val cowRepositoryRemote: CowRepositoryRemote,
        private val context: Application
) {

    @OptIn(FlowPreview::class)
    operator fun invoke(cowId: String): SourceIdentifiedSingleFlow {
        val localFlow = cowRepository.getCowByCowId(cowId)
            .map { cow -> cow to DataSource.Local }
        val cowCloudFlow = cowRepositoryRemote.readCowByCowIdRemote(cowId)
            .map { cow -> cow to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)

        val resultsFlow = if (isFetchingFromCloud) {
            localFlow.flatMapConcat { (localData, fromLocalSource) ->
                cowCloudFlow.onStart {
                    emit(localData to fromLocalSource)
                }.map { (cowModel, fromCloudSource) ->
                    if (cowModel != null && fromCloudSource == DataSource.Cloud) {
                        val cowList = listOf(cowModel)
                        cowRepository.syncCloudCowsByCowId(cowList, cowId)
                    }
                    cowModel to fromCloudSource
                }
            }
        } else {
            localFlow
        }

        return SourceIdentifiedSingleFlow(resultsFlow, isFetchingFromCloud)
    }
}