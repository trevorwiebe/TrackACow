package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.dao.LotDao
import com.trevorwiebe.trackacow.data.mapper.toLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LotRepositoryImpl(
    private val lotDao: LotDao
): LotRepository {

    override fun readLotsByPenId(penId: String): Flow<List<LotModel>> {
        return lotDao.getLotEntitiesByPenId(penId).map { lotList ->
            lotList.map {
                it.toLotModel()
            }
        }
    }

}