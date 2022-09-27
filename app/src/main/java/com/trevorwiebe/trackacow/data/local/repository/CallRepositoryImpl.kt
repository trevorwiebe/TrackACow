package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.dao.CallDao
import com.trevorwiebe.trackacow.data.local.holdingDao.HoldingCallDao
import com.trevorwiebe.trackacow.data.mapper.toCallEntity
import com.trevorwiebe.trackacow.data.mapper.toCallModel
import com.trevorwiebe.trackacow.data.mapper.toHoldingCallEntity
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.call.HoldingCallModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CallRepositoryImpl(
    private val callDao: CallDao,
    private val holdingCallDao: HoldingCallDao
): CallRepository {

    override suspend fun insertCall(callModel: CallModel) {
        callDao.insertCall(callModel.toCallEntity())
    }

    override fun getCalls(): Flow<List<CallModel>> {
        return callDao.getCalls().map { callEntityList ->
            callEntityList.map {
                it.toCallModel()
            }
        }
    }

    override fun getCallsByLotIdAndDate(lotId: String, date: Long): Flow<CallModel> {
        return callDao.getCallByDateAndLotId(date, lotId)
            .map { it.toCallModel() }
    }

    override fun getCallsByLotId(lotId: String): Flow<List<CallModel>> {
        return callDao.getCallByLotId(lotId).map { list ->
            list.map { it.toCallModel() }
        }
    }

    override suspend fun updateCall(callModel: CallModel) {
        callDao.updateCall(callModel.toCallEntity())
    }

    override suspend fun deleteCall(callModel: CallModel) {
        callDao.deleteCall(callModel.toCallEntity())
    }

    override suspend fun insertHoldingCall(holdingCallModel: HoldingCallModel) {
        holdingCallDao.insertHoldingCall(holdingCallModel.toHoldingCallEntity())
    }
}