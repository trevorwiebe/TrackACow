package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.dao.CallDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheCallDao
import com.trevorwiebe.trackacow.data.mapper.compound_mapper.toCallAndRationModel
import com.trevorwiebe.trackacow.data.mapper.toCallEntity
import com.trevorwiebe.trackacow.data.mapper.toCallModel
import com.trevorwiebe.trackacow.data.mapper.toHoldingCallEntity
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.call.CacheCallModel
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CallRepositoryImpl(
    private val callDao: CallDao,
    private val cacheCallDao: CacheCallDao
): CallRepository {

    override suspend fun insertCall(callModel: CallModel): Long {
        return callDao.insertCall(callModel.toCallEntity())
    }

    override fun getCalls(): Flow<List<CallModel>> {
        return callDao.getCalls().map { callEntityList ->
            callEntityList.map {
                it.toCallModel()
            }
        }
    }

    override fun getCallsByLotIdAndDate(lotId: String, date: Long): Flow<CallModel?> {
        return callDao.getCallByDateAndLotId(date, lotId)
            .map {
                it?.toCallModel()
            }
    }

    override fun getCallsAndRationByLotId(lotId: String): Flow<List<CallAndRationModel>> {
        return callDao.getCallAndRationByLotId(lotId)
            .map { callAndRationList ->
                callAndRationList.map { it.toCallAndRationModel() }
            }
    }


    override suspend fun updateCall(callModel: CallModel) {
        callDao.updateCallAmount(callModel.callAmount, callModel.callPrimaryKey)
    }

    override suspend fun deleteCall(callModel: CallModel) {
        callDao.deleteCall(callModel.toCallEntity())
    }

    override suspend fun insertHoldingCall(cacheCallModel: CacheCallModel) {
        cacheCallDao.insertHoldingCall(cacheCallModel.toHoldingCallEntity())
    }
}