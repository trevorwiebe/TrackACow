package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.cow.CacheCowModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel

interface CowRepositoryRemote {

    suspend fun insertCowRemote(cowModel: CowModel)

    suspend fun deleteCowRemote(cowModel: CowModel)

    suspend fun updateCowWithNewLotIdRemote(lotIdToSave: String, lotIdToDeleteList: List<String>)

    suspend fun insertCacheCowsRemote(cacheCowModelList: List<CacheCowModel>)
}