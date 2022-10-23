package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import kotlinx.coroutines.flow.Flow

interface LoadRepository {

    fun readLoadsByLotId(lotId: String): Flow<List<LoadModel>>
}