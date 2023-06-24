package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.cow.CacheCowModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import kotlinx.coroutines.flow.Flow

interface CowRepositoryRemote {

    suspend fun insertCowRemote(cowModel: CowModel)

    fun readCowByCowIdRemote(cowId: String): Flow<CowModel?>

    fun readCowsByLotId(lotId: String): Flow<List<CowModel>>

    suspend fun deleteCowRemote(cowModel: CowModel)

    suspend fun updateCowWithNewLotIdRemote(lotIdToSave: String, lotIdToDeleteList: List<String>)

    suspend fun insertCacheCowsRemote(cacheCowModelList: List<CacheCowModel>)
}