package com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toPenAndLotModel
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.local.PenRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.SourceIdentifiedListFlow
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ReadPenAndLotModelExcludeEmptyPens(
        private val penRepository: PenRepository,
        private val lotRepository: LotRepository,
        private val lotRepositoryRemote: LotRepositoryRemote,
        private val context: Application
) {

    @OptIn(FlowPreview::class)
    operator fun invoke(): SourceIdentifiedListFlow {

        val localFlow = penRepository.readPensAndLotsExcludeEmptyPens()
            .map { pen -> pen to DataSource.Local }
        val cloudFlow = lotRepositoryRemote.readPenAndLotsIncludeEmptyPens()
            .map { pen -> pen to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)

        val flowResult = if (isFetchingFromCloud) {
            localFlow.flatMapConcat { (localData, fromLocalSource) ->
                cloudFlow.flatMapConcat { (pair, fromCloudSource) ->
                    penRepository.syncCloudPens(pair.first)
                    lotRepository.syncCloudLots(pair.second)
                    flow {
                        val combinedList = combineList(pair.first, pair.second)
                        emit(combinedList to fromCloudSource)
                    }
                }.onStart { emit(localData to fromLocalSource) }
            }
        } else {
            localFlow
        }

        return SourceIdentifiedListFlow(flowResult, isFetchingFromCloud)
    }

    private fun combineList(
        penList: List<PenModel>,
        lotList: List<LotModel>
    ): List<PenAndLotModel> {
        // filtering out empty pens from firebase query
        val result = mutableListOf<PenAndLotModel>()
        lotList.forEach { lotModel ->
            val pen = penList.find {
                it.penCloudDatabaseId == lotModel.lotPenCloudDatabaseId
            }
            if (pen != null) {
                val penAndLotModel = pen.toPenAndLotModel(lotModel)
                result.add(penAndLotModel)
            }
        }
//        result.sortWith(object : Comparator<PenAndLotModel> {
//            override fun compare(p0: PenAndLotModel, p1: PenAndLotModel): Int {
//                if (p0.penName > p1.penName) {
//                    return 1
//                }
//                if (p0.penName == p1.penName) {
//                    return 0
//                }
//                return -1
//            }
//        })
        return result
    }
}