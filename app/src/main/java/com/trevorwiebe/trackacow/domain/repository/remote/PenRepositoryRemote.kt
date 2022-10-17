package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.pen.PenModel

interface PenRepositoryRemote {

    // Use this for insert and update
    suspend fun insertPenRemote(penModel: PenModel)

    suspend fun deletePenRemote(penModel: PenModel)

}