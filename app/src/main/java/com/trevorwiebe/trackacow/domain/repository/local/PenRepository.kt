package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.pen.CachePenModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import kotlinx.coroutines.flow.Flow

interface PenRepository {

    suspend fun insertPen(penModel: PenModel): Long

    fun readPens(): Flow<List<PenModel>>

    fun readPensAndLotsIncludeEmptyPens(): Flow<List<PenAndLotModel>>

    fun readPensAndLotsExcludeEmptyPens(): Flow<List<PenAndLotModel>>

    fun readPensByPenId(penId: String): Flow<PenModel?>

    suspend fun deletePen(penModel: PenModel)

    suspend fun updatePen(penModel: PenModel)

    suspend fun deleteAllPens()

    suspend fun insertOrUpdatePenList(penList: List<PenModel>)

    // cache functions
    suspend fun insertCachePen(cachePenModel: CachePenModel)

    suspend fun getCachePens(): List<CachePenModel>

    suspend fun deleteCachePens()
}