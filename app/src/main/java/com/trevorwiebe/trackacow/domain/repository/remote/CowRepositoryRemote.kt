package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.cow.CowModel

interface CowRepositoryRemote {

    suspend fun insertCowRemote(cowModel: CowModel)

    suspend fun deleteCowRemote(cowModel: CowModel)

}