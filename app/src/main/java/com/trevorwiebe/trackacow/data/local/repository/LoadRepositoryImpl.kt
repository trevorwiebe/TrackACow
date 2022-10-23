package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.dao.LoadDao
import com.trevorwiebe.trackacow.data.mapper.toLoadModel
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.repository.local.LoadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoadRepositoryImpl(
    private val loadDao: LoadDao
): LoadRepository {

    override fun readLoadsByLotId(lotId: String): Flow<List<LoadModel>> {
        return loadDao.readLoadsByLotId(lotId).map { loadList ->
            loadList.map { it.toLoadModel() }
        }
    }

}