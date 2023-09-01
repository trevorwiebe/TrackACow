package com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CowRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.SourceIdentifiedListFlow
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadDeadCowsByLotId(
        private val cowRepository: CowRepository,
        private val cowRepositoryRemote: CowRepositoryRemote,
        private val context: Application
){

    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String): SourceIdentifiedListFlow {
        val localCowFlow = cowRepository.getDeadCowsByLotId(lotId)
            .map { cowList -> cowList to DataSource.Local }
        val cowCloudFlow = cowRepositoryRemote.readCowsByLotId(lotId)
            .map { cowList -> cowList to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)

        val resultsFlow = if (isFetchingFromCloud) {
            localCowFlow.flatMapConcat { (localData, fromLocalSource) ->
                cowCloudFlow.onStart {
                    emit(localData to fromLocalSource)
                }.map { (cowModelList, fromCloudSource) ->
                    if (fromCloudSource == DataSource.Cloud) {
                        cowRepository.syncCloudCowsByLotId(cowModelList, lotId)
                    }
                    val cowList = cowModelList.filter {
                        it.alive == 0
                    }
                    cowList to fromCloudSource
                }
            }
        } else {
            localCowFlow
        }

        return SourceIdentifiedListFlow(resultsFlow, isFetchingFromCloud)
    }
}
