package com.trevorwiebe.trackacow.domain.repository

import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.call.HoldingCallModel
import kotlinx.coroutines.flow.Flow

interface CallRepository {

    suspend fun insertCall(callModel: CallModel)

    fun getCalls(): Flow<List<CallModel>>

    fun getCallsByLotIdAndDate(lotId: String, date: Long): Flow<CallModel>

    fun getCallsByLotId(lotId: String): Flow<List<CallModel>>

    suspend fun updateCall(callModel: CallModel)

    suspend fun deleteCall(callModel: CallModel)

    suspend fun insertHoldingCall(holdingCallModel: HoldingCallModel)
}