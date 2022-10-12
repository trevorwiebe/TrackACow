package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.entities.CallEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheCallEntity
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.call.HoldingCallModel

fun CallModel.toCallEntity(): CallEntity {
    return CallEntity(
        callPrimaryKey = callPrimaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        callRationId = callRationId,
        callCloudDatabaseId = callCloudDatabaseId
    )
}

fun CallEntity.toCallModel(): CallModel {
    return CallModel(
        callPrimaryKey = callPrimaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        callRationId = callRationId,
        callCloudDatabaseId = callCloudDatabaseId
    )
}

fun CacheCallEntity.toHoldingCallModel(): HoldingCallModel{
    return HoldingCallModel(
        callPrimaryKey = callPrimaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        callRationId = callRationId,
        callCloudDatabaseId = callCloudDatabaseId,
        whatHappened = whatHappened
    )
}

fun HoldingCallModel.toHoldingCallEntity(): CacheCallEntity {
    return CacheCallEntity(
        callPrimaryKey = callPrimaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        callRationId = callRationId,
        callCloudDatabaseId = callCloudDatabaseId,
        whatHappened = whatHappened
    )
}

fun CallModel.toHoldingCallModel(whatHappened: Int): HoldingCallModel{
    return HoldingCallModel(
        callPrimaryKey = callPrimaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        callRationId = callRationId,
        callCloudDatabaseId = callCloudDatabaseId,
        whatHappened = whatHappened
    )
}