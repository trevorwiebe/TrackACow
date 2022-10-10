package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.pen.PenModel

interface PenRepositoryRemote {

    suspend fun insertPenRemote(penModel: PenModel)

}