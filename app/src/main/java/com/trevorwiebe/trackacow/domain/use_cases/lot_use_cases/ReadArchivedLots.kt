package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.SourceIdentifiedListFlow
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadArchivedLots(
        private var lotRepository: LotRepository,
        private val lotRepositoryRemote: LotRepositoryRemote,
        private val context: Application
) {

    @OptIn(FlowPreview::class)
    operator fun invoke(): SourceIdentifiedListFlow {
        val localFlow = lotRepository.readArchivedLots()
            .map { lotList -> lotList to DataSource.Local }
        val cloudFlow = lotRepositoryRemote.readArchivedLots()
            .map { lotList -> lotList to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)

        val resultsFlow = if (isFetchingFromCloud) {
            localFlow.flatMapConcat { (localData, source) ->
                cloudFlow.onStart {
                    emit(localData to source)
                }.map { (lotList, source) ->
                    lotRepository.syncCloudLots(lotList)
                    val mutableList = lotList.toMutableList()
                    mutableList.sortWith(object : Comparator<LotModel> {
                        override fun compare(p0: LotModel, p1: LotModel): Int {
                            if (p0.dateArchived < p1.dateArchived) {
                                return 1
                            }
                            if (p0.dateArchived == p1.dateArchived) {
                                return 0
                            }
                            return -1
                        }
                    })
                    mutableList.toList() to source
                }
            }
        } else {
            localFlow
        }

        return SourceIdentifiedListFlow(resultsFlow, isFetchingFromCloud)
    }
}