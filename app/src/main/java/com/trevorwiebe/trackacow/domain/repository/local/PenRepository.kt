package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import kotlinx.coroutines.flow.Flow

interface PenRepository {

    suspend fun insertPen(penModel: PenModel)

    fun readPens(): Flow<List<PenModel>>

    fun readPensByPenId(penId: String): Flow<PenModel?>
}