package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.cow.CacheCowModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import kotlinx.coroutines.flow.Flow

interface CowRepository {

    suspend fun insertCow(cowModel: CowModel): Long

    fun getDeadCowsByLotId(lotId: String): Flow<List<CowModel>>

    fun getCowsByLotId(lotId: String): Flow<List<CowModel>>

    suspend fun updateCow(cowModel: CowModel)

    suspend fun deleteCow(cowModel: CowModel)

    suspend fun deleteAllCows()

    suspend fun insertCacheCow(cacheCowModel: CacheCowModel)
}