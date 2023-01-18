package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.ration.RationModel

interface RationRepositoryRemote {
    suspend fun insertOrUpdateRationRemote(rationModel: RationModel)

    suspend fun insertRationListRemote(rationModelList: List<RationModel>)

    suspend fun deleteRationRemote(rationModel: RationModel)
}