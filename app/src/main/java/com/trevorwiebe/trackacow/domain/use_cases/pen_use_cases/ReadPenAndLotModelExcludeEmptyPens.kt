package com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.data.mapper.toPenAndLotModel
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.repository.local.PenRepository
import com.trevorwiebe.trackacow.domain.utils.combineQueryDatabaseNodes
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class ReadPenAndLotModelExcludeEmptyPens(
    private val penRepository: PenRepository,
    private val firebaseDatabase: FirebaseDatabase,
    private val penDatabaseString: String,
    private val lotDatabaseString: String
) {

    @OptIn(FlowPreview::class)
    operator fun invoke(): Flow<List<PenAndLotModel>> {

        val localFlow = penRepository.readPensAndLotsExcludeEmptyPens()

        val penRef = firebaseDatabase.getReference(penDatabaseString)
        val lotRef = firebaseDatabase
            .getReference(lotDatabaseString)
            .orderByChild("archived")
            .equalTo(0.toDouble())

        return localFlow
            .flatMapConcat { localData ->
                combineQueryDatabaseNodes(
                    penRef,
                    lotRef,
                    PenModel::class.java,
                    LotModel::class.java,
                ).flatMapConcat { pair ->
                    penRepository.insertOrUpdatePenList(pair.first)
                    flow {
                        val combinedList = combineList(pair.first, pair.second)
                        emit(combinedList)
                    }
                }.onStart { emit(localData) }
            }
    }

    private fun combineList(
        penList: List<PenModel>,
        lotList: List<LotModel>
    ): List<PenAndLotModel> {
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
        result.sortWith(object : Comparator<PenAndLotModel> {
            override fun compare(p0: PenAndLotModel, p1: PenAndLotModel): Int {
                if (p0.penName.toInt() > p1.penName.toInt()) {
                    return 1
                }
                if (p0.penName.toInt() == p1.penName.toInt()) {
                    return 0
                }
                return -1
            }
        })
        return result
    }
}