package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.pen.CachePenModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel

interface PenRepositoryRemote {

    // Use this for insert and update
    suspend fun insertAndUpdatePenRemote(penModel: PenModel)

    suspend fun insertCachePenRemote(penList: List<CachePenModel>)

    suspend fun deletePenRemote(penModel: PenModel)

}