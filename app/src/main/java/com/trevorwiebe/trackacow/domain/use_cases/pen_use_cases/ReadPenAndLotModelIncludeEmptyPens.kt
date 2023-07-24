package com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toPenAndLotModel
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.local.PenRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart


class ReadPenAndLotModelIncludeEmptyPens(
        private val penRepository: PenRepository,
        private val lotRepository: LotRepository,
        private val lotRepositoryRemote: LotRepositoryRemote,
        private val context: Application
) {
    @OptIn(FlowPreview::class)
    operator fun invoke(): Flow<List<PenAndLotModel>> {

        val localFlow = penRepository.readPensAndLotsIncludeEmptyPens()
        val cloudFlow = lotRepositoryRemote.readPenAndLotsIncludeEmptyPens()

        return if (Utility.haveNetworkConnection(context)) {
            localFlow.flatMapConcat { localData ->
                cloudFlow.flatMapConcat { pair ->
                    penRepository.insertOrUpdatePenList(pair.first)
                    lotRepository.insertOrUpdateLotList(pair.second)
                    flow {
                        val combinedList = combineList(pair.first, pair.second)
                        emit(combinedList)
                    }
                }.onStart { emit(localData) }
            }
        } else {
            localFlow
        }
    }

    private fun combineList(
        penList: List<PenModel>,
        lotList: List<LotModel>
    ): List<PenAndLotModel> {
        val result = mutableListOf<PenAndLotModel>()
        penList.forEach { penModel ->
            val lot = lotList.find { it.lotPenCloudDatabaseId == penModel.penCloudDatabaseId }
            val penAndLotModel = penModel.toPenAndLotModel(lot)
            result.add(penAndLotModel)
        }
        return result
    }
}