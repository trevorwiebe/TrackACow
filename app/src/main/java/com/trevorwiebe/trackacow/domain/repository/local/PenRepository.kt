package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.pen.CachePenModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import kotlinx.coroutines.flow.Flow

interface PenRepository {

    suspend fun insertPen(penModel: PenModel): Long

    suspend fun insertCachePen(cachePenModel: CachePenModel)

    fun readPens(): Flow<List<PenModel>>

    fun readPensAndLots(): Flow<List<PenAndLotModel>>

    fun readPensByPenId(penId: String): Flow<PenModel?>

    suspend fun deletePen(penModel: PenModel)
}