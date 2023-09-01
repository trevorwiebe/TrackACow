package com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import com.trevorwiebe.trackacow.domain.repository.remote.RationRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.SourceIdentifiedListFlow
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ReadAllRationsUC(
        private val rationsRepository: RationsRepository,
        private val rationRepositoryRemote: RationRepositoryRemote,
        private val context: Application
) {

    @OptIn(FlowPreview::class)
    operator fun invoke(): SourceIdentifiedListFlow {
        val localFlow = rationsRepository.getRations()
            .map { rationList -> rationList to DataSource.Local }
        val rationCloudFlow = rationRepositoryRemote.readAllRations()
            .map { rationList -> rationList to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)

        val resultsFlow = if (isFetchingFromCloud) {
            localFlow.flatMapConcat { (localData, fromLocalSource) ->
                rationCloudFlow.onStart {
                    emit(localData to fromLocalSource)
                }.map { (rationList, fromCloudSource) ->
                    if (fromCloudSource == DataSource.Cloud) {
                        rationsRepository.syncCloudRationListToDatabase(rationList)
                    }
                    rationList to fromCloudSource
                }
            }
        } else {
            localFlow
        }

        return SourceIdentifiedListFlow(resultsFlow, isFetchingFromCloud)
    }
}