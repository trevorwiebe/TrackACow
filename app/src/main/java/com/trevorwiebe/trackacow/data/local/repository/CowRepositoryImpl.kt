package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.dao.CowDao
import com.trevorwiebe.trackacow.data.mapper.toCowModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class CowRepositoryImpl(
    private val cowDao: CowDao
): CowRepository{

    override fun getDeadCowsByLotId(lotId: String): Flow<List<CowModel>> {
        return cowDao.getDeadCowsByLotId(lotId).map { deadCowList ->
            deadCowList.map { it.toCowModel() }
        }
    }

    override fun getCowsByLotId(lotId: String): Flow<List<CowModel>> {
        return cowDao.getCowsByLotId(lotId).map { cowList->
            cowList.map { it.toCowModel() }
        }
    }
}
