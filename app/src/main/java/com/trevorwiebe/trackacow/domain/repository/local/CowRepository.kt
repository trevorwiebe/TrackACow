package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import kotlinx.coroutines.flow.Flow

interface CowRepository {

    fun getDeadCowsByLotId(lotId: String): Flow<List<CowModel>>

    fun getCowsByLotId(lotId: String): Flow<List<CowModel>>
}