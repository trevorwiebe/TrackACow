package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.call.CallModel

interface CallRepositoryRemote {
    suspend fun insertCallRemote(callModel: CallModel)

    suspend fun insertCallListRemote(callModelList: List<CallModel>)
}