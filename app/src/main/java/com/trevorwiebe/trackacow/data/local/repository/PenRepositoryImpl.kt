package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.dao.PenDao
import com.trevorwiebe.trackacow.data.mapper.compound_mapper.toPenAndLotModel
import com.trevorwiebe.trackacow.data.mapper.toPenEntity
import com.trevorwiebe.trackacow.data.mapper.toPenModel
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.repository.local.PenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PenRepositoryImpl(
    val penDao: PenDao
): PenRepository {

    override suspend fun insertPen(penModel: PenModel) {
        return penDao.insertPen(penModel.toPenEntity())
    }

    override fun readPens(): Flow<List<PenModel>> {
        return penDao.getPenList().map { returnedPenList ->
                returnedPenList.map {
                    it.toPenModel()
                }
            }
    }

    override fun readPensAndLots(): Flow<List<PenAndLotModel>> {
        return penDao.getPenAndLotList().map { penAndLotList ->
            penAndLotList.map { it.toPenAndLotModel() }
        }
    }

    override fun readPensByPenId(penId: String): Flow<PenModel?> {
        return penDao.getPenById(penId).map {
            it?.toPenModel()
        }
    }
}